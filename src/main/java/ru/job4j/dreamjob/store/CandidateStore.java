package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CandidateStore {

    private static final CandidateStore INST = new CandidateStore();

    private volatile int currentId = 1;

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private CandidateStore() {
        create(new Candidate(1, "John Smith", "Extremely capable but emotionally unstable", LocalDateTime.now()));
        create(new Candidate(2, "Bob Brown", "Very disciplined but lacks ambition", LocalDateTime.now()));
        create(new Candidate(3, "Bill Miller", "Mostly unreliable but has some useful connections", LocalDateTime.now()));
    }

    public static CandidateStore instOf() {
        return INST;
    }

    public Collection<Candidate> findAll() {
        return candidates.values();
    }

    public synchronized Candidate create(Candidate candidate) {
        candidate.setId(currentId);
        return candidates.putIfAbsent(currentId++, candidate);
    }

    public Candidate add(Candidate candidate) {
        return candidates.putIfAbsent(candidate.getId(), candidate);
    }

    public Candidate findById(int id) {
        return candidates.get(id);
    }

    public Candidate update(Candidate candidate) {
        return candidates.replace(candidate.getId(), candidate);
    }
}