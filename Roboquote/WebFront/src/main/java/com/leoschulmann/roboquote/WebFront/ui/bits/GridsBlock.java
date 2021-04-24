package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GridsBlock extends VerticalLayout {
    public GridsBlock() {
        super();
    }

    public List<SectionGrid> getGridsAsList() {
        return IntStream.range(0, getComponentCount())
                .mapToObj(index -> ((SectionAccordion) (getComponentAt(index))).getGrid())
                .collect(Collectors.toList());
    }

    public void removeGrid(SectionAccordion sectionAccordion) {
        remove(sectionAccordion);
    }

    public void moveAccordion(SectionAccordion sectionAccordion, int toIndex) {
        removeGrid(sectionAccordion);
        addComponentAtIndex(toIndex, sectionAccordion);
    }

}
