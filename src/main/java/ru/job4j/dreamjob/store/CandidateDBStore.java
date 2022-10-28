package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@ThreadSafe
public final class CandidateDBStore {

    private static final String FIND_ALL_QUERY = "SELECT * FROM candidate";

    private static final String ADD_QUERY = """
            INSERT INTO
                candidate(name, "desc", city_id, photo, created)
            VALUES (?, ?, ?, ?, NOW())
            """;

    private static final String UPDATE_QUERY = """
            UPDATE
                candidate
            SET
                name = ?,
                "desc" = ?,
                city_id = ?,
                photo = ?
            WHERE
                id = ?
            """;

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM candidate WHERE id = ?";

    private final BasicDataSource pool;

    private static final Logger LOG = LogManager.getLogger(CandidateDBStore.class.getName());

    public CandidateDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Candidate> findAll() {
        List<Candidate> candidates = new ArrayList<>();
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement(FIND_ALL_QUERY)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(candidateFromResultset(it));
                }
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return candidates;
    }

    public Candidate add(Candidate candidate) {
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement(ADD_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDesc());
            ps.setInt(3, candidate.getCity() != null ? candidate.getCity().getId() : 0);
            ps.setBytes(4, candidate.getPhoto());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return candidate;
    }

    public boolean update(Candidate candidate) {
        boolean result = false;
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement(UPDATE_QUERY)
        ) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDesc());
            ps.setInt(3, candidate.getCity() != null ? candidate.getCity().getId() : 0);
            ps.setBytes(4, candidate.getPhoto());
            ps.setInt(5, candidate.getId());
            result = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    public Candidate findById(int id) {
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement(FIND_BY_ID_QUERY)
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return candidateFromResultset(it);
                }
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private Candidate candidateFromResultset(ResultSet it) {
        Candidate candidate = null;
        try {
            candidate = new Candidate(
                    it.getInt("id"),
                    it.getString("name"),
                    it.getString("desc"),
                    it.getTimestamp("created").toLocalDateTime(),
                    new City(it.getInt("city_id"), null),
                    it.getBytes("photo")
            );
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return candidate;
    }
}