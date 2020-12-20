package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.security.User;
import com.leoschulmann.roboquote.WebFront.security.UserDao;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Route(value = "", layout = MainLayout.class)
public class Greeting extends VerticalLayout {

    public Greeting(UserDao userDao) {
        String pass;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();

        Optional<User> o = userDao.getByUserName(login);

        String role = o.isPresent() ? o.get().getRole() : "0";

        add(new H2("Hello, " + role + " " + login + "!"));
    }
}
