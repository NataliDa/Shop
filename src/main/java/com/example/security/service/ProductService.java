package com.example.security.service;

import com.example.security.models.Category;
import com.example.security.models.Image;
import com.example.security.models.Product;
import com.example.security.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    @Value("upload.path")
    private String uploadPath;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    public Product getProductId(int id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElse(null);
    }

    @Transactional
    public void saveProduct(Product product, Category category) {
        product.setCategory(category);
        productRepository.save(product);
    }

    @Transactional
    public void updateProduct(int id, Product product) {
        product.setId(id);
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(int id) throws IOException {
        Product product = productRepository.findById(id).orElseThrow();
        List<Image> imageList = product.getImageList();
        for (Image image : imageList) {
            File file = new File(uploadPath + "/" + image.getFileName());
            Files.deleteIfExists(file.toPath());
        }
        productRepository.deleteById(id);
    }
}
