package ru.job4j.dreamjob.store;

import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.Main;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

public class CandidateDBStoreTest {

    @Test
    public void whenCreateCandidate() {
        CandidateDBStore store = new CandidateDBStore(new Main().loadPool());
        Candidate candidate = new Candidate(
                0,
                "Java Middle",
                "",
                LocalDateTime.now(),
                new City(0, ""),
                new byte[0]
        );
        store.add(candidate);
        Candidate candidateInDb = store.findById(candidate.getId());
        assertThat(candidateInDb.getName()).isEqualTo(candidate.getName());
    }

    @Test
    public void whenUpdateCandidate() {
        CandidateDBStore store = new CandidateDBStore(new Main().loadPool());
        Candidate candidate = new Candidate(
                0,
                "Java Middle",
                "",
                LocalDateTime.now(),
                new City(0, ""),
                new byte[0]
        );
        store.add(candidate);
        candidate.setName("Java Senior");
        boolean updateSuccess = store.update(candidate);
        Candidate candidateInDb = store.findById(candidate.getId());
        assertThat(updateSuccess).isTrue();
        assertThat(candidateInDb.getName()).isEqualTo(candidate.getName());
    }
}