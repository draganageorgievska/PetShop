package mk.ukim.finki.petshop.web.controller;

import lombok.AllArgsConstructor;
import mk.ukim.finki.petshop.model.ShoppingCart;
import mk.ukim.finki.petshop.model.OrderExcelExporter;
import mk.ukim.finki.petshop.model.enumerations.Role;
import mk.ukim.finki.petshop.model.exceptions.EmailAlreadyExistsException;
import mk.ukim.finki.petshop.model.exceptions.InvalidUsernameOrPasswordException;
import mk.ukim.finki.petshop.model.exceptions.PasswordsDoNotMatchException;
import mk.ukim.finki.petshop.service.ShoppingCartService;
import mk.ukim.finki.petshop.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final ShoppingCartService shoppingCartService;
    @GetMapping("/register")
    public String getRegisterPage(@RequestParam(required = false) String error, Model model){
        if(error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        model.addAttribute("bodyContent", "register");
        return "master-template";
    }
    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String repeatedPassword,
                           @RequestParam String name,
                           @RequestParam String surname,
                           @RequestParam String address) {
        try{
            this.userService.register(email, password, repeatedPassword, name, surname,address, Role.ROLE_USER);
            return "redirect:/loginPage";
        } catch (InvalidUsernameOrPasswordException | EmailAlreadyExistsException | PasswordsDoNotMatchException exception) {
            return "redirect:/register?error=" + exception.getMessage();
        }
    }
    @GetMapping("/loginPage")
    public String login(Model model){
        model.addAttribute("bodyContent", "login");
        return "master-template";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/orders")
    public String getOrders(Model model){
        List<ShoppingCart> orders=this.shoppingCartService.getAllFinishedCarts();
        model.addAttribute("orders",orders);
        model.addAttribute("bodyContent", "orders");
        return "master-template";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/export/orders")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=orders_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<ShoppingCart> orders = this.shoppingCartService.getAllFinishedCarts();

        OrderExcelExporter excelExporter = new OrderExcelExporter(orders);

        excelExporter.export(response);
    }
}
