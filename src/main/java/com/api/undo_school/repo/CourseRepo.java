package com.api.undo_school.repo;

import com.api.undo_school.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepo extends JpaRepository<Course,Integer> {
    Optional<Course> findById(int id);


}
