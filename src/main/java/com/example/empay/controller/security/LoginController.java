package com.example.empay.controller.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * Render the login form.
     *
     * @return The login form URL.
     */
    @GetMapping("/login")
    String login() {
        return "login";
    }
}
