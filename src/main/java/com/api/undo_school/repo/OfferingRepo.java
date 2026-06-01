package com.api.undo_school.repo;

import com.api.undo_school.model.Offering;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferingRepo extends JpaRepository<Offering,Integer> {


    @Query("SELECT o FROM Offering o WHERE o.offeringStartDate>:currentTime")
    List<Offering> findByCurrentDate(@Param("currentTime") Instant currentTime);
}
