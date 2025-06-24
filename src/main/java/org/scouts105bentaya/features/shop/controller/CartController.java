package org.scouts105bentaya.features.shop.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.shop.dto.CartItemDto;
import org.scouts105bentaya.features.shop.dto.form.CartProductFormDto;
import org.scouts105bentaya.features.shop.service.CartProductService;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/shop/cart")
@PreAuthorize("hasAnyRole('SCOUTER', 'USER')")
public class CartController {
    private final CartProductService cartProductService;

    public CartController(CartProductService cartProductService) {
        this.cartProductService = cartProductService;
    }

    @GetMapping
    public List<CartItemDto> findAllByLoggedUser() {
        log.info("findAllByLoggedUser{}", SecurityUtils.getLoggedUserUsernameForLog());
        return cartProductService.findLoggedUserCartItems();
    }

    @GetMapping("/status")
    public String getCartStatus() {
        log.info("getCartStatus{}", SecurityUtils.getLoggedUserUsernameForLog());
        return cartProductService.getLoggedUserCartStatus();
    }

    @PutMapping
    public void updateCart(@RequestBody @Valid CartProductFormDto formDto) {
        log.info("updateCart{}", SecurityUtils.getLoggedUserUsernameForLog());
        cartProductService.updateProduct(formDto);
    }

    @DeleteMapping
    public void emptyCart() {
        log.info("emptyCart{}", SecurityUtils.getLoggedUserUsernameForLog());
        cartProductService.emptyCart();
    }
}
