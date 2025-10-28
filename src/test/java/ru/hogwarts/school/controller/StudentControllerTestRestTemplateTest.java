package ru.hogwarts.school.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Student;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;


    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/student";

        restTemplate.delete(baseUrl);
    }

    @Test
    void shouldCreateStudent() {
        Student student = new Student("Гарри Поттер", 17, "harry@hogwarts.com");

        ResponseEntity<Student> response = restTemplate.postForEntity(baseUrl, student, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Гарри Поттер");
        assertThat(response.getBody().getAge()).isEqualTo(17);
    }

    @Test
    void shouldGetStudentById() {
        Student student = new Student("Гермиона Грейнджер", 17, "hermione@hogwarts.com");
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(baseUrl, student, Student.class);
        Long studentId = createResponse.getBody().getId();

        ResponseEntity<Student> response = restTemplate.getForEntity(baseUrl + "/" + studentId, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(studentId);
        assertThat(response.getBody().getName()).isEqualTo("Гермиона Грейнджер");
    }

    @Test
    void shouldReturnNotFoundWhenStudentDoesNotExist() {
        ResponseEntity<Student> response = restTemplate.getForEntity(baseUrl + "/999", Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateStudent() {
        Student student = new Student("Рон Уизли", 17, "ron@hogwarts.com");
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(baseUrl, student, Student.class);
        Long studentId = createResponse.getBody().getId();

        Student updatedStudent = new Student("Рональд Уизли", 18, "ronald@hogwarts.com");

        ResponseEntity<Student> response = restTemplate.exchange(
                baseUrl + "/" + studentId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedStudent),
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(studentId);
        assertThat(response.getBody().getName()).isEqualTo("Рональд Уизли");
        assertThat(response.getBody().getAge()).isEqualTo(18);
    }

    @Test
    void shouldDeleteStudent() {
        Student student = new Student("Невилл Долгопупс", 17, "neville@hogwarts.com");
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(baseUrl, student, Student.class);
        Long studentId = createResponse.getBody().getId();

        ResponseEntity<Student> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + studentId,
                HttpMethod.DELETE,
                null,
                Student.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getBody().getName()).isEqualTo("Невилл Долгопупс");

        ResponseEntity<Student> getResponse = restTemplate.getForEntity(baseUrl + "/" + studentId, Student.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldGetAllStudents() {
        Student student1 = new Student("Драко Малфой", 17, "draco@hogwarts.com");
        Student student2 = new Student("Полумна Лавгуд", 16, "luna@hogwarts.com");

        restTemplate.postForEntity(baseUrl, student1, Student.class);
        restTemplate.postForEntity(baseUrl, student2, Student.class);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldGetStudentsByAge() {
        Student student1 = new Student("Седрик Диггори", 17, "cedric@hogwarts.com");
        Student student2 = new Student("Фред Уизли", 17, "fred@hogwarts.com");

        restTemplate.postForEntity(baseUrl, student1, Student.class);
        restTemplate.postForEntity(baseUrl, student2, Student.class);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                baseUrl + "/age/17",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(response.getBody()).allMatch(student -> student.getAge() == 17);
    }

    @Test
    void shouldGetStudentsByAgeRange() {
        Student student1 = new Student("Джинни Уизли", 15, "ginny@hogwarts.com");
        Student student2 = new Student("Коллин Криви", 16, "colin@hogwarts.com");

        restTemplate.postForEntity(baseUrl, student1, Student.class);
        restTemplate.postForEntity(baseUrl, student2, Student.class);

        ResponseEntity<String> stringResponse = restTemplate.exchange(
                baseUrl + "/age-range?minAge=15&maxAge=16",
                HttpMethod.GET,
                null,
                String.class
        );

        System.out.println("Response body: " + stringResponse.getBody());
        System.out.println("Response status: " + stringResponse.getStatusCode());

        assertThat(stringResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Student> students = mapper.readValue(stringResponse.getBody(),
                    new TypeReference<List<Student>>() {
                    });

            assertThat(students).isNotNull();
            assertThat(students).hasSizeGreaterThanOrEqualTo(2);
            assertThat(students).allMatch(student ->
                    student.getAge() >= 15 && student.getAge() <= 16);
        } catch (Exception e) {
            fail("Failed to deserialize response body: " + stringResponse.getBody(), e);
        }
    }
}