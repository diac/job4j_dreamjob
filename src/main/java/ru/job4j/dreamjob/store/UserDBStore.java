package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@ThreadSafe
public final class UserDBStore {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";

    private static final String ADD_QUERY = """
            INSERT INTO
                users(email, password)
            VALUES (?, ?)
            """;

    private static final String UPDATE_QUERY = """
            UPDATE
                users
            SET
                email = ?,
                password = ?
            WHERE
                id = ?""";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";

    private final BasicDataSource pool;

    private static final Logger LOG = LogManager.getLogger(UserDBStore.class.getName());

    public UserDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement(FIND_ALL_QUERY)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                users.add(new User(
                        it.getInt("id"),
                        it.getString("email"),
                        it.getString("password")
                ));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return users;
    }

    public Optional<User> add(User user) {
        Optional<User> result = Optional.empty();
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement(ADD_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
            result = Optional.of(user);
        } catch (SQLException e) {
            LOG.error(e);
        }
        return result;
    }

    public User update(User user) {
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement(UPDATE_QUERY)
        ) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getId());
            ps.execute();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return findById(user.getId());
    }

    public User findById(int id) {
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement(FIND_BY_ID_QUERY)
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new User(
                            it.getInt("id"),
                            it.getString("email"),
                            it.getString("password")
                    );
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return null;
    }
}