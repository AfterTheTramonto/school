
SELECT * FROM students WHERE age BETWEEN 10 AND 20;


SELECT name FROM students;


SELECT * FROM students WHERE name ILIKE '%о%';


SELECT * FROM students WHERE age < id;


SELECT * FROM students ORDER BY age ASC;



SELECT f.name AS faculty_name, COUNT(s.id) AS student_count
FROM faculties f
LEFT JOIN students s ON f.id = s.faculty_id
GROUP BY f.id, f.name
ORDER BY student_count DESC;


SELECT f.name AS faculty_name, AVG(s.age) AS average_age
FROM faculties f
LEFT JOIN students s ON f.id = s.faculty_id
GROUP BY f.id, f.name
ORDER BY average_age DESC;