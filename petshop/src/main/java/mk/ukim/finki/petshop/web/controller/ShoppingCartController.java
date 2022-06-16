package mk.ukim.finki.petshop.web.controller;

import com.stripe.exception.*;
import com.stripe.model.Charge;
import mk.ukim.finki.petshop.model.ChargeRequest;
import mk.ukim.finki.petshop.model.ProductInCart;
import mk.ukim.finki.petshop.model.ShoppingCart;
import mk.ukim.finki.petshop.model.User;
import mk.ukim.finki.petshop.model.enumerations.Size;
import mk.ukim.finki.petshop.service.ShoppingCartService;
import mk.ukim.finki.petshop.service.UserService;
import mk.ukim.finki.petshop.service.impl.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping("/shopping-cart")
public class ShoppingCartController {
    @Value("pk_test_51Iz1azA9T6OJb1xwWNwBxcYMBePjh6FqIz4zYYljrGVrhNYPQNS6RDweCI0H3NDHGk1z30oza511gtHyN25ihKBJ002U5Bw624")
    private String stripePublicKey;
    private final ShoppingCartService shoppingCartService;
    private final StripeService paymentsService;
    public ShoppingCartController(ShoppingCartService shoppingCartService, StripeService paymentsService) {
        this.shoppingCartService = shoppingCartService;
        this.paymentsService = paymentsService;
    }

    @GetMapping
    public String getShoppingCartPage(@RequestParam(required = false) String error,
                                      HttpServletRequest req,
                                      Model model) {
        if (error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        String username = req.getRemoteUser();
        ShoppingCart shoppingCart = this.shoppingCartService.getActiveShoppingCart(username);
        List<ProductInCart> products=this.shoppingCartService.listAllProductsInShoppingCart(shoppingCart.getId());
        model.addAttribute("shoppingCartId",shoppingCart.getId());
        model.addAttribute("products", products);
        Double amount=0.0;
        for(ProductInCart product : products){
            amount+=product.getPrice();
        }
        amount=Math.round(amount*100.0)/100.0;
        model.addAttribute("price",amount);
        int am=(int)(amount*100);
        model.addAttribute("amount", am);
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.USD);
        model.addAttribute("bodyContent", "shopping-cart");
        return "master-template";
    }
    @PostMapping("/add-product/{id}")
    public String addProductToShoppingCart(@PathVariable Long id, @RequestParam Integer quantity, @RequestParam(required = false) Size size, @RequestParam(required = false) String flavor, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            this.shoppingCartService.addProductToShoppingCart(user.getUsername(),id,quantity,size,flavor);
            return "redirect:/shopping-cart";
        } catch (RuntimeException exception) {
            return "redirect:/shopping-cart?error=" + exception.getMessage();
        }
    }
    @GetMapping("/{cartId}/{productId}")
    public String removeFromCart(@PathVariable Long cartId,@PathVariable Long productId){
        this.shoppingCartService.removeProductFromCart(cartId,productId);
        return "redirect:/shopping-cart";
    }
    @PostMapping("/charge")
    public String charge(@RequestParam String cartId, ChargeRequest chargeRequest, Model model) throws MessagingException {
        chargeRequest.setDescription("Charge");
        chargeRequest.setCurrency(ChargeRequest.Currency.USD);
        try {
            Charge charge = paymentsService.charge(chargeRequest);
            model.addAttribute("id", charge.getId());
            model.addAttribute("status", charge.getStatus());
            model.addAttribute("chargeId", charge.getId());
            model.addAttribute("balance_transaction", charge.getBalanceTransaction());
            ShoppingCart shoppingCart=this.shoppingCartService.getShoppingCartById(Long.parseLong(cartId));
            this.shoppingCartService.checkoutCart(Long.parseLong(cartId));
            Properties properties=System.getProperties();
            properties.put("mail.smtp.host","smtp.mail.yahoo.com");
            properties.put("mail.smtp.port","587");
            properties.put("mail.smtp.starttls.enable","true");
            properties.put("mail.smtp.auth","true");
            properties.put("mail.smtp.ssl.protocols","TLSv1.2");
            properties.put("mail.smtp.ssl.trust","smtp.mail.yahoo.com");
            Session session=Session.getInstance(properties,new javax.mail.Authenticator(){
                protected PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication("dgtesttest@yahoo.com","piwnaaodpkvswulc");
                }
            });
            MimeMessage message=new MimeMessage(session);
            message.setFrom(new InternetAddress("dgtesttest@yahoo.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(shoppingCart.getUser().getEmail()));
            String subject = "Purchase conformation";
            message.setSubject(subject);
            String content ="Your purchase has been successful!";
            message.setText(content);
            Transport.send(message);
        }
        catch(StripeException ex){
            model.addAttribute("error", ex.getMessage());
        }
        model.addAttribute("bodyContent", "checkout");
        return "master-template";
    }
}
