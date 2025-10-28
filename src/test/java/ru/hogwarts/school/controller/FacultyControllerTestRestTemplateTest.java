package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FacultyControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/faculty";
        facultyRepository.deleteAll();
    }

    @Test
    void shouldCreateFaculty() {
        Faculty faculty = new Faculty("Гриффиндор", "красный");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(baseUrl, faculty, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Гриффиндор");
        assertThat(response.getBody().getColor()).isEqualTo("красный");
    }

    @Test
    void shouldGetFacultyById() {
        Faculty faculty = new Faculty("Слизерин", "зеленый");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(baseUrl, faculty, Faculty.class);
        Long facultyId = createResponse.getBody().getId();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(baseUrl + "/" + facultyId, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(facultyId);
        assertThat(response.getBody().getName()).isEqualTo("Слизерин");
        assertThat(response.getBody().getColor()).isEqualTo("зеленый");
    }

    @Test
    void shouldReturnNotFoundWhenFacultyDoesNotExist() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateFaculty() {
        Faculty faculty = new Faculty("Когтевран", "синий");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(baseUrl, faculty, Faculty.class);
        Long facultyId = createResponse.getBody().getId();

        Faculty updatedFaculty = new Faculty("Когтевран", "голубой");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Faculty> requestEntity = new HttpEntity<>(updatedFaculty, headers);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                baseUrl + "/" + facultyId,
                HttpMethod.PUT,
                requestEntity,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(facultyId);
        assertThat(response.getBody().getName()).isEqualTo("Когтевран");
        assertThat(response.getBody().getColor()).isEqualTo("голубой");
    }

    @Test
    void shouldDeleteFaculty() {
        Faculty faculty = new Faculty("Пуффендуй", "желтый");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(baseUrl, faculty, Faculty.class);
        Long facultyId = createResponse.getBody().getId();

        ResponseEntity<Faculty> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + facultyId,
                HttpMethod.DELETE,
                null,
                Faculty.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getBody()).isNotNull();
        assertThat(deleteResponse.getBody().getName()).isEqualTo("Пуффендуй");

        ResponseEntity<String> getResponse = restTemplate.getForEntity(baseUrl + "/" + facultyId, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldGetAllFaculties() {
        Faculty faculty1 = new Faculty("Гриффиндор", "красный");
        Faculty faculty2 = new Faculty("Слизерин", "зеленый");

        restTemplate.postForEntity(baseUrl, faculty1, Faculty.class);
        restTemplate.postForEntity(baseUrl, faculty2, Faculty.class);

        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void shouldGetFacultiesByColor() {
        Faculty faculty1 = new Faculty("Гриффиндор", "красный");
        Faculty faculty2 = new Faculty("Дурмстранг", "красный");

        restTemplate.postForEntity(baseUrl, faculty1, Faculty.class);
        restTemplate.postForEntity(baseUrl, faculty2, Faculty.class);

        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                baseUrl + "/color/красный",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).allMatch(f -> f.getColor().equals("красный"));
    }

    @Test
    void shouldNotCreateFacultyWithDuplicateName() {
        Faculty faculty1 = new Faculty("Гриффиндор", "красный");
        Faculty faculty2 = new Faculty("Гриффиндор", "алый");

        ResponseEntity<Faculty> response1 = restTemplate.postForEntity(baseUrl, faculty1, Faculty.class);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> response2 = restTemplate.postForEntity(baseUrl, faculty2, String.class);

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotUpdateFacultyWithDuplicateName() {
        Faculty faculty1 = new Faculty("Гриффиндор", "красный");
        Faculty faculty2 = new Faculty("Слизерин", "зеленый");

        ResponseEntity<Faculty> response1 = restTemplate.postForEntity(baseUrl, faculty1, Faculty.class);
        ResponseEntity<Faculty> response2 = restTemplate.postForEntity(baseUrl, faculty2, Faculty.class);

        Long faculty2Id = response2.getBody().getId();

        Faculty updatedFaculty = new Faculty("Гриффиндор", "изумрудный");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Faculty> requestEntity = new HttpEntity<>(updatedFaculty, headers);

        ResponseEntity<String> updateResponse = restTemplate.exchange(
                baseUrl + "/" + faculty2Id,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}