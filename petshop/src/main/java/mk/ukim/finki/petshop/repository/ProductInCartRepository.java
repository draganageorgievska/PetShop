package mk.ukim.finki.petshop.repository;

import mk.ukim.finki.petshop.model.ProductInCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInCartRepository extends JpaRepository<ProductInCart,Long> {
}
