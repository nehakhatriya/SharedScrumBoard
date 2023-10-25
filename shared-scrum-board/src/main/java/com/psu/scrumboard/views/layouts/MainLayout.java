package com.psu.scrumboard.views.layouts;

import java.util.UUID;
import com.psu.scrumboard.session.SessionUtils;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import lombok.extern.log4j.Log4j2;

@SuppressWarnings("deprecation")
@Push
@Theme(value = Material.class, variant = Material.LIGHT)
@CssImport("./styles/custom-styles.css")
@CssImport(value = "./styles/custom-button-styles.css", themeFor = "vaadin-button")
@Log4j2
public class MainLayout extends VerticalLayout implements RouterLayout, PageConfigurator {
	private static final long serialVersionUID = 4630537412936320207L;
	public MainLayout() {
		super();
		setId(UUID.randomUUID().toString());
		setSizeFull();
		setPadding(false);
		SessionUtils.createSessionIdIfNotExistsExists();
		VaadinResponse.getCurrent().getService().addSessionDestroyListener(e -> {
			detach();
		});
	}
	
	@Override
	public void configurePage(InitialPageSettings settings) {
		String script = String.format(
				"window.onbeforeunload = function (e) { document.getElementById('%s').$server.detach(); };",
				getId().get());
		settings.addInlineWithContents(InitialPageSettings.Position.PREPEND, script, InitialPageSettings.WrapMode.JAVASCRIPT);
	}

	@ClientCallable
	public void detach() {
		log.info("detaching ui");
		UI.getCurrent().close();
	}
}