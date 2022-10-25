package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Candidate;

import java.sql.Date;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CandidateStore {

    private static final CandidateStore INST = new CandidateStore();

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private CandidateStore() {
        candidates.put(1, new Candidate(1, "John Smith", "Extremely capable but emotionally unstable", Date.valueOf("2022-01-01")));
        candidates.put(2, new Candidate(2, "Bob Brown", "Very disciplined but lacks ambition", Date.valueOf("2022-01-01")));
        candidates.put(3, new Candidate(3, "Bill Miller", "Mostly unreliable but has some useful connections", Date.valueOf("2022-01-01")));
    }

    public static CandidateStore instOf() {
        return INST;
    }

    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}