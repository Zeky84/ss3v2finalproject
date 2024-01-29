package finalproject.ss3v2.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import finalproject.ss3v2.domain.Products;

@RestController
public class ProductController {
//	 THIS CONTROLLER IS FOR TESTING PURPOSES

    List<Products> allProducts = new ArrayList<>();

    public ProductController () {
        allProducts.add(new Products(1, "Product 1", new BigDecimal("19.99")));
        allProducts.add(new Products(2, "Product 2", new BigDecimal("29.99")));
        allProducts.add(new Products(3, "Product 3", new BigDecimal("39.99")));
        allProducts.add(new Products(4, "Product 4", new BigDecimal("49.99")));
    }

    @GetMapping("/products")
    public ResponseEntity<List<Products>> getProducts() {
        return ResponseEntity.ok(allProducts);
    }

}
