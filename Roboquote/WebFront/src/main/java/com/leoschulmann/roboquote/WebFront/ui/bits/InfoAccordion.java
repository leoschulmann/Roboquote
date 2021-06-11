package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.events.DisableClickableComponents;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

@Getter
public class InfoAccordion extends Accordion {
    private final FormLayout columnLayout;
    private final ComboBox<String> customer;
    private final TextField customerInfo;
    private final ComboBox<String> dealer;
    private final TextField dealerInfo;
    private final ComboBox<String> paymentTerms;
    private final ComboBox<String> shippingTerms;
    private final ComboBox<String> warranty;
    private final ComboBox<String> installation;
    private final TextField comment;
    private final DatePicker validThru;
    private final TextField invisibleSerialField;

    public InfoAccordion() {
        setWidthFull();
        columnLayout = new FormLayout();
        columnLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));

        customer = new ComboBox<>("Customer");
        customer.setAllowCustomValue(true);
        customer.setClearButtonVisible(true);
        customer.addCustomValueSetListener(e -> customer.setValue(e.getDetail()));

        customerInfo = new TextField("Customer info");

        dealer = new ComboBox<>("Dealer");
        dealer.setAllowCustomValue(true);
        dealer.setClearButtonVisible(true);
        dealer.addCustomValueSetListener(e -> dealer.setValue(e.getDetail()));

        dealerInfo = new TextField("Dealer info");

        paymentTerms = new ComboBox<>("Payment Terms");
        paymentTerms.setAllowCustomValue(true);
        paymentTerms.setClearButtonVisible(true);
        paymentTerms.addCustomValueSetListener(e -> paymentTerms.setValue(e.getDetail()));

        shippingTerms = new ComboBox<>("Shipping Terms");
        shippingTerms.setAllowCustomValue(true);
        shippingTerms.setClearButtonVisible(true);
        shippingTerms.addCustomValueSetListener(e -> shippingTerms.setValue(e.getDetail()));

        warranty = new ComboBox<>("Warranty");
        warranty.setAllowCustomValue(true);
        warranty.setClearButtonVisible(true);
        warranty.addCustomValueSetListener(e -> warranty.setValue(e.getDetail()));

        installation = new ComboBox<>("Installation");
        installation.setAllowCustomValue(true);
        installation.setClearButtonVisible(true);
        installation.addCustomValueSetListener(e -> installation.setValue(e.getDetail()));

        comment = new TextField("Comment");
        validThru = new DatePicker("Valid through date");

        invisibleSerialField = new TextField();

        columnLayout.add(customer);
        columnLayout.add(customerInfo, 2);
        columnLayout.add(dealer);
        columnLayout.add(dealerInfo, 2);
        columnLayout.add(paymentTerms, shippingTerms, warranty, installation, validThru, comment);
        add("Quote details", columnLayout);
    }

    public void addRatesBlock(RatesPanel ratesPanel) {
        columnLayout.add(ratesPanel, 3);
    }

    public void disable(DisableClickableComponents e) {
        columnLayout.setEnabled(false);
    }
}
