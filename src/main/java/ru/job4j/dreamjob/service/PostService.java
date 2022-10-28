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

    private final CityService cityService;

    public PostService(PostDBStore store, CityService cityService) {
        this.store = store;
        this.cityService = cityService;
    }

    public List<Post> findAll() {
        var posts = store.findAll();
        posts.forEach(
                post -> post.setCity(
                        cityService.findById(post.getCity().getId())
                )
        );
        return posts;
    }

    public Post add(Post post) {
        return store.add(post);
    }

    public Post findById(int id) {
        var post = store.findById(id);
        post.setCity(cityService.findById(post.getCity().getId()));
        return post;
    }

    public boolean update(Post post) {
        return store.update(post);
    }
}