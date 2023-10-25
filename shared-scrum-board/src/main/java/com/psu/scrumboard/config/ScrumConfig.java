package com.psu.scrumboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ScrumConfig {

	public static boolean DEBUG;

	public static int NOTIF_TIME = 2000; 
	
	@Value("${app.debug: false}")
	public void setMAX_COLUMNS(boolean DEBUG) {
		ScrumConfig.DEBUG = DEBUG;
	}
}
