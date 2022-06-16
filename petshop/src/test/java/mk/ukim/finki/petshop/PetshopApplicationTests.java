package mk.ukim.finki.petshop;

import mk.ukim.finki.petshop.service.ProductService;
import mk.ukim.finki.petshop.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class PetshopApplicationTests {
    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;

    @Test
    void contextLoads() {
    }
    @Test
    public void test1_list() {
    }

}
