package com.psu.scrumboard.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.psu.scrumboard.data.table.ScrumBoardOptions;

@Repository
public interface ScrumBoardOptionRepository extends CrudRepository<ScrumBoardOptions, String> {

}
