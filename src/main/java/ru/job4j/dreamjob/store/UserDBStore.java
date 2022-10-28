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

@Repository
@ThreadSafe
public final class UserDBStore {

    private final BasicDataSource pool;

    private static final Logger LOG = LogManager.getLogger(UserDBStore.class.getName());

    public UserDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement("SELECT * FROM users")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                users.add(new User(
                        it.getInt("id"),
                        it.getString("email"),
                        it.getString("password")
                ));
            }
        } catch (SQLException e) {
            LOG.warn(e.getMessage());
        }
        return users;
    }

    public User add(User user) {
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement("""
                                INSERT INTO
                                    users(email, password)
                                VALUES (?, ?)
                                """,
                        PreparedStatement.RETURN_GENERATED_KEYS
                )
        ) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOG.warn(e);
        }
        return user;
    }

    public User update(User user) {
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement("""
                        UPDATE
                            users
                        SET
                            email = ?,
                            password = ?
                        WHERE
                            id = ?""")
        ) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getId());
            ps.execute();
        } catch (SQLException e) {
            LOG.warn(e.getMessage());
        }
        return findById(user.getId());
    }

    public User findById(int id) {
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement("SELECT * FROM users WHERE id = ?")
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
            LOG.warn(e.getMessage());
        }
        return null;
    }
}