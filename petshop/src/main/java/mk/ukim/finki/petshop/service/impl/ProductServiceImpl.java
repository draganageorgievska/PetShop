package mk.ukim.finki.petshop.service.impl;

import mk.ukim.finki.petshop.model.Product;
import mk.ukim.finki.petshop.model.enumerations.AnimalCategory;
import mk.ukim.finki.petshop.model.enumerations.ProductCategory;
import mk.ukim.finki.petshop.model.exceptions.ProductNotFoundException;
import mk.ukim.finki.petshop.repository.ProductRepository;
import mk.ukim.finki.petshop.service.ProductService;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Configuration
@Service
public class ProductServiceImpl implements ProductService, WebMvcConfigurer {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAll(AnimalCategory category,ProductCategory productCategory) {
        if(productCategory!=null){
            return this.productRepository.findByCategoryAndProductCategory(category,productCategory);
        }
        return this.productRepository.findByCategory(category);
    }

    @Override
    public Product findProductById(Long id) {
        return this.productRepository.findById(id).orElseThrow(()->new ProductNotFoundException(id));
    }

    @Override
    public List<Product> filterProducts(String text) {
        List<Product> productsName=this.productRepository.findAllByNameContainingIgnoreCase(text);
        List<Product> productsDescription=this.productRepository.findAllByDescriptionContainingIgnoreCase(text);
        productsName.removeAll(productsDescription);
        productsName.addAll(productsDescription);
        return productsName;
    }

    @Override
    public Product deleteProduct(Long id) {
        Product product=this.productRepository.findById(id).orElseThrow(()->new ProductNotFoundException(id));
        Path imageFolder=Paths.get("C:/Users/38978/IdeaProjects/petshop/product-photos/"+product.getId());
        Path imagePath=Paths.get("C:/Users/38978/IdeaProjects/petshop/"+product.getImage());
        try {
            Files.delete(imagePath);
            Files.delete(imageFolder);
        }
        catch (IOException e){
            System.err.println("Unable to delete "
                    + imagePath.toAbsolutePath().toString()
                    + " due to...");
            e.printStackTrace();
        }
        this.productRepository.delete(product);
        return product;
    }

    @Override
    public Product addProduct(String name, String description, String image, Double price, Integer quantity, AnimalCategory category, ProductCategory productCategory) {
        return this.productRepository.save(new Product(name,description,image,price,quantity,category,productCategory));
    }

    @Override
    public Product editProduct(Long id, String name, String description,String image, Double price, Integer quantity, AnimalCategory category,ProductCategory productCategory) {
        Product product=this.productRepository.findById(id).orElseThrow(()->new ProductNotFoundException(id));
        product.setName(name);
        product.setDescription(description);
        if(!image.equals("")) {
            Path imagePath=Paths.get("C:/Users/38978/IdeaProjects/petshop/"+product.getImage());
            try {
                Files.delete(imagePath);
            }
            catch (IOException e){
                System.err.println("Unable to delete "
                        + imagePath.toAbsolutePath().toString()
                        + " due to...");
                e.printStackTrace();
            }
            product.setImage(image);
        }
        product.setQuantity(quantity);
        product.setPrice(price);
        product.setCategory(category);
        product.setProductCategory(productCategory);
        return this.productRepository.save(product);
    }

    @Override
    public void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory("product-photos", registry);
    }

    @Override
    public void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        if (dirName.startsWith("../")) dirName = dirName.replace("../", "");
        registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/"+ uploadPath + "/");
    }
}
