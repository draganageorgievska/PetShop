package mk.ukim.finki.petshop.repository;

import mk.ukim.finki.petshop.model.Product;
import mk.ukim.finki.petshop.model.enumerations.AnimalCategory;
import mk.ukim.finki.petshop.model.enumerations.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findAllByNameContainingIgnoreCase(String name);
    List<Product> findAllByDescriptionContainingIgnoreCase(String description);
    List<Product> findByCategory(AnimalCategory category);
    List<Product> findByCategoryAndProductCategory(AnimalCategory animalCategory, ProductCategory productCategory);
}
