package com.psu.scrumboard.utils;

import java.util.Collection;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.collect.Lists;

public class StreamUtils {

	@SuppressWarnings("unchecked")
	public static <T> Stream<T> stream(Collection<T> values) {
		if (CollectionUtils.isEmpty(values)) {
			return (Stream<T>) Lists.newArrayList().stream();
		}

		return values.stream();
	}
}