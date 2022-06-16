package mk.ukim.finki.petshop.service.impl;

import lombok.AllArgsConstructor;
import mk.ukim.finki.petshop.model.Product;
import mk.ukim.finki.petshop.model.ProductInCart;
import mk.ukim.finki.petshop.model.ShoppingCart;
import mk.ukim.finki.petshop.model.User;
import mk.ukim.finki.petshop.model.enumerations.ShoppingCartStatus;
import mk.ukim.finki.petshop.model.enumerations.Size;
import mk.ukim.finki.petshop.model.exceptions.ProductNotFoundException;
import mk.ukim.finki.petshop.model.exceptions.ShoppingCartNotFoundException;
import mk.ukim.finki.petshop.model.exceptions.UserNotFoundException;
import mk.ukim.finki.petshop.repository.ProductInCartRepository;
import mk.ukim.finki.petshop.repository.ShoppingCartRepository;
import mk.ukim.finki.petshop.repository.UserRepository;
import mk.ukim.finki.petshop.service.ProductService;
import mk.ukim.finki.petshop.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final ProductInCartRepository productInCartRepository;

    @Override
    public List<ProductInCart> listAllProductsInShoppingCart(Long cartId) {
        if(!this.shoppingCartRepository.findById(cartId).isPresent())
            throw new ShoppingCartNotFoundException(cartId);
        return this.shoppingCartRepository.findById(cartId).get().getProducts();
    }

    @Override
    public ShoppingCart getActiveShoppingCart(String username) {
        User user = this.userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return this.shoppingCartRepository
                .findByUserAndStatus(user, ShoppingCartStatus.CREATED)
                .orElseGet(() -> {
                    ShoppingCart cart = new ShoppingCart(user);
                    return this.shoppingCartRepository.save(cart);
                });

    }
    @Transactional
    @Override
    public ShoppingCart addProductToShoppingCart(String username, Long productId, Integer quantity, Size size, String flavor) {
        ShoppingCart shoppingCart = this.getActiveShoppingCart(username);
        Product product = this.productService.findProductById(productId);
        ProductInCart productInCart=null;
        Double productsPrice=Math.round(product.getPrice()*quantity * 100.0) / 100.0;
        if(size==null&&flavor==null) {
            productInCart = new ProductInCart(product,quantity,productsPrice);
        }
        else if(size!=null){
            productInCart = new ProductInCart(product,quantity,size,productsPrice);
        }
        else if(!flavor.equals("")){
            productInCart = new ProductInCart(product,quantity,flavor,productsPrice);
        }
        ProductInCart pc=this.productInCartRepository.save(productInCart);
        shoppingCart.getProducts().add(pc);
        return this.shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void checkoutCart(Long cartId) {
        ShoppingCart shoppingCart=this.shoppingCartRepository.findById(cartId).orElseThrow(()->new ShoppingCartNotFoundException(cartId));
        shoppingCart.setStatus(ShoppingCartStatus.FINISHED);
        this.shoppingCartRepository.save(shoppingCart);
    }
    @Transactional
    @Override
    public void removeProductFromCart(Long cartId, Long productId) {
        ShoppingCart shoppingCart=this.shoppingCartRepository.findById(cartId).orElseThrow(()->new ShoppingCartNotFoundException(cartId));
        ProductInCart productInCart=this.productInCartRepository.findById(productId).orElseThrow(()->new ProductNotFoundException(cartId));
        shoppingCart.getProducts().remove(productInCart);
        this.shoppingCartRepository.save(shoppingCart);
        this.productInCartRepository.delete(productInCart);
    }

    @Override
    public ShoppingCart getShoppingCartById(Long id) {
        return this.shoppingCartRepository.findById(id).orElseThrow(()->new ShoppingCartNotFoundException(id));
    }

    @Override
    public List<ShoppingCart> getAllFinishedCarts() {
        return this.shoppingCartRepository.findByStatus(ShoppingCartStatus.FINISHED);
    }
}
