package com.mercatura.backend.controller;

import com.mercatura.backend.dto.Statistics.CartStatistics;
import com.mercatura.backend.dto.Statistics.ProductStatistics;
import com.mercatura.backend.dto.Statistics.UserStatistics;
import com.mercatura.backend.service.CartService;
import com.mercatura.backend.service.ProductService;
import com.mercatura.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;

    public AdminController(UserService userService, CartService cartService, ProductService productService) {
        this.userService = userService;
        this.cartService = cartService;
        this.productService = productService;
    }

    @Tag(name = "Admin/User")
    @Operation(summary = "Gets user statistics.")
    @GetMapping("/user")
    public UserStatistics getAllUsers() {
        return userService.getAllUsersStatistics();
    }

    @Tag(name = "Admin/Cart")
    @Operation(summary = "Gets carts statistics.")
    @GetMapping("/cart")
    public CartStatistics getAllCartsStatistics( ) {
        return cartService.getAllCartsStatistics();
    }

    @Tag(name = "Admin/Product")
    @Operation(summary = "Gets products statistics.")
    @GetMapping("/product")
    public ProductStatistics getAllProductsStatistics( ) {
        return productService.getAllProductsStatistics();
    }
}
