package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.BadRequestException;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        if (faculty.getName() == null || faculty.getName().isBlank()) {
            throw new BadRequestException("Faculty name cannot be null or empty");
        }
        if (faculty.getColor() == null || faculty.getColor().isBlank()) {
            throw new BadRequestException("Faculty color cannot be null or empty");
        }

        // Проверка на уникальность имени
        if (facultyRepository.findByNameIgnoreCase(faculty.getName()).isPresent()) {
            throw new BadRequestException("Faculty with name '" + faculty.getName() + "' already exists");
        }

        return facultyRepository.save(faculty);
    }

    public Faculty getFacultyById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Faculty not found with id: " + id));
    }

    public Faculty updateFaculty(Long id, Faculty faculty) {
        Faculty existingFaculty = getFacultyById(id); // Будет брошено исключение если не найден

        if (faculty.getName() != null && !faculty.getName().isBlank()) {
            // Проверяем, что новое имя не конфликтует с другими факультетами
            facultyRepository.findByNameIgnoreCase(faculty.getName())
                    .ifPresent(f -> {
                        if (!f.getId().equals(id)) {
                            throw new BadRequestException("Faculty with name '" + faculty.getName() + "' already exists");
                        }
                    });
            existingFaculty.setName(faculty.getName());
        }
        if (faculty.getColor() != null && !faculty.getColor().isBlank()) {
            existingFaculty.setColor(faculty.getColor());
        }

        return facultyRepository.save(existingFaculty);
    }

    public Faculty deleteFaculty(Long id) {
        Faculty faculty = getFacultyById(id); // Будет брошено исключение если не найден
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
}