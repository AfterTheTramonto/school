package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.BadRequestException;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        if (student.getName() == null || student.getName().isBlank()) {
            throw new BadRequestException("Student name cannot be null or empty");
        }
        if (student.getAge() <= 0) {
            throw new BadRequestException("Student age must be positive");
        }
        return studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + id));
    }

    public Student updateStudent(Long id, Student student) {
        Student existingStudent = getStudentById(id); // Будет брошено исключение если не найден

        if (student.getName() != null && !student.getName().isBlank()) {
            existingStudent.setName(student.getName());
        }
        if (student.getAge() > 0) {
            existingStudent.setAge(student.getAge());
        }

        return studentRepository.save(existingStudent);
    }

    public Student deleteStudent(Long id) {
        Student student = getStudentById(id); // Будет брошено исключение если не найден
        studentRepository.deleteById(id);
        return student;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getStudentsByAge(int age) {
        if (age <= 0) {
            throw new BadRequestException("Age must be positive");
        }
        return studentRepository.findByAge(age);
    }
}