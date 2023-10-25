package com.psu.scrumboard.views;

import com.psu.scrumboard.views.layouts.MainLayout;
import com.psu.scrumboard.data.repository.ScrummUserRepository;
import com.psu.scrumboard.data.table.ScrumUser;
import com.psu.scrumboard.utils.SpringContext;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;

/**
 * The main view is a top-level placeholder for other views.
 */
@Route(value = Strings.EMPTY, layout = MainLayout.class)
public class MainView extends VerticalLayout {

	private static final long serialVersionUID = 123456789L;
	@Autowired
	private ScrummUserRepository repository = SpringContext.getBean(ScrummUserRepository.class);
	private HorizontalLayout root;

	public MainView() {
		super();
	    setWidth("450px");
	    setHeight("700px");
		setSizeFull();
		setJustifyContentMode(JustifyContentMode.CENTER);
		root = createRootLayout();
		add(root);
		HorizontalLayout centerLayout = new HorizontalLayout();
		root.add(centerLayout);
		Button btnRegister = createRegisterBtn(root);
		Button btnLogin = createLoginBtn(root);
		centerLayout.add(btnRegister);
		centerLayout.add(btnLogin);
		root.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, centerLayout);
		root.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
	}

	private HorizontalLayout createRootLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();
		return layout;
	}

	private Button createRegisterBtn(HorizontalLayout root) {
		Button registerButton = new Button("Register");
		registerButton.getStyle().set("background-color", "#008CBA"); // Set the background color
		registerButton.getStyle().set("border-radius", "5px"); // Add border radius
		registerButton.getStyle().set("color", "white"); // Set the text color
		registerButton.getStyle().set("font-size", "16px"); // Set the font size
		registerButton.addClickListener(e -> {
			createRegistrationDialog(root);
		});
		return registerButton;
	}

	private Button createLoginBtn(HorizontalLayout root) {
		Button registerButton = new Button("Login");
		registerButton.getStyle().set("background-color", "#008CBA"); // Set the background color
		registerButton.getStyle().set("border-radius", "5px"); // Add border radius
		registerButton.getStyle().set("color", "white"); // Set the text color
		registerButton.getStyle().set("font-size", "16px"); // Set the font size
		registerButton.addClickListener(e -> {
			createLoginDialog(root);
		});
		return registerButton;
	}

	private void createRegistrationDialog(HorizontalLayout root) {
		root.removeAll();
		TextField usernameField = new TextField("Username");
		PasswordField passwordField = new PasswordField("Password");
		Button registerButton = new Button("Register");
		usernameField.setPlaceholder("Enter your username");
		usernameField.getStyle().set("background-color", "#F5F5F5"); // Set the background color
		usernameField.getStyle().set("border-radius", "5px"); // Add border radius
		passwordField.setPlaceholder("Enter your password");
		passwordField.getStyle().set("background-color", "#F5F5F5"); // Set the background color
		passwordField.getStyle().set("border-radius", "5px"); // Add border radius
		registerButton.getStyle().set("background-color", "#008CBA"); // Set the background color
		registerButton.getStyle().set("border-radius", "5px"); // Add border radius
		registerButton.getStyle().set("color", "white"); // Set the text color
		registerButton.getStyle().set("font-size", "16px"); // Set the font size
		registerButton.addClickListener(e -> {
			Notification.show("User registered successfully!");
			ScrumUser repo = repository.save(
					ScrumUser.builder().username(usernameField.getValue()).password(passwordField.getValue()).build());
			System.out.print(repository.findByUsername(repo.getUsername()));
			UI.getCurrent().navigate(RegistrationView.class);

		});
		VerticalLayout registrationLayout = new VerticalLayout();
		registrationLayout.add(usernameField, passwordField, registerButton);
		registrationLayout.setAlignItems(FlexComponent.Alignment.CENTER);
		registrationLayout.setSpacing(true);
		root.add(registrationLayout);
	}

	private void createLoginDialog(HorizontalLayout root) {
		root.removeAll();
		TextField usernameField = new TextField("Username");
		usernameField.setRequiredIndicatorVisible(true);
		PasswordField passwordField = new PasswordField("Password");
		Button registerButton = new Button("Login");
		usernameField.setPlaceholder("Enter your username");
		usernameField.getStyle().set("background-color", "#F5F5F5"); // Set the background color
		usernameField.getStyle().set("border-radius", "5px"); // Add border radius
		passwordField.setPlaceholder("Enter your password");
		passwordField.getStyle().set("background-color", "#F5F5F5"); // Set the background color
		passwordField.getStyle().set("border-radius", "5px"); // Add border radius
		registerButton.getStyle().set("background-color", "#008CBA"); // Set the background color
		registerButton.getStyle().set("border-radius", "5px"); // Add border radius
		registerButton.getStyle().set("color", "white"); // Set the text color
		registerButton.getStyle().set("font-size", "16px"); // Set the font size

		registerButton.addClickListener(e -> {

			ScrumUser repo = repository.findByUsername(usernameField.getValue());
			if (repo == null) {
				Notification.show("User do not exist!");
			} else {
				if (repo.getPassword().equals(passwordField.getValue())) {
					Notification.show("User logged in successfully!");
					UI.getCurrent().navigate(RegistrationView.class);
				}
			}

		});
		VerticalLayout registrationLayout = new VerticalLayout();
		registrationLayout.add(usernameField, passwordField, registerButton);
		registrationLayout.setAlignItems(FlexComponent.Alignment.CENTER);
		root.add(registrationLayout);
	}
}
