package mk.ukim.finki.petshop.model;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.petshop.model.enumerations.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "shop_users")
public class User implements UserDetails {
    @Id
    private String username;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String address;
    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = true;
    @Enumerated(value = EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<ShoppingCart> carts;
    @Column(name = "reset_password_token")
    private String resetPasswordToken;
    public User() {
    }

    public User(String email, String password, String name, String surname,String address, Role role) {
        this.username=email;
        this.email=email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.role = role;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

}
