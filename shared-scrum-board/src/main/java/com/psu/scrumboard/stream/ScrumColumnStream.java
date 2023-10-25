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
public class ScrumColumnStream {

	public static final String MESSAGE_SORT = "sort";
	public static final String ADD_COLUMN = "addcolumn.";
	
	// Executor for running tasks in parallel
	private static final Executor EXECUTOR = Executors.newCachedThreadPool();
	private static final Map<String, List<Consumer<String>>> LISTENERS = Maps.newLinkedHashMap();

	public static synchronized Registration register(String id, Consumer<String> listener) {
		if (ScrumConfig.DEBUG) {
			log.info("registering column consumer for: " + id);
		}

		LISTENERS.putIfAbsent(id, Lists.newLinkedList());
		// Adds the listener to the list of listeners for the ID
		LISTENERS.get(id).add(listener);

		return () -> {
			ScrumUtilsStream.removeBroadcaster(LISTENERS, id);
		};
	}
	// Broadcasts a message to all listeners for a given ID
	public static synchronized void broadcast(String id, String message) {
		ScrumUtilsStream.runParallel(EXECUTOR, LISTENERS, id, message);
	}

	public static synchronized void broadcastAddColumn(String id, String message) {
		ScrumUtilsStream.runParallel(EXECUTOR, LISTENERS, id, ADD_COLUMN + message);
	}

}
