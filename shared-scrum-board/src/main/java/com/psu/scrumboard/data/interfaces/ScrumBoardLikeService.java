package com.psu.scrumboard.data.interfaces;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.psu.scrumboard.data.table.ScrumCardLikes;

public interface ScrumBoardLikeService {

	public String getId();

	public void setId(String id);

	public default void removeLikeByOwnerId(String ownerId) {
		if (CollectionUtils.isEmpty(getLikes())) { // check if the list of likes is empty
			return;
		}
		// use a stream to filter the likes by owner ID, find the first match, and remove it
		getLikes().stream().filter(e -> e.getOwnerId().equals(ownerId)).findFirst().ifPresent(getLikes()::remove);
	};

	public default int cardLikesByOwnerId(String ownerId) { // default method to get the total like value for a specific owner ID
		if (CollectionUtils.isEmpty(getLikes())) {
			return 0;
		}

		return getLikes().stream().filter(e -> e.getOwnerId().equals(ownerId)).mapToInt(ScrumCardLikes::getLikeValue).sum();
	};

	public default int countAllLikes() { // default method to get the total number of likes
		if (CollectionUtils.isEmpty(getLikes())) {
			return 0;
		}

		return getLikes().stream().mapToInt(ScrumCardLikes::getLikeValue).sum();
	};

	public default void clear() { // default method to clear all likes
		if (CollectionUtils.isEmpty(getLikes())) {
			return;
		}

		getLikes().clear();
	}

	public List<ScrumCardLikes> getLikes();

}
