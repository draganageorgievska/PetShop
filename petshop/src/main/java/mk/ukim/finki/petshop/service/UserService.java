package mk.ukim.finki.petshop.service;

import mk.ukim.finki.petshop.model.User;
import mk.ukim.finki.petshop.model.enumerations.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User register(String email, String password, String repeatPassword, String name, String surname,String address, Role role);
    void updateResetPasswordToken(String token, String email);
    User getByResetPasswordToken(String token);
    void updatePassword(User user, String newPassword);
}
