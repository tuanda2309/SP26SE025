package com.example.SP26SE025.controller;
import com.example.SP26SE025.dtos.ClinicRegisterDTO;
import com.example.SP26SE025.entity.RegisterResult;
import com.example.SP26SE025.service.ClinicRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/clinicRegister")
public class ClinicRegisterController {

    @Autowired
    private ClinicRegisterService clinicRegisterService;

    @GetMapping("")
    public String showRegisterForm(Model model) {
        model.addAttribute("clinic", new ClinicRegisterDTO());
        return "clinic/register";
    }

    @PostMapping("")
public String registerClinic(
        @ModelAttribute("clinic") ClinicRegisterDTO dto,
        Model model
) {
    RegisterResult result = clinicRegisterService.registerClinic(dto);

    switch (result) {
        case USERNAME_EXISTS:
            model.addAttribute("error", "Tên đăng nhập đã tồn tại");
            return "clinic/register";

        case EMAIL_EXISTS:
            model.addAttribute("error", "Email đã được sử dụng");
            return "clinic/register";

        case PHONE_EXISTS:
            model.addAttribute("error", "Số điện thoại đã được sử dụng");
            return "clinic/register";

        case SUCCESS:
            return "redirect:/login";
    }

    model.addAttribute("error", "Đăng ký thất bại");
    return "clinic/register";
}

}

