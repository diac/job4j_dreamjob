package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@ThreadSafe
public final class PostDBStore {

    private static final String FIND_ALL_QUERY = "SELECT * FROM post";

    private static final String ADD_QUERY = """
            INSERT INTO
               post(name, description, visible, city_id, created)
            VALUES (?, ?, ?, ?, NOW())""";

    private static final String UPDATE_QUERY = """
            UPDATE
                post
            SET
                name = ?,
                description = ?,
                visible = ?,
                city_id = ?
            WHERE
                id = ?
            """;

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM post WHERE id = ?";

    private final BasicDataSource pool;

    private static final Logger LOG = LogManager.getLogger(PostDBStore.class.getName());

    public PostDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(FIND_ALL_QUERY)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(postFromResultSet(it));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return posts;
    }

    public Post add(Post post) {
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement(ADD_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setBoolean(3, post.isVisible());
            if (post.getCity() != null) {
                ps.setInt(4, post.getCity().getId());
            } else {
                ps.setNull(4, Types.NULL);
            }
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return post;
    }

    public boolean update(Post post) {
        boolean result = false;
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement(UPDATE_QUERY)
        ) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setBoolean(3, post.isVisible());
            if (post.getCity() != null) {
                ps.setInt(4, post.getCity().getId());
            } else {
                ps.setNull(4, Types.NULL);
            }
            ps.setInt(5, post.getId());
            result = (ps.executeUpdate() > 0);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return result;
    }

    public Post findById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(FIND_BY_ID_QUERY)
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return postFromResultSet(it);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    private Post postFromResultSet(ResultSet it) {
        Post post = null;
        try {
            post = new Post(
                    it.getInt("id"),
                    it.getString("name"),
                    it.getString("description"),
                    it.getTimestamp("created").toLocalDateTime(),
                    it.getBoolean("visible"),
                    new City(it.getInt("city_id"), null)
            );
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return post;
    }
}