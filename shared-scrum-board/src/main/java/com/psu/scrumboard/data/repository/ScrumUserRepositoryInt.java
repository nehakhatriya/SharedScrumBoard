package com.psu.scrumboard.data.repository;

import com.psu.scrumboard.data.table.ScrumBoardUser;
import com.psu.scrumboard.model.ScrumBoardCurrentUser;

public interface ScrumUserRepositoryInt {

	ScrumBoardUser insertUserById(String dataUserId, ScrumBoardCurrentUser user);

	ScrumBoardUser deleteUserById(String dataUserId, String userId);
	
	int countByDataId(String dataId);

}