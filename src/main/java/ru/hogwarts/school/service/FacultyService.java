package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
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
        return facultyRepository.save(faculty);
    }

    public Faculty getFacultyById(Long id) {
        Optional<Faculty> faculty = facultyRepository.findById(id);
        return faculty.orElse(null);
    }

    public Faculty updateFaculty(Long id, Faculty faculty) {
        Optional<Faculty> existingFaculty = facultyRepository.findById(id);
        if (existingFaculty.isPresent()) {
            faculty.setId(id);
            return facultyRepository.save(faculty);
        }
        return null;
    }

    public Faculty deleteFaculty(Long id) {
        Optional<Faculty> faculty = facultyRepository.findById(id);
        if (faculty.isPresent()) {
            facultyRepository.deleteById(id);
            return faculty.get();
        }
        return null;
    }

    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public List<Faculty> getFacultiesByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public List<Faculty> getFacultiesByNameOrColor(String searchString) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(searchString, searchString);
    }
    public List<Student> getFacultyStudents(Long facultyId) {
        Faculty faculty = getFacultyById(facultyId);
        return faculty != null ? faculty.getStudents() : null;
    }
}