package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hogwarts.school.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Поиск по возрасту
    List<Student> findByAge(Integer age);

    // Поиск по диапазону возрастов
    List<Student> findByAgeBetween(Integer minAge, Integer maxAge);

    // Поиск по имени (точное совпадение)
    List<Student> findByName(String name);

    // Поиск по имени (без учета регистра)
    List<Student> findByNameIgnoreCase(String name);

    // Поиск по email
    Optional<Student> findByEmail(String email);

    // Поиск студентов, у которых имя содержит подстроку
    List<Student> findByNameContainingIgnoreCase(String namePart);

    // Поиск студентов старше указанного возраста
    List<Student> findByAgeGreaterThan(Integer age);

    // Поиск студентов младше указанного возраста
    List<Student> findByAgeLessThan(Integer age);

    // Кастомный запрос для поиска по нескольким критериям
    @Query("SELECT s FROM Student s WHERE s.name LIKE %:name% AND s.age BETWEEN :minAge AND :maxAge")
    List<Student> findByNameContainingAndAgeBetween(@Param("name") String name,
                                                    @Param("minAge") Integer minAge,
                                                    @Param("maxAge") Integer maxAge);

    // Получение количества студентов по возрасту
    Long countByAge(Integer age);

    // Получение среднего возраста студентов
    @Query("SELECT AVG(s.age) FROM Student s")
    Double findAverageAge();
}