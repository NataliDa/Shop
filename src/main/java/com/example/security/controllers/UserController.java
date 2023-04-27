package com.example.security.controllers;

import com.example.security.enumm.Status;
import com.example.security.models.Cart;
import com.example.security.models.Order;
import com.example.security.models.Product;
import com.example.security.repository.CartRepository;
import com.example.security.repository.OrderRepository;
import com.example.security.repository.ProductRepository;
import com.example.security.security.PersonDetails;
import com.example.security.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    private final ProductService productService;

    private final ProductRepository productRepository;

    private final CartRepository cartRepository;

    private final OrderRepository orderRepository;

    public UserController(ProductService productService, ProductRepository productRepository, CartRepository cartRepository, OrderRepository orderRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/person account")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        String role = personDetails.getPerson().getRole().name();
        if (role.equals("ADMIN")) {
            return "redirect:/admin";
        }
        model.addAttribute("products", productService.getAllProduct());
        return "/person/user";
    }

    @GetMapping("/product/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model) {
        model.addAttribute("product", productService.getProductId(id));
        return "/product/infoProduct";
    }

    @PostMapping("/person account/product/search")
    public String productSearch(@RequestParam("search") String search, @RequestParam("ot") String ot, @RequestParam("do") String Do, @RequestParam(value = "price", required = false, defaultValue = "") String price, @RequestParam(value = "contract", required = false, defaultValue = "") String contract, Model model) {
        model.addAttribute("products", productService.getAllProduct());

        if (!ot.isEmpty() & !Do.isEmpty()) {
            if (!price.isEmpty()) {
                if (price.equals("sorted_by_ascending_price")) {
                    findAllProductsByTitleAndPriceAndCategory(search, ot, Do, contract, model);
                } else {
                    if (!contract.isEmpty()) {
                        switch (contract) {
                            case "furniture" -> model.addAttribute("search_product", productRepository.findByCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                            case "appliances" -> model.addAttribute("search_product", productRepository.findByCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                            case "clothes" -> model.addAttribute("search_product", productRepository.findByCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                        }
                    } else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                    }
                }
            } else {
                findAllProductsByTitleAndPriceAndCategory(search, ot, Do, contract, model);
            }
        } else {
            if (!price.isEmpty()) {
                if (price.equals("sorted_by_ascending_price")) {
                    if (!contract.isEmpty()) {
                        findAllProductsByTitleAndCategory(search, contract, model);
                    } else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceAsc(search.toLowerCase()));
                    }
                } else {
                    if (!contract.isEmpty()) {
                        switch (contract) {
                            case "furniture" -> model.addAttribute("search_product", productRepository.findByCategoryOrderByPriceDesc(search.toLowerCase(), 1));
                            case "appliances" -> model.addAttribute("search_product", productRepository.findByCategoryOrderByPriceDesc(search.toLowerCase(), 2));
                            case "clothes" -> model.addAttribute("search_product", productRepository.findByCategoryOrderByPriceDesc(search.toLowerCase(), 3));
                        }
                    } else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceDesc(search.toLowerCase()));
                    }
                }
            } else {
                if (!contract.isEmpty()) {
                    findAllProductsByTitleAndCategory(search, contract, model);
                } else {
                    model.addAttribute("search_product", productRepository.findByTitleContainingIgnoreCase(search.toLowerCase()));
                }
            }
        }

        model.addAttribute("value_search", search);
        model.addAttribute("value_price_ot", ot);
        model.addAttribute("value_price_do", Do);
        return "/product/product";
    }

    @GetMapping("/cart/add/{id}")
    public String addProductInCart(@PathVariable("id") int id, Model model) {
        Product product = productService.getProductId(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Long id_person = personDetails.getPerson().getId();
        Cart cart = new Cart(id_person, product.getId());
        cartRepository.save(cart);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        List<Product> productList = getProductList();

        float price = 0;
        for (Product product : productList) {
            price += product.getPrice();
        }

        model.addAttribute("price", price);
        model.addAttribute("cart_product", productList);
        return "/person/user/cart";
    }

    @GetMapping("/cart/delete/{id}")
    public String deleteProductFromCart(Model model, @PathVariable("id") int id) {
        List<Product> productList = getProductList();
        cartRepository.deleteCartByProductId(id);
        return "redirect:/cart";
    }

    @GetMapping("/order/create")
    public String order() {
        PersonDetails personDetails = getPersonDetails();
        List<Product> productList = getProductList();

        float price = 0;
        for (Product product : productList) {
            price += product.getPrice();
        }

        String number = "1000";
        if (orderRepository.count() != 0) {
            int maxId = orderRepository.findMaxId();
            Order order = orderRepository.findById(maxId).orElseThrow();
            number = String.valueOf(Integer.parseInt(order.getNumber()) + 1);
        }
        for (Product product : productList) {
            Order newOrder = new Order(number, product, personDetails.getPerson(), 1, product.getPrice(), Status.Принят);
            orderRepository.save(newOrder);
            cartRepository.deleteCartByProductId(product.getId());
        }
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        List<Order> orderList = orderRepository.findByPerson(personDetails.getPerson());
        model.addAttribute("orders", orderList);
        return "/person/user/orders";
    }

    private PersonDetails getPersonDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (PersonDetails) authentication.getPrincipal();
    }

    private List<Product> getProductList() {
        Long id_person = getPersonDetails().getPerson().getId();

        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList = new ArrayList<>();

        for (Cart cart : cartList) {
            productList.add(productService.getProductId(cart.getProductId()));
        }
        return productList;
    }

    private void findAllProductsByTitleAndPriceAndCategory(String search, String ot, String Do, String contract, Model model) {
        if (!contract.isEmpty()) {
            switch (contract) {
                case "furniture" -> model.addAttribute("search_product", productRepository.findByCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                case "appliances" -> model.addAttribute("search_product", productRepository.findByCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                case "clothes" -> model.addAttribute("search_product", productRepository.findByCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
            }
        } else {
            model.addAttribute("search_product", productRepository.findByTitleOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
        }
    }

    private void findAllProductsByTitleAndCategory(String search, @RequestParam(value = "contract", required = false, defaultValue = "") String contract, Model model) {
        switch (contract) {
            case "furniture" -> model.addAttribute("search_product", productRepository.findByCategoryOrderByPriceAsc(search.toLowerCase(), 1));
            case "appliances" -> model.addAttribute("search_product", productRepository.findByCategoryOrderByPriceAsc(search.toLowerCase(), 2));
            case "clothes" -> model.addAttribute("search_product", productRepository.findByCategoryOrderByPriceAsc(search.toLowerCase(), 3));
        }
    }
}
