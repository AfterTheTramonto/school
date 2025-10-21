package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAge(int age);
    List<Student> findByAgeBetween(int minAge, int maxAge);
    List<Student> findByNameContainingIgnoreCase(String namePart);

    @Query("SELECT s FROM Student s WHERE s.age < s.id")
    List<Student> findByAgeLessThanIdCustom();

    List<Student> findAllByOrderByAgeAsc();

    List<Student> findByFacultyId(Long facultyId);
}