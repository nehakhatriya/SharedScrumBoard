package com.psu.scrumboard.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ShareComponent extends HorizontalLayout {

	private static final long serialVersionUID = 2286591255827943922L;

	private Button btn;
	private ScrumBoardClipboardHelper btnBoardIdClipboard;

	public ShareComponent(String caption, String tooltip, String contentToShare) {
		Icon share=VaadinIcon.SHARE.create();
		share.getStyle().set("color", "#008CBA");
		btn = new Button(caption, share);
		btnBoardIdClipboard = new ScrumBoardClipboardHelper(contentToShare, btn);
		ToolTip.add(btn, tooltip);

		Label txt = new Label(contentToShare);
		txt.setWidthFull();

		add(btnBoardIdClipboard, txt);
		setAlignItems(Alignment.CENTER);
	}

}
