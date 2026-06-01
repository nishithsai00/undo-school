package com.api.undo_school.repo;

import com.api.undo_school.model.Bookings;
import com.api.undo_school.model.Sessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Bookings,Integer> {
  List<Bookings> findByParentId_userId(int parentId);

}
