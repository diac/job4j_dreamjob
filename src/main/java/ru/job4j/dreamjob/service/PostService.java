package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.store.PostDBStore;

import java.util.List;

@Service
@ThreadSafe
public final class PostService {

    private final PostDBStore store;

    public PostService(PostDBStore store) {
        this.store = store;
    }

    public List<Post> findAll() {
        return store.findAll().stream().toList();
    }

    public Post add(Post post) {
        return store.add(post);
    }

    public Post findById(int id) {
        return store.findById(id);
    }

    public Post update(Post post) {
        return store.update(post);
    }
}