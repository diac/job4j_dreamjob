package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Candidate;

import java.sql.Date;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CandidateStore {

    private static final CandidateStore INST = new CandidateStore();

    private volatile int currentId = 1;

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private CandidateStore() {
        create(new Candidate(1, "John Smith", "Extremely capable but emotionally unstable", Date.valueOf("2022-01-01")));
        create(new Candidate(2, "Bob Brown", "Very disciplined but lacks ambition", Date.valueOf("2022-01-01")));
        create(new Candidate(3, "Bill Miller", "Mostly unreliable but has some useful connections", Date.valueOf("2022-01-01")));
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
}