package com.psu.scrumboard.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.psu.scrumboard.data.table.ScrumBoard;

@Repository
public interface ScrumBoardRepository extends CrudRepository<ScrumBoard, String>, ScrumBoardRepositoryInt {

	@Query("FROM ScrumBoard d LEFT JOIN FETCH d.columns c LEFT JOIN FETCH d.user WHERE d.id = :id")
	public ScrumBoard findByIdFetched(@Param("id") String id);

}
