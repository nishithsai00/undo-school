package com.api.undo_school.repo;

import com.api.undo_school.model.Sessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SessionsRepo extends JpaRepository<Sessions,Integer> {

    @Query("SELECT s FROM Sessions s JOIN s.offering o WHERE s.teacherId.userId = :teacherId AND s.startTime < :endTime AND s.endTime > :startTime")
    List<Sessions> findOverLappingSession(@Param("teacherId") int teacherId,@Param("startTime") Instant startTime,@Param("endTime") Instant endTime);

    List<Sessions> findByTeacherIdOrderByStartTimeAsc(int id);

    @Query("SELECT s FROM Sessions s WHERE s.teacherId.userId = :teacherId AND s.startTime > :now AND s.offering.status != com.api.undo_school.model.Status.cancelled ORDER BY s.startTime ASC")
    List<Sessions> findUpcomingSessionsByTeacher(@Param("teacherId") int teacherId,@Param("now") Instant now);

    List<Sessions> findByOffering_offeringId(int offeringId);

    @Query("SELECT s FROM Sessions s JOIN s.offering o JOIN o.bookings b WHERE b.parentId.userId = :parentId AND b.status = com.api.undo_school.model.Status.conformed AND s.startTime < :endTime AND s.endTime > :startTime")
    List<Sessions> findConflictingSessionsForParent(@Param("parentId") int parentId,@Param("startTime") Instant startTime,@Param("endTime") Instant endTime);

}
