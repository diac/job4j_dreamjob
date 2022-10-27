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

    private final BasicDataSource pool;

    private static final Logger LOG = LogManager.getLogger(CandidateDBStore.class.getName());

    public CandidateDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Candidate> findAll() {
        List<Candidate> candidates = new ArrayList<>();
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement("SELECT * FROM candidate")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(new Candidate(
                            it.getInt("id"),
                            it.getString("name"),
                            it.getString("desc"),
                            it.getTimestamp("created").toLocalDateTime(),
                            new City(it.getInt("city_id"), null)
                    ));
                }
            }
        } catch (SQLException e) {
            LOG.warn(e.getMessage());
        }
        return candidates;
    }

    public Candidate add(Candidate candidate) {
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement("""
                        INSERT INTO
                            candidate(name, "desc", city_id, photo, created)
                        VALUES (?, ?, ?, ?, NOW())
                        """)
        ) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDesc());
            if (candidate.getCity() != null) {
                ps.setInt(3, candidate.getCity().getId());
            } else {
                ps.setNull(3, Types.NULL);
            }
            ps.setBytes(4, candidate.getPhoto());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOG.warn(e.getMessage());
        }
        return candidate;
    }

    public Candidate update(Candidate candidate) {
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement("""
                        UPDATE
                            candidate
                        SET
                            name = ?,
                            "desc" = ?,
                            city_id = ?,
                            photo = ?
                        WHERE
                            id = ?
                        """)
        ) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDesc());
            if (candidate.getCity() != null) {
                ps.setInt(3, candidate.getCity().getId());
            } else {
                ps.setNull(3, Types.NULL);
            }
            ps.setBytes(4, candidate.getPhoto());
            ps.setInt(5, candidate.getId());
            ps.execute();
        } catch (SQLException e) {
            LOG.warn(e.getMessage());
        }
        return findById(candidate.getId());
    }

    public Candidate findById(int id) {
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement("SELECT * FROM candidate WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    Candidate candidate = new Candidate(
                            it.getInt("id"),
                            it.getString("name"),
                            it.getString("desc"),
                            it.getTimestamp("created").toLocalDateTime(),
                            new City(it.getInt("city_id"), null)
                    );
                    candidate.setPhoto(it.getBytes("photo"));
                    return candidate;
                }
            }
        } catch (SQLException e) {
            LOG.warn(e.getMessage());
        }
        return null;
    }
}