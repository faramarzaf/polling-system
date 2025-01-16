package org.example.pollingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard() {
        return "admin-dashboard";
    }


}
