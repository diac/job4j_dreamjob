package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.store.CandidateStore;

import java.util.List;

public class CandidateService {

    private static final CandidateService INST = new CandidateService();

    private final CandidateStore store = CandidateStore.instOf();

    public static CandidateService getInstance() {
        return INST;
    }

    public List<Candidate> findAll() {
        return store.findAll().stream().toList();
    }

    public Candidate add(Candidate candidate) {
        return store.add(candidate);
    }

    public Candidate findById(int id) {
        return store.findById(id);
    }

    public Candidate update(Candidate candidate) {
        return store.update(candidate);
    }
}