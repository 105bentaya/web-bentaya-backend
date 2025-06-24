package org.scouts105bentaya.features.shop.service;

import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.shop.dto.CartItemDto;
import org.scouts105bentaya.features.shop.dto.CartProductSizeDto;
import org.scouts105bentaya.features.shop.dto.ProductDto;
import org.scouts105bentaya.features.shop.dto.form.CartProductFormDto;
import org.scouts105bentaya.features.shop.entity.CartProduct;
import org.scouts105bentaya.features.shop.entity.Product;
import org.scouts105bentaya.features.shop.entity.ProductSize;
import org.scouts105bentaya.features.shop.repository.CartProductRepository;
import org.scouts105bentaya.features.shop.repository.ProductSizeRepository;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.shared.service.AuthService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartProductService {
    private final AuthService authService;
    private final ProductSizeRepository productSizeRepository;
    private final CartProductRepository cartProductRepository;
    private final UserPurchaseStatusService userPurchaseStatusService;

    public CartProductService(
        AuthService authService,
        ProductSizeRepository productSizeRepository,
        CartProductRepository cartProductRepository,
        UserPurchaseStatusService userPurchaseStatusService
    ) {
        this.authService = authService;
        this.productSizeRepository = productSizeRepository;
        this.cartProductRepository = cartProductRepository;
        this.userPurchaseStatusService = userPurchaseStatusService;
    }

    public String getLoggedUserCartStatus() {
        User user = authService.getLoggedUser();
        if (userPurchaseStatusService.loggedUserHasStartedPurchase()) {
            return "STARTED";
        }
        if (userPurchaseStatusService.loggedUserHasOngoingPurchase()) {
            return "ONGOING";
        }
        if (user.getItems() == null || user.getItems().isEmpty()) {
            return "0";
        }
        return user.getItems().stream().reduce(0, (tot, item) -> tot + item.getCount(), Integer::sum).toString();
    }

    public List<CartItemDto> findLoggedUserCartItems() {
        if (userPurchaseStatusService.loggedUserHasStartedOrOngoingPurchase()) {
            throw new WebBentayaConflictException("Hay una compra en marcha");
        }

        return authService.getLoggedUser().getItems().stream()
            .collect(Collectors.groupingBy(cartProduct -> cartProduct.getProductSize().getProduct()))
            .entrySet().stream()
            .sorted(Comparator.comparing(product -> product.getKey().getId()))
            .map(entry -> buildCartItem(entry.getKey(), entry.getValue()))
            .toList();
    }

    private CartItemDto buildCartItem(Product product, List<CartProduct> cartProducts) {
        ProductDto productDto = ProductDto.fromEntity(product);

        List<CartProductSizeDto> items = cartProducts.stream()
            .map(CartProductSizeDto::fromEntity)
            .toList();

        int totalPrice = cartProducts.stream()
            .mapToInt(item -> item.getProductSize().getProduct().getPrice() * item.getCount())
            .sum();

        return new CartItemDto(productDto, totalPrice, items);
    }
    public void updateProduct(CartProductFormDto form) {
        if (userPurchaseStatusService.loggedUserHasStartedOrOngoingPurchase()) {
            throw new WebBentayaConflictException("No se puede actualizar el carrito mientras hay una compra en marcha");
        }

        User user = authService.getLoggedUser();
        Optional<CartProduct> optionalCartProduct = user.getItems().stream()
            .filter(item -> item.getProductSize().getId().equals(form.productSizeId()))
            .findAny();

        if (optionalCartProduct.isPresent()) {
            CartProduct cartProductDB = optionalCartProduct.get();
            if (form.count() < 1 || cartProductDB.getProductSize().getStock() < 1) {
                cartProductRepository.delete(cartProductDB);
            } else {
                if (form.count() > cartProductDB.getProductSize().getStock()) {
                    cartProductDB.setCount(cartProductDB.getProductSize().getStock());
                } else {
                    cartProductDB.setCount(form.count());
                }
                cartProductRepository.save(cartProductDB);
            }
        } else if (form.count() > 0) {
            CartProduct cartProduct = new CartProduct();
            cartProduct.setUser(user);
            cartProduct.setProductSize(this.findProductSize(form.productSizeId()));
            int stock = cartProduct.getProductSize().getStock();
            cartProduct.setCount(form.count() > stock ? stock : form.count());
            cartProductRepository.save(cartProduct);
        }
    }

    public void emptyCart() {
        if (userPurchaseStatusService.loggedUserHasStartedOrOngoingPurchase()) {
            throw new WebBentayaConflictException("No se puede vaciar el carrito mientras hay una compra en marcha");
        }
        cartProductRepository.deleteAll(authService.getLoggedUser().getItems());
    }

    public void emptyCart(User user) {
        if (userPurchaseStatusService.userHasStartedOrOngoingPurchase(user)) {
            throw new WebBentayaConflictException("No se puede vaciar el carrito mientras hay una compra en marcha");
        }
        cartProductRepository.deleteAll(user.getItems());
    }

    private ProductSize findProductSize(int productSizeId) {
        return productSizeRepository.findById(productSizeId).orElseThrow(WebBentayaNotFoundException::new);
    }
}
