package mk.ukim.finki.petshop.service;

import mk.ukim.finki.petshop.model.Product;
import mk.ukim.finki.petshop.model.enumerations.AnimalCategory;
import mk.ukim.finki.petshop.model.enumerations.ProductCategory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<Product> findAll(AnimalCategory category,ProductCategory productCategory);
    Product findProductById(Long id);
    List<Product> filterProducts(String text);
    Product deleteProduct(Long id);
    Product addProduct(String name, String description, String image, Double price, Integer quantity, AnimalCategory category, ProductCategory productCategory);
    Product editProduct(Long id, String name, String description , String image, Double price, Integer quantity, AnimalCategory category,ProductCategory productCategory);
    void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException;
    void exposeDirectory(String dirName, ResourceHandlerRegistry registry);
}
