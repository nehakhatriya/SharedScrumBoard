package com.psu.scrumboard.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.psu.scrumboard.data.table.ScrumUser;

@Repository
public interface ScrummUserRepository extends CrudRepository<ScrumUser, String> {
    ScrumUser findByUsername(String username); //find user by username
}

