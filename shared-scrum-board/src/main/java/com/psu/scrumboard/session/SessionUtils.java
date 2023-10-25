package com.psu.scrumboard.session;

import com.psu.scrumboard.utils.Utils;
import com.vaadin.flow.server.VaadinSession;

public class SessionUtils {
	
	public static void createSessionIdIfNotExistsExists() {
		if (getSessionId() == null) {
			VaadinSession.getCurrent().setAttribute("session-id", Utils.randomId());
		}
	}
	// get current session id for the current user
	public static String getSessionId() {
		return (String) VaadinSession.getCurrent().getAttribute("session-id");
	}

}
