package mk.ukim.finki.petshop.repository;

import mk.ukim.finki.petshop.model.ShoppingCart;
import mk.ukim.finki.petshop.model.User;
import mk.ukim.finki.petshop.model.enumerations.ShoppingCartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,Long> {
    Optional<ShoppingCart> findByUserAndStatus(User user, ShoppingCartStatus status);
    List<ShoppingCart> findByStatus(ShoppingCartStatus status);
}
