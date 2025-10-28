package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.exception.BadRequestException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        // Валидация входных данных
        if (faculty.getName() == null || faculty.getName().isBlank()) {
            throw new BadRequestException("Faculty name cannot be null or empty");
        }
        if (faculty.getColor() == null || faculty.getColor().isBlank()) {
            throw new BadRequestException("Faculty color cannot be null or empty");
        }

        // Проверка на уникальность имени
        Optional<Faculty> existingFaculty = facultyRepository.findByNameIgnoreCase(faculty.getName());
        if (existingFaculty.isPresent()) {
            throw new BadRequestException("Faculty with name '" + faculty.getName() + "' already exists");
        }

        return facultyRepository.save(faculty);
    }

    public Faculty getFacultyById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Faculty not found with id: " + id));
    }

    public Faculty updateFaculty(Long id, Faculty faculty) {
        // Получаем существующий факультет (бросит исключение если не найден)
        Faculty existingFaculty = getFacultyById(id);

        // Обновляем только переданные поля
        if (faculty.getName() != null && !faculty.getName().isBlank()) {
            // Проверяем, что новое имя не конфликтует с другими факультетами
            Optional<Faculty> facultyWithSameName = facultyRepository.findByNameIgnoreCase(faculty.getName());
            if (facultyWithSameName.isPresent() && !facultyWithSameName.get().getId().equals(id)) {
                throw new BadRequestException("Faculty with name '" + faculty.getName() + "' already exists");
            }
            existingFaculty.setName(faculty.getName());
        }

        if (faculty.getColor() != null && !faculty.getColor().isBlank()) {
            existingFaculty.setColor(faculty.getColor());
        }

        return facultyRepository.save(existingFaculty);
    }

    public Faculty deleteFaculty(Long id) {
        // Получаем факультет (бросит исключение если не найден)
        Faculty faculty = getFacultyById(id);
        facultyRepository.deleteById(id);
        return faculty;
    }

    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public List<Faculty> getFacultiesByColor(String color) {
        if (color == null || color.isBlank()) {
            throw new BadRequestException("Color cannot be null or empty");
        }
        return facultyRepository.findByColor(color);
    }

    // Дополнительные методы для расширенного поиска
    public List<Faculty> findFacultiesByNameOrColor(String name, String color) {
        if ((name == null || name.isBlank()) && (color == null || color.isBlank())) {
            throw new BadRequestException("At least one search parameter (name or color) must be provided");
        }
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }

    public Optional<Faculty> findFacultyByName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Faculty name cannot be null or empty");
        }
        return facultyRepository.findByNameIgnoreCase(name);
    }
}