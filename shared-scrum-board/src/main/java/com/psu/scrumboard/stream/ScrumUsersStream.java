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
public class ScrumUsersStream {

	private static final Executor EXECUTOR = Executors.newCachedThreadPool();
	private static final Map<String, List<Consumer<String>>> LISTENERS = Maps.newLinkedHashMap();

	public static synchronized Registration register(String id, Consumer<String> listener) {
		if (ScrumConfig.DEBUG) {
			log.info("registering user consumer for: " + id);
		}

		LISTENERS.putIfAbsent(id, Lists.newLinkedList());
		LISTENERS.get(id).add(listener);

		return () -> {
			ScrumUtilsStream.removeBroadcaster(LISTENERS, id);
		};
	}

	public static synchronized void broadcast(String id, String message) {
		ScrumUtilsStream.runParallel(EXECUTOR, LISTENERS, id, message);
	}

}
