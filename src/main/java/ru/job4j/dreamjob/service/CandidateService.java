package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.store.CandidateDBStore;

import java.util.List;

@Service
@ThreadSafe
public final class CandidateService {

    private final CandidateDBStore store;

    private final CityService cityService;

    public CandidateService(CandidateDBStore store, CityService cityService) {
        this.store = store;
        this.cityService = cityService;
    }

    public List<Candidate> findAll() {
        var candidates = store.findAll();
        candidates.forEach(
                candidate -> candidate.setCity(
                        cityService.findById(candidate.getCity().getId())
                )
        );
        return candidates;
    }

    public Candidate add(Candidate candidate) {
        return store.add(candidate);
    }

    public Candidate findById(int id) {
        var candidate = store.findById(id);
        candidate.setCity(cityService.findById(candidate.getCity().getId()));
        return candidate;
    }

    public Candidate update(Candidate candidate) {
        return store.update(candidate);
    }
}