package com.psu.scrumboard.views.dialogs;

import java.util.function.Consumer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

public class ScrumTextArea extends Dialog {

	private static final long serialVersionUID = -5714183761044782095L;

	private VerticalLayout layout;
	@Getter
	private TextArea textArea;

	public ScrumTextArea(String caption, String initValue, Consumer<String> text) {
		this(caption, Strings.EMPTY, initValue, text);
	}

	public ScrumTextArea(String caption, String placeholder, String initValue, Consumer<String> text) {
		layout = new VerticalLayout();
		add(layout);
		setWidth("400px");
		setMinHeight("300px");

		H3 captionLabel = new H3(caption);
		layout.add(captionLabel);
		layout.setAlignSelf(Alignment.CENTER, captionLabel);

		textArea = new TextArea();
		textArea.setWidthFull();
		layout.add(textArea);
		textArea.setValue(initValue);
		textArea.setPlaceholder(placeholder);
	    Icon check=VaadinIcon.CHECK.create();
	    check.getStyle().set("color", "green");
		Button save = new Button(check);
		save.setWidthFull();
		save.addClickListener(e -> {
			text.accept(textArea.getValue());
			close();
		});
	    Icon closee=VaadinIcon.CLOSE.create();
	    closee.getStyle().set("color", "red");
		Button close = new Button(closee);
		close.setWidthFull();
		close.addClickListener(e -> {
			close();
		});
		HorizontalLayout footer = new HorizontalLayout(close, save);
		footer.setWidthFull();
		layout.add(footer);

		textArea.focus();
	}

}
