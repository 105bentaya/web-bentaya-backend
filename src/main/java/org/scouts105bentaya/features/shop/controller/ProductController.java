package org.scouts105bentaya.features.shop.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.shop.dto.ProductDto;
import org.scouts105bentaya.features.shop.dto.form.ProductFormDto;
import org.scouts105bentaya.features.shop.service.ProductService;
import org.scouts105bentaya.shared.GenericConverter;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/shop/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCOUTER', 'USER', 'TRANSACTION')")
    public List<ProductDto> findAll() {
        log.info("METHOD ProductController.findAll");
        return GenericConverter.convertEntityCollectionToDtoList(productService.findAll(), ProductDto::fromEntity);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCOUTER', 'USER', 'TRANSACTION')")
    public ProductDto findById(@PathVariable Integer id) {
        log.info("METHOD ProductController.findById");
        return ProductDto.fromEntity(productService.findById(id));
    }

    @GetMapping("/public/photo/{uuid}")
    public ResponseEntity<byte[]> getProductPhoto(@PathVariable String uuid) {
        log.info("getProductPhoto {}", uuid);
        return productService.getPhoto(uuid);
    }

    @PreAuthorize("hasRole('TRANSACTION')")
    @PostMapping
    public ProductDto save(
        @RequestPart("form") @Valid ProductFormDto form,
        @RequestPart("file") MultipartFile file
    ) {
        log.info("METHOD ProductController.save{}", SecurityUtils.getLoggedUserUsernameForLog());
        return ProductDto.fromEntity(productService.save(form, file));
    }

    @PreAuthorize("hasRole('TRANSACTION')")
    @PutMapping("/{productId}")
    public ProductDto update(
        @PathVariable Integer productId,
        @RequestPart("form") @Valid ProductFormDto form,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        log.info("METHOD ProductController.update{}", SecurityUtils.getLoggedUserUsernameForLog());
        return ProductDto.fromEntity(productService.update(productId, form, file));
    }
}
