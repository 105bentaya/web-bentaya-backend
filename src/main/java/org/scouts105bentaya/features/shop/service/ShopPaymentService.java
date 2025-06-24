package org.scouts105bentaya.features.shop.service;

import jakarta.mail.util.ByteArrayDataSource;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.payment.Payment;
import org.scouts105bentaya.features.payment.PaymentService;
import org.scouts105bentaya.features.payment.PaymentTypeEnum;
import org.scouts105bentaya.features.payment.dto.PaymentFormDataRequestDto;
import org.scouts105bentaya.features.payment.dto.PaymentInfoDto;
import org.scouts105bentaya.features.payment.dto.PaymentRedsysFormDataDto;
import org.scouts105bentaya.features.payment.dto.PaymentUrlsDto;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.features.shop.PaymentStatus;
import org.scouts105bentaya.features.shop.PdfInvoiceGenerator;
import org.scouts105bentaya.features.shop.dto.form.ShopPurchaseInformationFormDto;
import org.scouts105bentaya.features.shop.entity.BoughtProduct;
import org.scouts105bentaya.features.shop.entity.CartProduct;
import org.scouts105bentaya.features.shop.entity.ProductSize;
import org.scouts105bentaya.features.shop.entity.ShopPurchase;
import org.scouts105bentaya.features.shop.repository.ProductSizeRepository;
import org.scouts105bentaya.features.shop.repository.ShopPurchaseRepository;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.UserService;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.service.EmailService;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ShopPaymentService {
    private final AuthService authService;
    private final ShopPurchaseRepository shopPurchaseRepository;
    private final ProductSizeRepository productSizeRepository;
    private final CartProductService cartProductService;
    private final EmailService emailService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final UserPurchaseStatusService userPurchaseStatusService;
    private final TemplateEngine templateEngine;
    private final PdfInvoiceGenerator pdfInvoiceGenerator;

    public ShopPaymentService(
        AuthService authService,
        ShopPurchaseRepository shopPurchaseRepository,
        ProductSizeRepository productRepository,
        CartProductService cartProductService,
        EmailService emailService,
        UserService userService,
        PaymentService paymentService,
        UserPurchaseStatusService userPurchaseStatusService,
        TemplateEngine templateEngine,
        PdfInvoiceGenerator pdfInvoiceGenerator
    ) {
        this.authService = authService;
        this.shopPurchaseRepository = shopPurchaseRepository;
        this.productSizeRepository = productRepository;
        this.cartProductService = cartProductService;
        this.emailService = emailService;
        this.userService = userService;
        this.paymentService = paymentService;
        this.userPurchaseStatusService = userPurchaseStatusService;
        this.templateEngine = templateEngine;
        this.pdfInvoiceGenerator = pdfInvoiceGenerator;
    }

    public List<ShopPurchase> findAll() {
        return this.shopPurchaseRepository.findAll();
    }

    public ShopPurchase getStartedPurchase() {
        Optional<ShopPurchase> shopPayment = userPurchaseStatusService.getLoggedUserStartedPurchase();
        if (shopPayment.isEmpty()) {
            log.warn("getStartedPurchase - user has not any started purchase");
            throw new WebBentayaConflictException("Este usuario no tiene una compra iniciada");
        }
        return shopPayment.get();
    }

    public ShopPurchase getOngoingPurchase() {
        Optional<ShopPurchase> shopPayment = userPurchaseStatusService.getLoggedUserOngoingPurchase();
        if (shopPayment.isEmpty()) {
            log.warn("getOngoingPurchase - user has not any ongoing purchase");
            throw new WebBentayaConflictException("Este usuario no tiene una compra en marcha");
        }
        return shopPayment.get();
    }

    public synchronized void synchronizeWithPurchaseService() { //todo: maybe create lock object, this may not work
        log.info("synchronizeWithPurchaseService - synchronizing");
    }

    public synchronized PaymentRedsysFormDataDto continuePurchase(PaymentUrlsDto urls) {
        ShopPurchase ongoingPurchase = getOngoingPurchase();
        PaymentFormDataRequestDto tpvData = new PaymentFormDataRequestDto(
            ongoingPurchase.getPayment(),
            urls.okUrl(),
            urls.koUrl()
        );
        return paymentService.getPaymentAsRedsysFormData(tpvData);
    }

    @Transactional
    public synchronized void startPurchase() {
        if (userPurchaseStatusService.loggedUserHasStartedOrOngoingPurchase()) {
            log.warn("startPurchase - user has an ongoing purchase");
            throw new WebBentayaConflictException("Este usuario ya tiene una transacción en marcha");
        }

        User user = authService.getLoggedUser();
        List<CartProduct> cart = user.getItems();

        if (cart.isEmpty()) {
            log.warn("startPurchase - cart is empty");
            throw new WebBentayaConflictException("El carrito está vacío");
        }

        if (cart.stream().anyMatch(item -> item.getCount() > item.getProductSize().getStock())) {
            log.warn("startPurchase - cart product count higher than stock");
            throw new WebBentayaConflictException("Hay productos del carrito con más cantidad que en stock");
        }

        cart.forEach(item -> {
            ProductSize productSize = item.getProductSize();
            productSize.setStock(productSize.getStock() - item.getCount());
            productSizeRepository.save(productSize);
        });

        int amount = cart.stream().reduce(0, (total, item) ->
            total + item.getCount() * item.getProductSize().getProduct().getPrice(), Integer::sum
        );

        Payment payment = paymentService.createPayment(new PaymentInfoDto(amount, PaymentTypeEnum.SHOP_PURCHASE));

        ShopPurchase shopPurchase = new ShopPurchase();
        shopPurchase.setUser(user);
        shopPurchase.setBoughtProducts(createBoughtProductsList(cart, shopPurchase));
        shopPurchase.setEmail(user.getUsername());
        shopPurchase.setPayment(payment);

        user.getShopPurchases().stream()
            .filter(PaymentStatus::purchaseSuccessful)
            .max(Comparator.comparing(purchase -> purchase.getPayment().getModificationDate()))
            .ifPresent(purchase -> {
                shopPurchase.setName(purchase.getName());
                shopPurchase.setSurname(purchase.getSurname());
                shopPurchase.setPhone(purchase.getPhone());
            });

        shopPurchaseRepository.save(shopPurchase);
    }

    private List<BoughtProduct> createBoughtProductsList(List<CartProduct> cart, ShopPurchase shopPurchase) {
        return cart.stream().map(item -> {
                BoughtProduct boughtProduct = new BoughtProduct();
                boughtProduct.setProductName(item.getProductSize().getProduct().getName());
                boughtProduct.setSizeName(item.getProductSize().getSize());
                boughtProduct.setPrice(item.getProductSize().getProduct().getPrice());
                boughtProduct.setCount(item.getCount());
                boughtProduct.setShopPurchase(shopPurchase);
                return boughtProduct;
            }
        ).toList();
    }

    @Transactional
    public synchronized void cancelPurchaseByCurrentUser() {
        this.cancelUserPurchase(authService.getLoggedUser());
    }

    @Transactional
    public synchronized void cancelPurchaseByUserId(Integer userId) {
        this.cancelUserPurchase(userService.findById(userId));
    }

    private synchronized void cancelUserPurchase(User user) {
        Optional<ShopPurchase> optionalShopPurchase = userPurchaseStatusService.getUserStartedOrOngoingPurchase(user);

        if (optionalShopPurchase.isEmpty()) {
            log.warn("cancelPurchase - user has not any started purchase");
            throw new WebBentayaConflictException("Este usuario no tiene una transacción en marcha para ser cancelada");
        }

        user.getItems().forEach(item -> {
            ProductSize productSize = item.getProductSize();
            productSize.setStock(productSize.getStock() + item.getCount());
            productSizeRepository.save(productSize);
        });

        ShopPurchase purchase = optionalShopPurchase.get();
        purchase.getPayment().setStatus(PaymentStatus.CANCELED_BEFORE_PAYMENT.getCode());
        purchase.getPayment().setModificationDate(ZonedDateTime.now());
        shopPurchaseRepository.save(purchase);
    }

    @Transactional
    public synchronized void confirmPurchase(ShopPurchaseInformationFormDto purchaseInformation) {
        Optional<ShopPurchase> optionalPurchase = userPurchaseStatusService.getLoggedUserStartedPurchase();
        if (optionalPurchase.isEmpty()) {
            log.warn("METHOD confirmPurchase: user has not any started purchase");
            throw new WebBentayaConflictException("Este usuario tiene ninguna transacción en marcha");
        }

        ShopPurchase shopPurchase = optionalPurchase.get();

        shopPurchase.setPhone(purchaseInformation.phone());
        shopPurchase.setObservations(purchaseInformation.observations());
        shopPurchase.setName(purchaseInformation.name());
        shopPurchase.setSurname(purchaseInformation.surname());
        shopPurchase.setEmail(purchaseInformation.email());
        shopPurchase.getPayment().setStatus(PaymentStatus.ONGOING.getCode());

        shopPurchaseRepository.save(shopPurchase);
    }

    public void updatePaymentStatus(ShopPurchase shopPurchase) {
        if (shopPurchase == null) {
            log.error("updatePaymentStatus - shopPayment not found while handling notification");
            throw new WebBentayaNotFoundException("No se ha encontrado la transacción");
        }
        try {
            handlePayment(shopPurchase);
        } catch (Exception e) {
            log.error("updatePaymentStatus - error while updating payment status for purchase {}:", shopPurchase.getId(), e);
            this.emailService.sendSimpleEmail(
                "Error al actualizar un pedido",
                "Informática, ha ocurrido un error al actualizar el estado del pedido %s con estado %d.".formatted(shopPurchase.getPayment().getOrderNumber(), shopPurchase.getPayment().getStatus()),
                emailService.getSettingEmails(SettingEnum.ADMINISTRATION_MAIL)
            );
        }
    }

    private void handlePayment(ShopPurchase purchase) {
        Payment payment = purchase.getPayment();
        if (PaymentStatus.purchaseSuccessful(purchase)) {
            log.info("updatePaymentStatus - payment successfully processed with code {}", payment.getStatus());

            this.cartProductService.emptyCart(purchase.getUser());
            ByteArrayDataSource invoice = this.generatePurchaseInvoice(purchase);

            this.sendEmail(
                "Compra %s realizada con éxito".formatted(purchase.getPayment().getOrderNumber()),
                "shop/successful-shop-payment.html",
                invoice,
                purchase.getPayment().getOrderNumber(),
                ArrayUtils.addAll(emailService.getSettingEmails(SettingEnum.TREASURY_MAIL), emailService.getSettingEmails(SettingEnum.ADMINISTRATION_MAIL))
            );

            this.sendEmail(
                "Scouts 105 Bentaya - Recibo de la compra %s".formatted(purchase.getPayment().getOrderNumber()),
                "shop/shop-payment-invoice.html",
                invoice,
                purchase.getPayment().getOrderNumber(),
                purchase.getEmail()
            );

        } else {
            log.info("updatePaymentStatus - payment unsuccessfully processed with code {}", payment.getStatus());

            purchase.getUser().getItems().forEach(item -> {
                ProductSize productSize = item.getProductSize();
                productSize.setStock(productSize.getStock() + item.getCount());
                productSizeRepository.save(productSize);
            });

            this.emailService.sendSimpleEmail(
                "Compra finalizada con error",
                "Informática, no se ha completado la compra %s con código de error %d.".formatted(payment.getOrderNumber(), payment.getStatus()),
                emailService.getSettingEmails(SettingEnum.ADMINISTRATION_MAIL)
            );
        }
    }

    private void sendEmail(String subject, String template, ByteArrayDataSource invoice, String order, String... emails) {
        Context context = new Context();
        context.setVariable("purchaseNumber", order);
        context.setVariable("itMail", emailService.getSettingEmails(SettingEnum.ADMINISTRATION_MAIL)[0]);
        String body = this.templateEngine.process(template, context);
        this.emailService.sendSimpleEmailWithHtmlAndAttachment(subject, body, invoice, emails);
    }

    public ByteArrayDataSource generatePurchaseInvoice(ShopPurchase purchase) {
        return pdfInvoiceGenerator.generateInvoicePdf(purchase);
    }
}
