package com.psu.scrumboard.data.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.psu.scrumboard.data.table.ScrumBoard;
import com.psu.scrumboard.data.table.ScrumColumn;

public class ScrumBoardRepositoryIntImpl implements ScrumBoardRepositoryInt {

	@Autowired
	private ScrumBoardRepository repository;
	
	@Override
	@Transactional
	public ScrumBoard insertColumnById(String dataId, ScrumColumn column) {
		ScrumBoard data = repository.findById(dataId).get();
		data.getColumns().add(column);
		return data;
	}
	
}
