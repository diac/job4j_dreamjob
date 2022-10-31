package ru.job4j.dreamjob.util;

import ru.job4j.dreamjob.model.User;

import javax.servlet.http.HttpSession;

public final class Users {

    private Users() {
    }

    public static User guestUser() {
        User user = new User();
        user.setName("Гость");
        return user;
    }

    public static User fromHttpSession(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = guestUser();
        }
        return user;
    }
}