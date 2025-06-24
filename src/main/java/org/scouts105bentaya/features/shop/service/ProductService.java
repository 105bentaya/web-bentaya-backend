package org.scouts105bentaya.features.shop.service;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.shop.dto.form.ProductFormDto;
import org.scouts105bentaya.features.shop.dto.form.ProductSizeFormDto;
import org.scouts105bentaya.features.shop.entity.Product;
import org.scouts105bentaya.features.shop.entity.ProductImage;
import org.scouts105bentaya.features.shop.entity.ProductSize;
import org.scouts105bentaya.features.shop.repository.ProductImageRepository;
import org.scouts105bentaya.features.shop.repository.ProductRepository;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.util.FileTypeEnum;
import org.scouts105bentaya.shared.util.FileUtils;
import org.scouts105bentaya.shared.util.dto.FileTransferDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ShopPaymentService shopPaymentService;
    private final BlobService blobService;
    private final ProductImageRepository productImageRepository;

    public ProductService(
        ProductRepository productRepository,
        ShopPaymentService shopPaymentService,
        BlobService blobService,
        ProductImageRepository productImageRepository
    ) {
        this.productRepository = productRepository;
        this.shopPaymentService = shopPaymentService;
        this.blobService = blobService;
        this.productImageRepository = productImageRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Integer id) {
        return productRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    public ResponseEntity<byte[]> getPhoto(String uuid) {
        ProductImage file = productImageRepository.findByUuid(uuid).orElseThrow(WebBentayaNotFoundException::new);
        return new FileTransferDto(
            blobService.getBlob(uuid),
            file.getName(),
            file.getMimeType()
        ).asResponseEntity();
    }

    public Product save(ProductFormDto productDto, MultipartFile file) {
        FileUtils.validateFileType(file, FileTypeEnum.IMG_TYPE);

        ProductImage image = new ProductImage();
        image.setName(file.getOriginalFilename());
        image.setMimeType(file.getContentType());
        image.setUuid(blobService.createBlob(file));

        Product product = new Product();
        product.setName(productDto.name());
        product.setDescription(productDto.description());
        product.setImage(image);
        product.setPrice(productDto.price());
        this.setStockList(product, productDto);

        return productRepository.save(product);
    }

    private void setStockList(Product savedProduct, ProductFormDto form) {
        savedProduct.setStockList(form.stockList().stream()
            .map(stock -> this.newProductSize(stock, savedProduct))
            .collect(Collectors.toSet()));
    }

    @Transactional
    public Product update(Integer id, ProductFormDto productDto, @Nullable MultipartFile file) {
        this.shopPaymentService.synchronizeWithPurchaseService();
        Product productDB = findById(id);

        if (file != null) {
            FileUtils.validateFileType(file, FileTypeEnum.IMG_TYPE);
            ProductImage image = productDB.getImage();
            image.setName(file.getOriginalFilename());
            image.setMimeType(file.getContentType());
            blobService.deleteBlob(image.getUuid());
            image.setUuid(blobService.createBlob(file));
        }

        productDB.setName(productDto.name());
        productDB.setDescription(productDto.description());
        productDB.setPrice(productDto.price());

        this.updateStockList(productDto, productDB);

        return productRepository.save(productDB);
    }

    private void updateStockList(ProductFormDto form, Product productDB) {
        this.removeDeletedProductSizes(form, productDB);

        List<ProductSize> newContacts = new ArrayList<>();
        form.stockList().forEach(productSizeForm -> {
            if (productSizeForm.id() != null) {
                ProductSize existingProductSize = productDB.getStockList().stream()
                    .filter(productSize -> productSize.getId().equals(productSizeForm.id()))
                    .findFirst().orElseThrow(WebBentayaNotFoundException::new);
                this.updateExistingProductSize(existingProductSize, productSizeForm);
            } else {
                newContacts.add(this.newProductSize(productSizeForm, productDB));
            }
        });

        productDB.getStockList().addAll(newContacts);
    }

    private void updateExistingProductSize(ProductSize existingProductSize, ProductSizeFormDto productSizeForm) {
        this.updateProductSizeStock(existingProductSize, productSizeForm);
        existingProductSize.setSize(productSizeForm.size());
    }

    private ProductSize newProductSize(ProductSizeFormDto form, Product product) {
        ProductSize productSize = new ProductSize();
        productSize.setProduct(product);
        productSize.setSize(form.size());
        productSize.setStock(form.stock());
        return productSize;
    }

    private void removeDeletedProductSizes(ProductFormDto form, Product productDB) {
        List<ProductSize> sizesToRemove = productDB.getStockList().stream()
            .filter(stock -> form.stockList().stream()
                .noneMatch(newStock -> stock.getId().equals(newStock.id())))
            .toList();

        sizesToRemove.forEach(productDB.getStockList()::remove);
    }

    private void updateProductSizeStock(ProductSize stockDB, ProductSizeFormDto updatedStock) {
        if (stockDB.getStock() != updatedStock.originalStock()) {
            int stockDifference = stockDB.getStock() - updatedStock.originalStock();
            int newStock = updatedStock.stock() + stockDifference;
            if (newStock < 0) {
                throw new WebBentayaConflictException("El stock ha cambiado durante la actualización");
            }
            stockDB.setStock(newStock);
        } else {
            stockDB.setStock(updatedStock.stock());
        }
    }
}
