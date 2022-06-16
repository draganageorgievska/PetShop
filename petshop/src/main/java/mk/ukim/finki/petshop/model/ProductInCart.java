package mk.ukim.finki.petshop.model;

import lombok.Data;
import mk.ukim.finki.petshop.model.enumerations.Size;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Data
@Entity
public class ProductInCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Product product;
    private Integer quantity;
    @Enumerated(value=EnumType.STRING)
    private Size size;
    private String flavor;
    private Double price;
    public ProductInCart(Product product,Integer quantity,Double price){
        this.product=product;
        this.quantity=quantity;
        this.price=price;
    }
    public ProductInCart(Product product,Integer quantity,String flavor,Double price){
        this.product=product;
        this.quantity=quantity;
        this.flavor=flavor;
        this.price=price;
    }
    public ProductInCart(Product product,Integer quantity,Size size,Double price){
        this.product=product;
        this.quantity=quantity;
        this.size=size;
        this.price=price;
    }
    public ProductInCart() {

    }
}
