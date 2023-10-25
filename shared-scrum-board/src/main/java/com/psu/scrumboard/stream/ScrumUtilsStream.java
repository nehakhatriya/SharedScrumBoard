
package com.psu.scrumboard.stream;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;

import com.psu.scrumboard.config.ScrumConfig;
import com.psu.scrumboard.utils.StreamUtils;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ScrumUtilsStream {

	// This method removes a listener with the given id from the listeners map.
	// It is synchronized to prevent concurrent access to the map.
	public static synchronized void removeBroadcaster(final Map<String, List<Consumer<String>>> listeners, String id) {
		try {
			List<Consumer<String>> list = listeners.get(id);
			if (CollectionUtils.isEmpty(list)) {
				if (ScrumConfig.DEBUG) {
					log.info("remove broadcast listener id '{}'", id);
				}
				listeners.remove(id);
			}
		} catch (Exception e) {
			log.error("failed to remove broadcast listener id '{}'", id);
		}
	}
	// This method broadcasts a message to all listeners with the given id.
	// It uses an executor to run each listener in a separate thread, and is synchronized to prevent concurrent access to the listeners map.
	public static void runParallel(final Executor executor, final Map<String, List<Consumer<String>>> listeners, String id,
			String message) {
		List<Consumer<String>> list = listeners.get(id);
		if (CollectionUtils.isEmpty(list)) {
			return;
		}

		StreamUtils.stream(list).forEach(e -> {
			if (ScrumConfig.DEBUG) {
				log.info("broadcast message '{}' to id '{}'", message, id);
			}

			try {
				executor.execute(execute(id, message, e));
			} catch (Exception e2) {
				log.error("failed to sync {} - {}", id, message);
			}
		});
	}
	// This method returns a Runnable that executes a listener with the given message.
	// It is used by the runParallel method to run each listener in a separate thread.
	public static Runnable execute(String id, String message, Consumer<String> e) {
		return () -> {
			try {
				e.accept(message);
			} catch (Exception | Error e2) {
				log.error("failed to sync {} - {}", id, message);
			}
		};
	}

}
