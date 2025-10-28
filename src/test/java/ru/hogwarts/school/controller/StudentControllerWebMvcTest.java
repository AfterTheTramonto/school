package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    void shouldCreateStudent() throws Exception {
        Student student = new Student("Гарри Поттер", 17, "harry@hogwarts.com");
        Student savedStudent = new Student(1L, "Гарри Поттер", 17, "harry@hogwarts.com");

        when(studentService.createStudent(any(Student.class))).thenReturn(savedStudent);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Гарри Поттер"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void shouldGetStudentById() throws Exception {
        Student student = new Student(1L, "Гермиона Грейнджер", 17, "hermione@hogwarts.com");
        when(studentService.getStudentById(1L)).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Гермиона Грейнджер"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void shouldReturnNotFoundWhenStudentDoesNotExist() throws Exception {
        when(studentService.getStudentById(999L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateStudent() throws Exception {
        // Given
        Student updatedStudent = new Student(1L, "Рональд Уизли", 18, "ronald@hogwarts.com");
        when(studentService.updateStudent(anyLong(), any(Student.class))).thenReturn(updatedStudent);

        mockMvc.perform(MockMvcRequestBuilders.put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Рональд Уизли"))
                .andExpect(jsonPath("$.age").value(18));
    }

    @Test
    void shouldDeleteStudent() throws Exception {
        Student student = new Student(1L, "Невилл Долгопупс", 17, "neville@hogwarts.com");
        when(studentService.deleteStudent(1L)).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.delete("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Невилл Долгопупс"));
    }

    @Test
    void shouldGetAllStudents() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Гарри Поттер", 17, "harry@hogwarts.com"),
                new Student(2L, "Гермиона Грейнджер", 17, "hermione@hogwarts.com")
        );
        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Гарри Поттер"))
                .andExpect(jsonPath("$[1].name").value("Гермиона Грейнджер"));
    }

    @Test
    void shouldGetStudentsByAge() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Гарри Поттер", 17, "harry@hogwarts.com"),
                new Student(2L, "Гермиона Грейнджер", 17, "hermione@hogwarts.com")
        );
        when(studentService.getStudentsByAge(17)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/age/17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].age").value(17))
                .andExpect(jsonPath("$[1].age").value(17));
    }

    @Test
    void shouldGetStudentsByAgeRange() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Джинни Уизли", 15, "ginny@hogwarts.com"),
                new Student(2L, "Коллин Криви", 16, "colin@hogwarts.com")
        );
        when(studentService.getStudentsByAgeRange(15, 16)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/age-range")
                        .param("minAge", "15")
                        .param("maxAge", "16"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Джинни Уизли"))
                .andExpect(jsonPath("$[1].name").value("Коллин Криви"));
    }
}