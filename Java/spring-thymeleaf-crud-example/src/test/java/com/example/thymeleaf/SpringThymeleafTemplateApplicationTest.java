package com.example.thymeleaf;

import com.example.thymeleaf.entity.Address;
import com.example.thymeleaf.entity.Student;
import com.example.thymeleaf.service.StudentService;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class SpringThymeleafTemplateApplicationTest {

	@Test
	void contextLoads() {
	}

	@Autowired
	private WebApplicationContext webApplicationContext;

	private Student student;
	private StudentService studentService;

	@Getter
	private String value;

	@BeforeEach
	public void setUp() {
		student = new Student();
	}

	// Testy poprawnych danych

	@Test
	public void testCreatingNewStudent() {
		assertNotNull(student);
		assertNotNull(student.getId());
		assertNull(student.getName());
		assertNull(student.getEmail());
		assertNull(student.getBirthday());
		assertNotNull(student.getCreatedAt());
		assertNull(student.getUpdatedAt());
		assertNull(student.getAddress());
	}

	@Test
	public void testUpdatingStudent() {
		student.setName("John Doe");
		student.setEmail("john@example.com");
		student.setBirthday(LocalDate.of(1995, 5, 15));
		LocalDateTime originalUpdatedAt = student.getUpdatedAt();

		// Symulacja opóźnienia przed aktualizacją
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		student.setName("Updated Name");
		student.setEmail("updated@example.com");
		student.setBirthday(LocalDate.of(1990, 10, 20));

		assertNotNull(student.getUpdatedAt());
		assertNotEquals(originalUpdatedAt, student.getUpdatedAt());
	}

	@Test
	public void testRemovingStudentAddress() {
		Address address = new Address();
		student.setAddress(address);

		assertNotNull(student.getAddress());
		student.setAddress(null);
		assertNull(student.getAddress());
	}

	// Testy niepoprawnych danych

	//    @Test(expected = NullPointerException.class)
	@Test
	public void testCreatingStudentWithMissingData() {
		// Oczekuje wyjątku, gdy tworzony jest student bez imienia
		student.setName(null);
		student.setEmail("test@example.com");
		student.setBirthday(LocalDate.now());

		studentService.save(student);
	}

	// Test wstrzykiwania SQL

	@Test
	public void testSQLInjection() {
		// Symulacja wstrzyknięcia SQL
		String maliciousInput = "'; DROP TABLE student; --";
		student.setName(maliciousInput);
		assertEquals("'; DROP TABLE student; --", student.getName());
	}

	// Test wstrzykiwania kodu JavaScript

	@Test
	public void testJavaScriptInjection() {
		// Symulacja wstrzyknięcia kodu JavaScript
		String maliciousScript = "<script>alert('Hello');</script>";
		student.setName(maliciousScript);
		assertEquals("<script>alert('Hello');</script>", student.getName());
	}

	// Testy ekstremalne

	@Test
	@Timeout(1)
	public void testPerformanceCreatingManyStudents() {
		// Test wydajności - tworzenie wielu studentów
		for (int i = 0; i < 1000; i++) {
			Student newStudent = new Student();
		}
	}

	@Test
	public void testYoungAndOldStudentsBirthdate() {
		// Testy dla bardzo młodych (< 15 lat) i bardzo starych (> 100 lat) studentów
		LocalDate youngDate = LocalDate.now().minusYears(16);
		LocalDate oldDate = LocalDate.now().minusYears(101);

		student.setBirthday(youngDate);
		assertTrue(student.getBirthday().isAfter(LocalDate.now().minusYears(5)));

		student.setBirthday(oldDate);
		assertTrue(student.getBirthday().isBefore(LocalDate.now().minusYears(100)));
	}

	@Test
	public void testLongAndSpecialCharacterNames() {
		// Testy dla długich nazw
		String longName = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
		student.setName(longName);
		assertEquals(longName, student.getName());
	}

}
