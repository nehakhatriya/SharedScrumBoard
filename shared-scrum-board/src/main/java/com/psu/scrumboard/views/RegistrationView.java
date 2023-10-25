package com.psu.scrumboard.views;

import com.psu.scrumboard.views.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * The main view is a top-level placeholder for other views.
 */
@Route(value = RegistrationView.NAME, layout = MainLayout.class)
public class RegistrationView extends VerticalLayout {

	private static final long serialVersionUID = 8874200985319706829L;
	public static final String NAME = "registration";
	private HorizontalLayout root;

	public RegistrationView() {
		super();

		setSizeFull();
		setJustifyContentMode(JustifyContentMode.CENTER);

		root = createRootLayout();
		add(root);
		
		HorizontalLayout centerLayout = new HorizontalLayout();
		root.add(centerLayout);
		centerLayout.add(new CreateScrumBoard());
		centerLayout.add(new JoinScrumBoard());
		root.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, centerLayout);
		root.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

	}

	private HorizontalLayout createRootLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();
		return layout;
	}
}
