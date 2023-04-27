package com.example.security.controllers;

import com.example.security.enumm.Role;
import com.example.security.models.Category;
import com.example.security.models.Image;
import com.example.security.models.Order;
import com.example.security.models.Person;
import com.example.security.models.Product;
import com.example.security.security.PersonDetails;
import com.example.security.service.AdminService;
import com.example.security.service.CategoryService;
import com.example.security.service.OrderService;
import com.example.security.service.PersonService;
import com.example.security.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class AdminController {
    private final ProductService productService;

    private final CategoryService categoryService;

    private final PersonService personService;

    private final OrderService orderService;

    private final AdminService adminService;

    @Value("${upload.path}")
    private String uploadPath;

    public AdminController(ProductService productService, CategoryService categoryService, PersonService personService, OrderService orderService, AdminService adminService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.personService = personService;
        this.orderService = orderService;
        this.adminService = adminService;
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("products", productService.getAllProduct());
        return "/person/admin";
    }

    @GetMapping("admin/product/add")
    public String addProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("category", categoryService.findAll());
        return "product/addProduct";
    }

    @PostMapping("/admin/product/add")
    public String addProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult, @RequestParam("file_one") MultipartFile file_one, @RequestParam("file_two") MultipartFile file_two, @RequestParam("file_three") MultipartFile file_three, @RequestParam("file_four") MultipartFile file_four, @RequestParam("file_five") MultipartFile file_five, @RequestParam("category") int category, Model model) throws IOException {
        Category category_db = categoryService.findById(category);
        if (bindingResult.hasErrors()) {
            model.addAttribute("category", categoryService.findAll());
            return "product/addProduct";
        }
        if (file_one.getOriginalFilename() != null && !file_one.getOriginalFilename().isEmpty()) {
            product.addImageToProduct(createImage(file_one, product));
        }

        if (file_two.getOriginalFilename() != null && !file_two.getOriginalFilename().isEmpty()) {
            product.addImageToProduct(createImage(file_two, product));
        }

        if (file_three.getOriginalFilename() != null && !file_three.getOriginalFilename().isEmpty()) {
            product.addImageToProduct(createImage(file_three, product));
        }

        if (file_four.getOriginalFilename() != null && !file_four.getOriginalFilename().isEmpty()) {
            product.addImageToProduct(createImage(file_four, product));
        }

        if (file_five.getOriginalFilename() != null && !file_five.getOriginalFilename().isEmpty()) {
            product.addImageToProduct(createImage(file_five, product));
        }
        if (!product.getImageList().isEmpty()) {
            productService.saveProduct(product, category_db);
        }
        return "redirect:/admin";
    }

    @GetMapping("admin/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id, Model model) {
        try {
            productService.deleteProduct(id);
        } catch (Exception e) {
            List<Product> products = productService.getAllProduct();
            model.addAttribute("products", products);
            for (Product product : products) {
                if (product.getId() == id) {
                    product.setError("Нельзя удалить данный товар, потому что он включен в заказ");
                }
            }
            return "/person/admin";
        }
        return "redirect:/admin";
    }

    @GetMapping("admin/product/edit/{id}")
    public String editProduct(Model model, @PathVariable("id") int id) {
        model.addAttribute("product", productService.getProductId(id));
        model.addAttribute("category", categoryService.findAll());
        return "product/editProduct";
    }

    @PostMapping("admin/product/edit/{id}")
    public String editProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult, @PathVariable("id") int id, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("category", categoryService.findAll());
            return "product/editProduct";
        }
        productService.updateProduct(id, product);
        return "redirect:/admin";
    }

    @GetMapping("admin/persons")
    public String getPersons(Model model) {
        model.addAttribute("persons", personService.getAllPersons());
        return "/person/admin/persons";
    }

    @PostMapping("admin/person/edit/{id}")
    public String editPersonRole(@PathVariable("id") Long id) {
        personService.editPersonRole(id);
        return "redirect:/admin/persons";
    }

    @GetMapping("admin/orders")
    public String getOrders(Model model) {
        List<Order> orderList = personService.getAllOrders();
        model.addAttribute("orders", orderList);
        return "/person/user/orders";
    }

    @GetMapping("admin/order/edit/{id}")
    public String getEditOrderStatusForm(@PathVariable("id") int id, Model model) {
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        return "/person/user/order";
    }

    @PostMapping("admin/order/edit/{id}")
    public String editOrderStatus(@PathVariable("id") int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = personDetails.getPerson();
        if (Role.ADMIN.equals(person.getRole())) {
            adminService.editOrderStatus(id);
        } else {
            personService.editOrderStatus(id);
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/admin/order/search")
    public String searchOrder(@RequestParam("search") String search, Model model) {
        Order order = orderService.searchOrder(search);
        if (order == null) {
            return "redirect:/admin/order/search";
        }
        model.addAttribute("order", order);
        return "/person/user/order";
    }

    private Image createImage(MultipartFile file, Product product) throws IOException {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        String uuidFile = UUID.randomUUID().toString();
        String resultFileName = uuidFile + "." + file.getOriginalFilename();
        file.transferTo(new File(uploadPath + "/" + resultFileName));
        Image image = new Image();
        image.setProduct(product);
        image.setFileName(resultFileName);
        return image;
    }
}
