package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.List;

public class GridSizeCombobox extends ComboBox<String> {
    public <T> GridSizeCombobox(PaginatedGrid<T> grid, List<T> data) {
        setItems("15", "50", "100", "all");
        addValueChangeListener(l -> {
            if (l.getValue().equals("all")) grid.setPageSize(data.size());
            else grid.setPageSize(Integer.parseInt(l.getValue()));
        });
        setValue("15");
        setWidth("5em");
    }
}
