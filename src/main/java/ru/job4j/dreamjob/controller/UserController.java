package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

@Controller
@ThreadSafe
public final class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registrationForm(Model model) {
        model.addAttribute("user", new User());
        return "userRegistration";
    }

    @PostMapping("/registration")
    public String registrationSubmit(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        Optional<User> regUser = userService.add(user);
        if (regUser.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Пользователь с такой почтой уже существует"
            );
            return "redirect:/fail";
        }
        redirectAttributes.addFlashAttribute(
                "message",
                "Регистрация успешно пройдена"
        );
        return "redirect:/success";
    }

    @GetMapping("/success")
    public String registrationSuccess(Model model) {
        return "success";
    }

    @GetMapping("/fail")
    public String registrationFail(Model model) {
        return "fail";
    }
}