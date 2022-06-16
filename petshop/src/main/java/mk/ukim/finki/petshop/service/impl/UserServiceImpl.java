package mk.ukim.finki.petshop.service.impl;

import lombok.AllArgsConstructor;
import mk.ukim.finki.petshop.model.User;
import mk.ukim.finki.petshop.model.enumerations.Role;
import mk.ukim.finki.petshop.model.exceptions.EmailAlreadyExistsException;
import mk.ukim.finki.petshop.model.exceptions.InvalidUsernameOrPasswordException;
import mk.ukim.finki.petshop.model.exceptions.PasswordsDoNotMatchException;
import mk.ukim.finki.petshop.repository.UserRepository;
import mk.ukim.finki.petshop.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(String email, String password, String repeatPassword, String name, String surname, String address, Role role) {
        if (email==null || email.isEmpty()  || password==null || password.isEmpty())
            throw new InvalidUsernameOrPasswordException();
        if (!password.equals(repeatPassword))
            throw new PasswordsDoNotMatchException();
        if(this.userRepository.findByEmail(email).isPresent())
            throw new EmailAlreadyExistsException(email);
        User user = new User(email,passwordEncoder.encode(password),name,surname,address,role);
        return userRepository.save(user);

    }

    @Override
    public void updateResetPasswordToken(String token, String email) {
        User user = this.userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException(email));
        if (user != null) {
            user.setResetPasswordToken(token);
            userRepository.save(user);
        }
    }
    @Override
    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }
    @Override
    public void updatePassword(User user, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetPasswordToken(null);
        userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException(username));
    }
}
