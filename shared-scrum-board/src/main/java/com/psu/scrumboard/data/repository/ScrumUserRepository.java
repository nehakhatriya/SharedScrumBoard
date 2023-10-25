package com.psu.scrumboard.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.psu.scrumboard.data.table.ScrumBoardUser;

@Repository
public interface ScrumUserRepository extends CrudRepository<ScrumBoardUser, String>, ScrumUserRepositoryInt { // 

	@Query("SELECT u FROM ScrumBoard d RIGHT JOIN d.user u WHERE d.id = :id")
	public ScrumBoardUser findByDataIdFetched(@Param("id") String dataId);

}
