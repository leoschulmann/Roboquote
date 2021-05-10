package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.components.DownloadService;
import com.leoschulmann.roboquote.WebFront.events.QuoteViewerCancelClicked;
import com.leoschulmann.roboquote.WebFront.events.QuoteViewerCommentClicked;
import com.leoschulmann.roboquote.WebFront.events.QuoteViewerNewVersionClicked;
import com.leoschulmann.roboquote.WebFront.events.QuoteViewerTemplateClicked;
import com.leoschulmann.roboquote.WebFront.ui.QuoteViewer;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.util.Objects;

public class QuoteViewerWrapper extends Dialog {
    private final DownloadService downloadService;

    public QuoteViewerWrapper(QuoteViewer qv, Quote quote, DownloadService downloadService) {
        this.downloadService = downloadService;
        add(qv);
        FileDownloadWrapper downloadXlsxWrapper = createDownloadWrapper(quote.getNumber(), quote.getVersion(), quote.getId());
        Button asTemplateBtn = createAsTemplateBtn(quote);
        Button appendNewVersionBtn = createNewQuoteVersion(quote.getNumber(), quote.getVersion(), quote);
        Button closeBtn = createCloseBtn();
        Button addCommentBtn = createCommentButton(quote);
        Button cancelBtn = createCancelButton(quote);
        FormLayout responsiveLayout = new FormLayout();
        responsiveLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));

        responsiveLayout.add(asTemplateBtn, appendNewVersionBtn, downloadXlsxWrapper,
                addCommentBtn, cancelBtn, closeBtn);
        add(responsiveLayout);
    }

    private Button createCancelButton(Quote quote) {
        boolean cancelAction = !quote.getCancelled();
        Button button = new Button();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        button.setText(cancelAction ? "Cancel quote" : "Uncancel quote");
        button.addClickListener(c -> {
            fireEvent(new QuoteViewerCancelClicked(this, quote.getId(), cancelAction));
            this.close();
        });
        return button;
    }

    private Button createCommentButton(Quote quote) {
        Button button = new Button("Add comment");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        Dialog dialog = new Dialog();

        TextField textField = new TextField();
        textField.setWidth("30em");
        textField.setValue(Objects.requireNonNullElse(quote.getComment(), ""));
        Button ok = new Button("OK", click -> {
            fireEvent(new QuoteViewerCommentClicked(this, quote.getId(), textField.getValue()));
            this.close();
            dialog.close();
        });
        ok.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        VerticalLayout vl = new VerticalLayout(textField, ok);
        vl.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(vl);
        button.addClickListener(c -> dialog.open());
        return button;
    }

    private FileDownloadWrapper createDownloadWrapper(String serialNumber, Integer version, int id) {
        Button downloadXlsxBtn = new Button("Download " + serialNumber + "-" + version + ".xlsx");
        downloadXlsxBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        FileDownloadWrapper wrapper = new FileDownloadWrapper(
                new StreamResource(serialNumber + "-" + version + downloadService.getExtension(),
                        () -> new ByteArrayInputStream(downloadService.downloadXlsx(id))));
        downloadXlsxBtn.setWidthFull();
        wrapper.wrapComponent(downloadXlsxBtn);
        return wrapper;
    }

    private Button createAsTemplateBtn(Quote quote) {
        Button btn = new Button("Use as a template");
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btn.addClickListener(click -> {
            fireEvent(new QuoteViewerTemplateClicked(this, quote));
            this.close();
        });
        return btn;
    }

    private Button createNewQuoteVersion(String serialNumber, Integer version, Quote quote) {
        Button btn = new Button("Create new version of " + serialNumber + "-" + version);
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btn.addClickListener(click -> {
            fireEvent(new QuoteViewerNewVersionClicked(this, quote));
            this.close();
        });
        return btn;
    }

    private Button createCloseBtn() {
        Button btn = new Button("Close");
        btn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        btn.addClickListener(click -> this.close());
        return btn;
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
