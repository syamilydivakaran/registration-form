package com.cruduser.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.cruduser.entity.User;
import com.cruduser.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String defaultPage() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, HttpSession session) {
        Optional<User> existingUser = userService.findUserByUsername(user.getUsername());
        if (existingUser.isPresent() && existingUser.get().getPassword().equals(user.getPassword())) {
            session.setAttribute("loggedInUser", existingUser.get());
            return "redirect:/profile";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User user, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        loggedInUser.setFirstname(user.getFirstname());
        loggedInUser.setLastname(user.getLastname());
        loggedInUser.setEmail(user.getEmail());
        loggedInUser.setGender(user.getGender());
        loggedInUser.setBirthdate(user.getBirthdate());
        loggedInUser.setCountry(user.getCountry());
        userService.updateUser(loggedInUser);
        model.addAttribute("user", loggedInUser);
        model.addAttribute("success", "Profile updated successfully!");
        return "profile";
    }

    @GetMapping("/delete")
    public String deleteUser(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        userService.deleteUser(user);
        session.invalidate();
        return "redirect:/register";
    }
}
