package mk.ukim.finki.petshop.web.controller;

import mk.ukim.finki.petshop.model.User;
import mk.ukim.finki.petshop.model.Utility;
import mk.ukim.finki.petshop.model.exceptions.UserNotFoundException;
import mk.ukim.finki.petshop.service.UserService;
import net.bytebuddy.utility.RandomString;
import java.util.Properties;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {
    private final UserService userService;

    public ForgotPasswordController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/forgot")
    public String forgot(@RequestParam(required = false) String error, Model model){
        if (error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        model.addAttribute("bodyContent", "forgot_password_form");
        return "master-template";
    }
    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(30);

        try {
            userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

        } catch (UserNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Error while sending email");
        }
        model.addAttribute("bodyContent", "forgot_password_form");
        return "master-template";
    }
    public void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
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
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));

        String subject = "Here's the link to reset your password";
message.setSubject(subject);
        String content = "Hello, "
                + "You have requested to reset your password. "
                + "Click the link below to change your password: "
                + link + " "
                + "Ignore this email if you do remember your password, "
                + "or you have not made the request.";

        message.setText(content);
        Transport.send(message);
    }
    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "redirect:/forgot?error=Invalid Token";
        }
        model.addAttribute("bodyContent", "reset_password_form");
        return "master-template";
    }
    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        User customer = userService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");
        String message="";
        if (customer == null) {
            message="Invalid Token";
        } else {
            userService.updatePassword(customer, password);
            message="You have successfully changed your password.";
        }
        model.addAttribute("message",message);
        model.addAttribute("bodyContent", "reset_password_form");
        return "master-template";    }
}
