package com.psu.scrumboard.data.repository;

import com.psu.scrumboard.data.table.ScrumColumn;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrumColumnRepository extends CrudRepository<ScrumColumn, String> {

}
