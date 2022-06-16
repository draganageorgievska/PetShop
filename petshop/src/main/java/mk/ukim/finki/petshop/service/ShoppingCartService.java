package mk.ukim.finki.petshop.service;

import mk.ukim.finki.petshop.model.ProductInCart;
import mk.ukim.finki.petshop.model.ShoppingCart;
import mk.ukim.finki.petshop.model.enumerations.Size;

import java.util.List;

public interface ShoppingCartService {
    List<ProductInCart> listAllProductsInShoppingCart(Long cartId);
    ShoppingCart getActiveShoppingCart(String username);
    ShoppingCart addProductToShoppingCart(String username, Long productId, Integer quantity, Size size,String flavor);
    void checkoutCart(Long cartId);
    void removeProductFromCart(Long cartId,Long productId);
    ShoppingCart getShoppingCartById(Long id);
    List<ShoppingCart> getAllFinishedCarts();
}
