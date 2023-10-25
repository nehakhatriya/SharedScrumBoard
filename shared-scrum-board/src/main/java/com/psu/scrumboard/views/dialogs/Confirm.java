package com.psu.scrumboard.views.dialogs;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Confirm extends Dialog {

	private static final long serialVersionUID = 3160984240233717631L;

	public Confirm(String title, Runnable ok) {
		H3 lbl = new H3(title);
		Icon check=VaadinIcon.CHECK.create();
		check.getStyle().set("color", "green");
		Button btnOk = new Button(check);
		btnOk.addClickShortcut(Key.ENTER);
		btnOk.addClickListener(e -> {
			ok.run();
			close();
		});
		Icon close=VaadinIcon.CLOSE.create();
		close.getStyle().set("color", "red");
		Button btnCancel = new Button(close);
		btnCancel.addClickListener(e -> {
			close();
		});

		HorizontalLayout l = new HorizontalLayout(btnCancel, btnOk);
		l.setJustifyContentMode(JustifyContentMode.END);
		l.setWidthFull();
		l.setMargin(true);
		add(l);

		VerticalLayout root = new VerticalLayout(lbl, l);
		root.setWidth("350px");
		add(root);
	}

}
