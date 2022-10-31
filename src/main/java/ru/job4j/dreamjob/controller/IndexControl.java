package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

import static ru.job4j.dreamjob.util.Users.fromHttpSession;

@Controller
@ThreadSafe
public final class IndexControl {

    @GetMapping("/index")
    public String index(Model model, HttpSession session) {
        model.addAttribute("user", fromHttpSession(session));
        return "index";
    }
}