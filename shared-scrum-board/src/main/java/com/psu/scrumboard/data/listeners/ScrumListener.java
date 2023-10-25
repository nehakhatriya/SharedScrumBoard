package com.psu.scrumboard.data.listeners;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import com.psu.scrumboard.data.table.ScrumBoard;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ScrumListener {

	@PreRemove
	@PreUpdate
	@PrePersist
	public void onSave(ScrumBoard sb) {
		log.info("interceptor: " + sb);
	}

}
