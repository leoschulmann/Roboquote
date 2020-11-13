package com.leoschulmann.roboquote.WebFront.ui;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
public class Greeting extends VerticalLayout {

    public Greeting() {
        Span span = new Span("Hello world");
        add(span);
    }
}
