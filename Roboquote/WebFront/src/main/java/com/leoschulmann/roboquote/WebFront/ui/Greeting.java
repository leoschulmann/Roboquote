package com.leoschulmann.roboquote.WebFront.ui;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "", layout = MainLayout.class)
public class Greeting extends VerticalLayout {

    public Greeting() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        add(new H2("Hello, " + login + "!"));
    }
}
