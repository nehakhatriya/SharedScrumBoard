package com.psu.scrumboard.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.psu.scrumboard.data.table.ScrumCardComment;

@Repository
public interface ScrumCardCommentRepository extends CrudRepository<ScrumCardComment, String> {

}


