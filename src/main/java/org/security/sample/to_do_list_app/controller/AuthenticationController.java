package org.security.sample.to_do_list_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

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
}
