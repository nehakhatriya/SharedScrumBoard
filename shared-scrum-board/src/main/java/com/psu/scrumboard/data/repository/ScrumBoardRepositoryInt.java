package com.psu.scrumboard.data.repository;

import com.psu.scrumboard.data.table.ScrumBoard;
import com.psu.scrumboard.data.table.ScrumColumn;

public interface ScrumBoardRepositoryInt {

	ScrumBoard insertColumnById(String dataId, ScrumColumn column); // method to insert a ScrumColumn into a ScrumBoard by ID

}