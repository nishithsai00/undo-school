package com.api.undo_school.repo;

import com.api.undo_school.model.Users;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users,Integer> {
    @Query("SELECT u.passcode FROM Users u Where u.id=:id")
     String findcode(@Param("id") int id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Users u WHERE u.id = :id")
   Optional<Users> findByIdWithLock(@Param("id") int id);
}
