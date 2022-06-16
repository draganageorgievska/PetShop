package mk.ukim.finki.petshop.web.controller;

import mk.ukim.finki.petshop.model.Product;
import mk.ukim.finki.petshop.model.enumerations.AnimalCategory;
import mk.ukim.finki.petshop.model.enumerations.ProductCategory;
import mk.ukim.finki.petshop.model.enumerations.Size;
import mk.ukim.finki.petshop.service.ProductService;
import mk.ukim.finki.petshop.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductsController {
    private final ProductService productService;
    public ProductsController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping("/")
    public String homePage(Model model){
        model.addAttribute("bodyContent", "home");
        return "master-template";
    }
    @GetMapping("/access_denied")
    public String getAccessDeniedPage(Model model) {
        model.addAttribute("bodyContent", "access_denied");
        return "master-template";
    }
    @GetMapping({"/products","/products/{animal}","/products/{animal}/{category}"})
    public String findAll(@PathVariable(required = false) AnimalCategory animal, @PathVariable(required = false) ProductCategory category, @RequestParam(required = false) String searchText, Model model){
        List<Product> products=new ArrayList<>();
        if(searchText!=null){
            products=this.productService.filterProducts(searchText);
        }
        else{
            products=this.productService.findAll(animal,category);
        }
        model.addAttribute("animal", animal);
        model.addAttribute("category",category);
        model.addAttribute("products",products);
        model.addAttribute("bodyContent", "products");
        return "master-template";
    }
    @GetMapping("/admin/products/add-form")
    public String addProductPage(Model model){
        model.addAttribute("animalCategories", AnimalCategory.values());
        model.addAttribute("productCategories", ProductCategory.values());
        model.addAttribute("bodyContent", "add-product");
        return "master-template";
    }
    @PostMapping("/admin/products/add")
    public String addProduct(@RequestParam String name,@RequestParam String description,@RequestParam MultipartFile image,@RequestParam Double price, @RequestParam Integer quantity,@RequestParam AnimalCategory animal,@RequestParam ProductCategory productCategory) throws IOException {
        String fileName = StringUtils.cleanPath(image.getOriginalFilename());
        Product product=this.productService.addProduct(name,description,fileName,price,quantity,animal,productCategory);
        String uploadDir = "product-photos/" + product.getId();
        this.productService.saveFile(uploadDir, fileName, image);
        return "redirect:/products/"+animal;
    }
    @GetMapping("/admin/products/edit-form/{id}")
    public String editForm(@PathVariable Long id,Model model){
        Product product=null;
        try {
            product = this.productService.findProductById(id);
        }
        catch(RuntimeException ex){
            return "redirect:/access_denied";
        }
        model.addAttribute("product",product);
        model.addAttribute("animalCategories", AnimalCategory.values());
        model.addAttribute("productCategories", ProductCategory.values());
        model.addAttribute("bodyContent", "add-product");
        return "master-template";
    }
    @PostMapping("/admin/products/add/{id}")
    public String editProduct(@PathVariable Long id,@RequestParam String name,@RequestParam String description,@RequestParam MultipartFile image,@RequestParam Double price, @RequestParam Integer quantity,@RequestParam AnimalCategory animal,@RequestParam ProductCategory productCategory) throws IOException{
        String fileName="";
        if(!image.isEmpty()) {
            fileName = StringUtils.cleanPath(image.getOriginalFilename());
            String uploadDir = "product-photos/" + id;
            this.productService.saveFile(uploadDir, fileName, image);
        }
        Product product = this.productService.editProduct(id, name, description, fileName, price, quantity, animal,productCategory);
        return "redirect:/products/"+animal;
    }
    @GetMapping("/admin/products/delete/{animal}/{id}")
    public String deleteProduct(@PathVariable AnimalCategory animal, @PathVariable Long id) {
        this.productService.deleteProduct(id);
        return "redirect:/products/"+animal;
    }
    @GetMapping("/products/showProduct/{id}")
    public String showProduct(@PathVariable Long id, Model model){
        Product product=null;
        try {
            product = this.productService.findProductById(id);
        }
        catch(Exception ex){
            return "redirect:/access_denied";
        }
        model.addAttribute("product",product);
        model.addAttribute("sizes", Size.values());
        model.addAttribute("bodyContent", "product");
        return "master-template";
    }
    @PostMapping("/products/filter/{animal}")
    public String filterByPrice(@PathVariable AnimalCategory animal,@RequestParam(required = false) ProductCategory category, @RequestParam Integer filterPrice, Model model){
        List<Product> products=this.productService.findAll(animal,category);
        if(filterPrice==1) {
            products = products.stream().sorted(Comparator.comparing(Product::getPrice)).collect(Collectors.toList());
        }
        else{
            products = products.stream().sorted(Comparator.comparing(Product::getPrice).reversed()).collect(Collectors.toList());
        }
        model.addAttribute("animal", animal);
        if(category!=null) {
            model.addAttribute("category", category);
        }
        model.addAttribute("products",products);
        model.addAttribute("bodyContent", "products");
        return "master-template";
    }
}
