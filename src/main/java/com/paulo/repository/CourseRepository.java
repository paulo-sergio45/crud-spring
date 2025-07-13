package com.paulo.repository;

import com.paulo.enums.Status;
import com.paulo.model.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.Iterable;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    //JPA Query Methods in Spring
    Optional<Course> findByIdAndStatus(Long Id, Status status);

    Iterable<Course> findByStatus( Status status);

    Iterable<Course> findByStatus(Sort sort, Status status);

    Page<Course> findByStatus(Pageable pageable, Status status);
}
