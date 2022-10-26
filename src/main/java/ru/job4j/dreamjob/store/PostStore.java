package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Post;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PostStore {

    private static final PostStore INST = new PostStore();

    private volatile int currentId = 1;

    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();

    private PostStore() {
        create(new Post(1, "Junior Java Job", "Junior", LocalDateTime.now()));
        create(new Post(2, "Middle Java Job", "Middle", LocalDateTime.now()));
        create(new Post(3, "Senior Java Job", "Senior", LocalDateTime.now()));
    }

    public static PostStore instOf() {
        return INST;
    }

    public Collection<Post> findAll() {
        return posts.values();
    }

    public synchronized Post create(Post post) {
        post.setId(currentId);
        return posts.putIfAbsent(currentId++, post);
    }
}