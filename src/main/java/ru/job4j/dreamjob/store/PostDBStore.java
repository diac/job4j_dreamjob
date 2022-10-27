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

    private final BasicDataSource pool;

    private static final Logger LOG = LogManager.getLogger(PostDBStore.class.getName());

    public PostDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(new Post(
                            it.getInt("id"),
                            it.getString("name"),
                            it.getString("description"),
                            it.getTimestamp("created").toLocalDateTime(),
                            it.getBoolean("visible"),
                            new City(it.getInt("city_id"), null)
                    ));
                }
            }
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
        return posts;
    }

    public Post add(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("""
                             INSERT INTO
                                post(name, description, visible, city_id, created)
                             VALUES (?, ?, ?, ?, NOW())""",
                     PreparedStatement.RETURN_GENERATED_KEYS)
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
            LOG.warn(e.getMessage());
        }
        return post;
    }

    public Post update(Post post) {
        try (
                Connection cn = pool.getConnection();
                PreparedStatement ps = cn.prepareStatement("""
                        UPDATE
                            post
                        SET
                            name = ?,
                            description = ?,
                            visible = ?,
                            city_id = ?
                        WHERE
                            id = ?
                        """
                )
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
            ps.execute();
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
        return findById(post.getId());
    }

    public Post findById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Post(
                            it.getInt("id"),
                            it.getString("name"),
                            it.getString("description"),
                            it.getTimestamp("created").toLocalDateTime(),
                            it.getBoolean("visible"),
                            new City(it.getInt("city_id"), null)
                    );
                }
            }
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
        return null;
    }
}