package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.Getter;
import org.vaadin.klaudeta.PaginatedGrid;

@Getter
public class ZoomButtons {
    private int textSize = 14;
    private final Button zoomIn;
    private final Button zoomOut;

    public <T> ZoomButtons(PaginatedGrid<T> grid) {

        grid.getStyle().set("font-size", textSize + "px");

        zoomIn = new Button(VaadinIcon.SEARCH_PLUS.create());
        zoomIn.getElement().setProperty("title", "Bigger font");

        zoomOut = new Button(VaadinIcon.SEARCH_MINUS.create());
        zoomOut.getElement().setProperty("title", "Smaller font");

        zoomIn.addClickListener(c -> {
            if (textSize <= 20) {
                grid.getStyle().set("font-size", ++textSize + "px");
            }
        });
        zoomOut.addClickListener(c -> {
            if (textSize >= 8) {
                grid.getStyle().set("font-size", --textSize + "px");
            }
        });
    }
}
