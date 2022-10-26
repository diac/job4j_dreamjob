package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.store.CandidateStore;

import java.time.LocalDateTime;

@Controller
public class CandidateController {

    private final CandidateStore candidateStore = CandidateStore.instOf();

    @GetMapping("/candidates")
    public String candidates(Model model) {
        model.addAttribute("candidates", candidateStore.findAll());
        return "candidates";
    }

    @GetMapping("/formAddCandidate")
    public String addCandidate(Model model) {
        model.addAttribute(
                "candidate",
                new Candidate(
                        0,
                        "Заполните имя",
                        "Заполните описание",
                        LocalDateTime.now()
                )
        );
        return "addCandidate";
    }

    @PostMapping("/formAddCandidate")
    public String storeCandidate(@ModelAttribute Candidate candidate) {
        System.out.println(candidate.getName());
        candidateStore.create(candidate);
        return "redirect:/candidates";
    }
}