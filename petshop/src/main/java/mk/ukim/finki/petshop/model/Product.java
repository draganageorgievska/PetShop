package mk.ukim.finki.petshop.model;

import lombok.Data;
import mk.ukim.finki.petshop.model.enumerations.AnimalCategory;
import mk.ukim.finki.petshop.model.enumerations.ProductCategory;

import javax.persistence.*;

@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String image;
    private Double price;
    private Integer quantity;
    @Enumerated(value = EnumType.STRING)
    private AnimalCategory category;
    @Enumerated(value = EnumType.STRING)
    private ProductCategory productCategory;
    public Product(String name, String description, String image, Double price, Integer quantity, AnimalCategory category,ProductCategory productCategory){
        this.name=name;
        this.description=description;
        this.image=image;
        this.price=price;
        this.quantity=quantity;
        this.category=category;
        this.productCategory=productCategory;
    }

    public Product() {

    }

    public AnimalCategory getCategory() {
        return category;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public String getImage() {
        if (image == null || id == null) return null;

        return "/product-photos/" + id + "/" + image;
    }
}
