package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Post;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public final class PostStore {

    private final AtomicInteger ids = new AtomicInteger(0);

    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();

    private PostStore() {
        add(new Post(1, "Junior Java Job", "Junior", LocalDateTime.now(), true));
        add(new Post(2, "Middle Java Job", "Middle", LocalDateTime.now(), true));
        add(new Post(3, "Senior Java Job", "Senior", LocalDateTime.now(), true));
    }

    public Collection<Post> findAll() {
        return posts.values();
    }


    public Post add(Post post) {
        post.setId(ids.incrementAndGet());
        return posts.putIfAbsent(post.getId(), post);
   }

    public Post findById(int id) {
        return posts.get(id);
    }

    public Post update(Post post) {
        return posts.replace(post.getId(), post);
    }
}