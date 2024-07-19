package org.security.sample.to_do_list_app.controller;

import org.security.sample.to_do_list_app.model.User;
import org.security.sample.to_do_list_app.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

    // Final Project - Step #5 (service injection to enable user management logic)
    @Autowired
    private CustomUserDetailsService userService;

    // Final Project - Step #2 (create the custom redirect page to /login)
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    // Final Project - Step #4 (custom login page with sign-up redirection)
    @GetMapping("/login")
    public String login() {
        return "log-in-page";
    }

    // Final Project - Step #4 (signup page)
    @GetMapping("/sign-up")
    public String signUp() {
        return "sign-up-page";
    }

    // Final Project - Step #5 (user creation API method)
    @PostMapping("/sign-up")
    public String signUp(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/login";
    }
}
