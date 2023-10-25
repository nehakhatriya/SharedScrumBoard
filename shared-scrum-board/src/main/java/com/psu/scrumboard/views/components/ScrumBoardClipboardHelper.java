package com.psu.scrumboard.views.components;

import org.vaadin.olli.ClipboardHelper;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;

public class ScrumBoardClipboardHelper extends ClipboardHelper implements HasStyle {

	private static final long serialVersionUID = 2423347915842009494L;

	public ScrumBoardClipboardHelper(String content, Component component) {
		super(content, component);
	}

}
