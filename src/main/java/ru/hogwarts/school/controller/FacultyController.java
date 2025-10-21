package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private static final Logger logger = LoggerFactory.getLogger(FacultyController.class);
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public ResponseEntity<?> createFaculty(@RequestBody Faculty faculty) {
        try {
            Faculty createdFaculty = facultyService.createFaculty(faculty);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFaculty);
        } catch (Exception e) {
            logger.error("Error creating faculty", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating faculty: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFaculty(@PathVariable Long id) {
        try {
            Faculty faculty = facultyService.getFacultyById(id);
            if (faculty == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(faculty);
        } catch (Exception e) {
            logger.error("Error getting faculty with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting faculty: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFaculty(@PathVariable Long id, @RequestBody Faculty faculty) {
        try {
            Faculty updatedFaculty = facultyService.updateFaculty(id, faculty);
            if (updatedFaculty == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedFaculty);
        } catch (Exception e) {
            logger.error("Error updating faculty with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating faculty: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFaculty(@PathVariable Long id) {
        try {
            Faculty deletedFaculty = facultyService.deleteFaculty(id);
            if (deletedFaculty == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(deletedFaculty);
        } catch (Exception e) {
            logger.error("Error deleting faculty with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting faculty: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllFaculties() {
        try {
            List<Faculty> faculties = facultyService.getAllFaculties();
            return ResponseEntity.ok(faculties);
        } catch (Exception e) {
            logger.error("Error getting all faculties", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting faculties: " + e.getMessage());
        }
    }

    @GetMapping("/color/{color}")
    public ResponseEntity<?> getFacultiesByColor(@PathVariable String color) {
        try {
            List<Faculty> faculties = facultyService.getFacultiesByColor(color);
            return ResponseEntity.ok(faculties);
        } catch (Exception e) {
            logger.error("Error getting faculties by color: {}", color, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting faculties by color: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getFacultiesByNameOrColor(@RequestParam String search) {
        try {
            List<Faculty> faculties = facultyService.getFacultiesByNameOrColor(search);
            return ResponseEntity.ok(faculties);
        } catch (Exception e) {
            logger.error("Error searching faculties by name or color: {}", search, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching faculties: " + e.getMessage());
        }
    }
    @GetMapping("/{id}/students")
    public ResponseEntity<?> getFacultyStudents(@PathVariable Long id) {
        try {
            List<Student> students = facultyService.getFacultyStudents(id);
            if (students == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            logger.error("Error getting students for faculty with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting faculty students: " + e.getMessage());
        }
    }
}