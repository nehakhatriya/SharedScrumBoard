package com.psu.scrumboard.data.repository;

import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.psu.scrumboard.data.table.ScrumBoardUser;
import com.psu.scrumboard.model.ScrumBoardCurrentUser;

public class ScrumUserRepositoryIntImpl implements ScrumUserRepositoryInt {

	@Autowired
	@Lazy
	private ScrumUserRepository repository;

	@Override
	public ScrumBoardUser insertUserById(String dataUserId, ScrumBoardCurrentUser user) {
		Optional<ScrumBoardUser> data = repository.findById(dataUserId);
		if (data.isPresent()) {
			if (!data.get().getUsers().contains(user)) {
				data.get().getUsers().add(user);
				return repository.save(data.get());
			}
		}

		return null;
	}

	@Override
	public ScrumBoardUser deleteUserById(String dataUserId, String userId) {
		if (dataUserId == null) {
			return null;
		}

		Optional<ScrumBoardUser> data = repository.findById(dataUserId);
		if (data.isPresent()) {
			data.get().getUsers().removeIf(e -> StringUtils.equals(e.getId(), userId));
			return repository.save(data.get());
		}

		return null;
	}

	@Override
	public int countByDataId(String dataId) {
		ScrumBoardUser user = repository.findByDataIdFetched(dataId);
		if (user == null) {
			return 0;
		}

		return CollectionUtils.size(user.getUsers());
	}

}
