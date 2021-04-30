package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.components.StringFormattingService;
import com.leoschulmann.roboquote.WebFront.events.GridChangedQtyClickedEvent;
import com.leoschulmann.roboquote.WebFront.events.GridDeletedPositionClickedEvent;
import com.leoschulmann.roboquote.WebFront.events.RedrawGridAndSubtotalsEvent;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.format.AmountFormatParams;
import org.javamoney.moneta.format.CurrencyStyle;

import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.vaadin.flow.component.grid.GridVariant.*;

public class SectionGrid extends Grid<ItemPosition> {
    private final List<HasEnabled> clickables = new ArrayList<>();
    private ItemPosition draggedItem;
    private static final MonetaryAmountFormat FORMATTER;

    @Getter
    private final QuoteSection quoteSection;

    @Getter
    private final Footer footer;

    @Getter
    @Setter
    private boolean textWrap = true;

    static {
        FORMATTER = MonetaryFormats.getAmountFormat(
                AmountFormatQueryBuilder
                        .of(Locale.FRANCE)
                        .set(CurrencyStyle.SYMBOL)
                        .set(AmountFormatParams.PATTERN, "#,###,###.## ¤")
                        .build());
    }


    public SectionGrid(QuoteSection qs, StringFormattingService stringFormattingService) {
        super(ItemPosition.class);
        this.quoteSection = qs;
        setDataProvider(new ListDataProvider<>(quoteSection.getPositions()));

        removeAllColumns();
        addThemeVariants(LUMO_ROW_STRIPES, LUMO_WRAP_CELL_CONTENT, LUMO_COLUMN_BORDERS, LUMO_COMPACT);
        addComponentColumn(this::createDeleteButton).setSortable(false).setAutoWidth(true).setFlexGrow(0);
        addColumn(ItemPosition::getName).setHeader("Item name").setSortable(false).setFlexGrow(1).setResizable(true);
        addComponentColumn(this::getQuantityField).setHeader("Quantity").setSortable(false).setAutoWidth(true).setFlexGrow(0);
        addColumn(ItemPosition::getPartNo).setHeader("Part No").setSortable(false).setWidth("6em").setFlexGrow(0);
        addColumn(ip -> FORMATTER.format(ip.getSellingPrice())).setHeader("Price").setWidth("8em").setFlexGrow(0);
        addColumn(ip -> FORMATTER.format(ip.getSellingSum())).setHeader("Sum").setWidth("8em").setFlexGrow(0);
        setHeightByRows(true);

        setupDragNdrop();

        footer = new Footer(this, stringFormattingService);
    }

    private void setupDragNdrop() {
        setSelectionMode(SelectionMode.NONE);
        setRowsDraggable(true);

        addDragStartListener(event -> {
            draggedItem = event.getDraggedItems().get(0);
            setDropMode(GridDropMode.BETWEEN);
        });

        addDropListener(event -> {
            ItemPosition dropOverItem = event.getDropTargetItem().get();
            if (!dropOverItem.equals(draggedItem)) {
                quoteSection.getPositions().remove(draggedItem);
                int dropIndex = quoteSection.getPositions().indexOf(dropOverItem)
                        + (event.getDropLocation() == GridDropLocation.BELOW ? 1 : 0);
                quoteSection.getPositions().add(dropIndex, draggedItem);
                getDataProvider().refreshAll();
            }
        });

        addDragEndListener(event -> {
            draggedItem = null;
            setDropMode(null);
        });
    }

    private Component getQuantityField(ItemPosition itemPosition) {
        IntegerField field = new IntegerField();
        field.setWidth("6em");
        field.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        field.setMax(99);
        field.setMin(1);
        field.setHasControls(true);
        field.setValue(itemPosition.getQty());
        field.addValueChangeListener(event -> {
            int qty = field.getValue();
            fireEvent(new GridChangedQtyClickedEvent(this, qty, itemPosition));
        });
        clickables.add(field);
        return field;
    }

    private Component createDeleteButton(ItemPosition itemPosition) {
        Button deleteItemPositionBtn = new Button(VaadinIcon.CLOSE_SMALL.create());
        deleteItemPositionBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);

        deleteItemPositionBtn.addClickListener(c -> {
            fireEvent(new GridDeletedPositionClickedEvent(this, itemPosition));
        });
        clickables.add(deleteItemPositionBtn);
        return deleteItemPositionBtn;
    }

    public void disableClickables() {
        clickables.stream().filter(Objects::nonNull).forEach(c -> c.setEnabled(false));
        setRowsDraggable(false);
    }

    @Override
    public String toString() {
        return quoteSection.getName();
    }

    String getName() {
        return quoteSection.getName();
    }

    private void redrawFooter() {
        footer.update();
    }

    public void update(RedrawGridAndSubtotalsEvent event) {
        getDataProvider().refreshAll();
        redrawFooter();
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
