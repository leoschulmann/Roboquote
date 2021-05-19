package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.events.SectionDialogAddBundledSectionButtonClicked;
import com.leoschulmann.roboquote.WebFront.events.SectionDialogAddEmptySectionButtonClicked;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class AddSectionDialog extends Dialog {
    private TextField tf;
    private Button addEmptySecBtn;
    private Button addBundleBtn;
    private ComboBox<Bundle> bundles;


    public AddSectionDialog(List<Bundle> bundles) {
        FormLayout layout = new FormLayout(createEmptySectionLayout(), createSectionFromBundleLayout(bundles));
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("1em", 1),
                new FormLayout.ResponsiveStep("65em", 2));
        add(layout);
    }

    private VerticalLayout createEmptySectionLayout() {
        tf = new TextField();
        addEmptySecBtn = new Button("Add empty section");
        addEmptySecBtn.addClickListener(click -> fireEvent(
                new SectionDialogAddEmptySectionButtonClicked(this, tf.getValue().trim())));
        tf.setWidth("32em");
        tf.setClearButtonVisible(true);
        addEmptySecBtn.setEnabled(false);
        addEmptySecBtn.setWidth("32em");
        addEmptySecBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        tf.addValueChangeListener(event -> addEmptySecBtn.setEnabled(!event.getValue().isBlank()));
        return new VerticalLayout(tf, addEmptySecBtn);
    }

    private VerticalLayout createSectionFromBundleLayout(List<Bundle> allBundlesNamesAndIds) {
        bundles = new ComboBox<>();
        bundles.setItems(allBundlesNamesAndIds);
        bundles.setAllowCustomValue(false);
        bundles.setClearButtonVisible(true);
        bundles.setWidth("32em");
        addBundleBtn = new Button("Add bundle");
        addBundleBtn.addClickListener(bang -> fireEvent(new SectionDialogAddBundledSectionButtonClicked(this,
                bundles.getValue().getId())));
        addBundleBtn.setEnabled(false);
        addBundleBtn.setWidth("32em");
        addBundleBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        bundles.addValueChangeListener(event -> addBundleBtn.setEnabled(!(event.getValue() == null)));
        return new VerticalLayout(bundles, addBundleBtn);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    public void cleanInputs() {
        tf.clear();
        bundles.clear();
    }
}
