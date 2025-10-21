package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        try {
            Student createdStudent = studentService.createStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
        } catch (Exception e) {
            logger.error("Error creating student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating student: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudent(@PathVariable Long id) {
        try {
            Student student = studentService.getStudentById(id);
            if (student == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            logger.error("Error getting student with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting student: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        try {
            Student updatedStudent = studentService.updateStudent(id, student);
            if (updatedStudent == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedStudent);
        } catch (Exception e) {
            logger.error("Error updating student with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating student: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        try {
            Student deletedStudent = studentService.deleteStudent(id);
            if (deletedStudent == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(deletedStudent);
        } catch (Exception e) {
            logger.error("Error deleting student with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting student: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            logger.error("Error getting all students", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting students: " + e.getMessage());
        }
    }

    @GetMapping("/age/{age}")
    public ResponseEntity<?> getStudentsByAge(@PathVariable int age) {
        try {
            List<Student> students = studentService.getStudentsByAge(age);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            logger.error("Error getting students by age: {}", age, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting students by age: " + e.getMessage());
        }
    }

    @GetMapping("/age-between")
    public ResponseEntity<?> getStudentsByAgeBetween(@RequestParam int min, @RequestParam int max) {
        try {
            List<Student> students = studentService.getStudentsByAgeBetween(min, max);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            logger.error("Error getting students by age between {} and {}", min, max, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting students by age range: " + e.getMessage());
        }
    }

    @GetMapping("/name-contains")
    public ResponseEntity<?> getStudentsByNameContains(@RequestParam String namePart) {
        try {
            List<Student> students = studentService.getStudentsByNameContains(namePart);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            logger.error("Error getting students by name containing: {}", namePart, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting students by name: " + e.getMessage());
        }
    }

    @GetMapping("/age-less-than-id")
    public ResponseEntity<?> getStudentsByAgeLessThanId() {
        try {
            List<Student> students = studentService.getStudentsByAgeLessThanId();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            logger.error("Error getting students by age less than id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting students: " + e.getMessage());
        }
    }

    @GetMapping("/ordered-by-age")
    public ResponseEntity<?> getStudentsOrderedByAge() {
        try {
            List<Student> students = studentService.getStudentsOrderedByAge();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            logger.error("Error getting students ordered by age", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting students: " + e.getMessage());
        }
    }
    @GetMapping("/{id}/faculty")
    public ResponseEntity<?> getStudentFaculty(@PathVariable Long id) {
        try {
            Faculty faculty = studentService.getStudentFaculty(id);
            if (faculty == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(faculty);
        } catch (Exception e) {
            logger.error("Error getting faculty for student with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting student faculty: " + e.getMessage());
        }
    }
}