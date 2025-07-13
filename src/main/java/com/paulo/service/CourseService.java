package com.paulo.service;

import java.util.List;
import java.util.stream.Collectors;

import com.paulo.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.paulo.dto.CourseDTO;
import com.paulo.dto.CoursePageDTO;
import com.paulo.dto.mapper.CourseMapper;
import com.paulo.exception.RecordNotFoundException;
import com.paulo.model.Course;
import com.paulo.repository.CourseRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Validated
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    public CoursePageDTO list(@PositiveOrZero int page, @Positive @Max(100) int pageSize) {
        //  Page<Course> pageCourse = courseRepository.findAll(PageRequest.of(page, pageSize));
        Page<Course> pageCourse = courseRepository.findByStatus(PageRequest.of(page, pageSize),Status.ACTIVE);
        List<CourseDTO> courses = pageCourse.get().map(courseMapper::toDTO).collect(Collectors.toList());
        return new CoursePageDTO(courses, pageCourse.getTotalElements(), pageCourse.getTotalPages());
    }

    /*
     * public List<CourseDTO> list() {
     * return courseRepository.findAll()
     * .stream()
     * .map(courseMapper::toDTO)
     * .collect(Collectors.toList());
     * }
     */

    public CourseDTO findById(@NotNull @Positive Long id) {
      // return courseRepository.findById(id).map(courseMapper::toDTO)
        return courseRepository.findByIdAndStatus(id, Status.ACTIVE).map(courseMapper::toDTO)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    public CourseDTO create(@Valid @NotNull CourseDTO course) {
        return courseMapper.toDTO(courseRepository.save(courseMapper.toEntity(course)));
    }

    public CourseDTO update(@NotNull @Positive Long id, @Valid @NotNull CourseDTO courseDTO) {
        // return courseRepository.findById(id)
        return courseRepository.findByIdAndStatus(id,Status.ACTIVE)
                .map(recordFound -> {
                    Course course = courseMapper.toEntity(courseDTO);
                    recordFound.setName(courseDTO.name());
                    recordFound.setCategory(courseMapper.convertCategoryValue(courseDTO.category()));
                    recordFound.getLessons().clear();
                    course.getLessons().forEach(recordFound.getLessons()::add);
                    return courseMapper.toDTO(courseRepository.save(recordFound));
                }).orElseThrow(() -> new RecordNotFoundException(id));
    }

    public void delete(@NotNull @Positive Long id) {
        // courseRepository.delete(courseRepository.findById(id)
        courseRepository.delete(courseRepository.findByIdAndStatus(id,Status.ACTIVE)
                .orElseThrow(() -> new RecordNotFoundException(id)));
    }
}
