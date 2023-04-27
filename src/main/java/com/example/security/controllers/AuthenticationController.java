package com.example.security.controllers;

import com.example.security.models.Person;
import com.example.security.service.PersonService;
import com.example.security.service.ProductService;
import com.example.security.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.function.Consumer;

@Controller
public class AuthenticationController {

    private final PersonValidator personValidator;
    private final PersonService personService;
    private final ProductService productService;

    @Autowired
    public AuthenticationController(PersonValidator personValidator, PersonService personService, ProductService productService) {
        this.personValidator = personValidator;
        this.personService = personService;
        this.productService = productService;
    }

    @GetMapping("/authentication")
    public String login() {
        return "authentication";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("person") Person person) {
        return "registration";
    }

    @PostMapping("/registration")
    public String resultRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        return registration(person, bindingResult, "redirect:/person account", "registration",
                person1 -> personService.registerUser(person));
    }

    @GetMapping("/registrationAdmin")
    public String registrationAdmin(@ModelAttribute("person") Person person) {
        return "registrationAdmin";
    }

    @PostMapping("/registrationAdmin")
    public String resultAdminRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        return registration(person, bindingResult, "redirect:/admin", "registrationAdmin",
                person1 -> personService.registerAdmin(person));
    }

    @PostMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "authentication";
    }

    @GetMapping("/")
    public String getMainPage(Model model) {
        model.addAttribute("products", productService.getAllProduct());
        return "main";
    }

    private String registration(Person person, BindingResult bindingResult,
                                String returnFormSuccess, String returnFormError,
                                Consumer<Person> registration) {
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            return returnFormError;
        }
        registration.accept(person);
        return returnFormSuccess;
    }
}
