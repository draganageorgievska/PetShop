package mk.ukim.finki.petshop.repository;

import mk.ukim.finki.petshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    User findByResetPasswordToken(String token);
}
