package com.psu.scrumboard.stream;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.psu.scrumboard.config.ScrumConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vaadin.flow.shared.Registration;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ScrumBoardStream {
	// Create a new thread pool for handling broadcasts
	private static final Executor EXECUTOR = Executors.newCachedThreadPool();
	// Map of listeners for each ScrumBoard
	private static final Map<String, List<Consumer<String>>> LISTENERS = Maps.newLinkedHashMap();
	// Method to register a new listener for a ScrumBoard
	public static synchronized Registration register(String id, Consumer<String> listener) {
		if (ScrumConfig.DEBUG) {
			log.info("registering scrumboard consumer for: " + id);
		}
		// Add the listener to the map of listeners for the specified ScrumBoard ID
		LISTENERS.putIfAbsent(id, Lists.newLinkedList());
		LISTENERS.get(id).add(listener);
		
		return () -> {
			ScrumUtilsStream.removeBroadcaster(LISTENERS, id);
		};
	}
	// Method to broadcast a message to all listeners for a given ScrumBoard
	public static synchronized void broadcast(String id, String message) {
		ScrumUtilsStream.runParallel(EXECUTOR, LISTENERS, id, message);
	}

}
