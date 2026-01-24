package com.example.SP26SE025.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/doctor")
public class DoctorViewController {

    @GetMapping("/home")
    public String showDashboard() {
        return "doctor/home";
    }

    @GetMapping("/review")
    public String showReview() {
        return "doctor/doctor_review";
    }

    @GetMapping("/result")
    public String showResult() {
        return "doctor/doctor_result";
    }
}
