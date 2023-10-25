package com.psu.scrumboard.views.utils;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

import com.psu.scrumboard.session.SessionUtils;
import com.psu.scrumboard.data.interfaces.ScrumBoardPosition;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ScrumBoardViewUtils {

	public static boolean isAllowed(String boardOwnerId, String ownerId) {
			if (ownerId.equals(SessionUtils.getSessionId())) {
				return true;
			} else if(boardOwnerId.equals(SessionUtils.getSessionId())) {
				return true;
			}
			else {
				return false;
			}
		}

	public static HorizontalLayout createColumnLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidthFull();
		layout.setHeight("1px");
		layout.getStyle().set("flex-grow", "1");
		layout.setSpacing(true);
		return layout;
	}

	public static Label createColumnTextLabel(TextArea area) {
		Label label = new Label(area.getValue());
		label.getStyle().set("border", "2px solid black");
		return label;
	}

	public static VerticalLayout createRootLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		return layout;
	}

	public static int calculateNextPosition(Collection<? extends ScrumBoardPosition> items) {
		if (CollectionUtils.isEmpty(items)) {
			return 0;
		}

		return items.stream().map(ScrumBoardPosition::getPosition).max(Integer::compare).get() + 1;
	}

}
