package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.exception.BadRequestException;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FacultyService facultyService;

    @Test
    void shouldCreateFaculty() throws Exception {
        Faculty faculty = new Faculty("Гриффиндор", "красный");
        Faculty savedFaculty = new Faculty(1L, "Гриффиндор", "красный");

        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(savedFaculty);

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("красный"));
    }

    @Test
    void shouldGetFacultyById() throws Exception {
        Faculty faculty = new Faculty(1L, "Слизерин", "зеленый");
        when(facultyService.getFacultyById(1L)).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Слизерин"))
                .andExpect(jsonPath("$.color").value("зеленый"));
    }

    @Test
    void shouldReturnNotFoundWhenFacultyDoesNotExist() throws Exception {
        when(facultyService.getFacultyById(999L))
                .thenThrow(new NotFoundException("Faculty not found with id: 999"));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Faculty not found with id: 999"));
    }

    @Test
    void shouldUpdateFaculty() throws Exception {
        Faculty updatedFaculty = new Faculty(1L, "Когтевран", "голубой");
        when(facultyService.updateFaculty(anyLong(), any(Faculty.class))).thenReturn(updatedFaculty);

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Когтевран"))
                .andExpect(jsonPath("$.color").value("голубой"));
    }

    @Test
    void shouldDeleteFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Пуффендуй", "желтый");
        when(facultyService.deleteFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Пуффендуй"));
    }

    @Test
    void shouldGetAllFaculties() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "Гриффиндор", "красный"),
                new Faculty(2L, "Слизерин", "зеленый")
        );
        when(facultyService.getAllFaculties()).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"))
                .andExpect(jsonPath("$[1].name").value("Слизерин"));
    }

    @Test
    void shouldGetFacultiesByColor() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "Гриффиндор", "красный"),
                new Faculty(2L, "Дурмстранг", "красный")
        );
        when(facultyService.getFacultiesByColor("красный")).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/color/красный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].color").value("красный"))
                .andExpect(jsonPath("$[1].color").value("красный"));
    }

    @Test
    void shouldReturnBadRequestForInvalidFaculty() throws Exception {
        Faculty invalidFaculty = new Faculty("", ""); // Пустые name и color

        when(facultyService.createFaculty(any(Faculty.class)))
                .thenThrow(new BadRequestException("Faculty name cannot be null or empty"));

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFaculty)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Faculty name cannot be null or empty"));
    }
}