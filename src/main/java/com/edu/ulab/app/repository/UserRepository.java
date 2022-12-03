package com.edu.ulab.app.repository;

import com.edu.ulab.app.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Person, Integer> {

    /*
    User has books - book - started - comited status - other logic
    User has books - book - in progress
    User has books - book - finished
     */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Person p where p.id = :id")
    Optional<Person> findByIdForUpdate(Integer id);
}
