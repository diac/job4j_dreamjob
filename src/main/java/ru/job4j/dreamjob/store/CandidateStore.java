package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public final class CandidateStore {

    private final AtomicInteger ids = new AtomicInteger(0);

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private CandidateStore() {
        add(new Candidate(1, "John Smith", "Extremely capable but emotionally unstable", LocalDateTime.now(), new City(1, "Москва")));
        add(new Candidate(2, "Bob Brown", "Very disciplined but lacks ambition", LocalDateTime.now(), new City(2, "СПб")));
        add(new Candidate(3, "Bill Miller", "Mostly unreliable but has some useful connections", LocalDateTime.now(), new City(3, "Екб")));
    }

    public Collection<Candidate> findAll() {
        return candidates.values();
    }

    public Candidate add(Candidate candidate) {
        candidate.setId(ids.incrementAndGet());
        return candidates.putIfAbsent(candidate.getId(), candidate);

    }

    public Candidate findById(int id) {
        return candidates.get(id);
    }

    public Candidate update(Candidate candidate) {
        return candidates.replace(candidate.getId(), candidate);
    }
}