package com.leoschulmann.roboquote.WebFront.ui;

import com.vaadin.flow.component.grid.Grid;

import java.util.ArrayList;
import java.util.List;

public class SectionGrid<T> extends Grid<T> {
    private  List<T> content;


    public SectionGrid(Class<T> beanType) {
        super(beanType);
        content = new ArrayList<>();
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public void renderItems() {
        setItems(content);
    }
}
