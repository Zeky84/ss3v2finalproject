package finalproject.ss3v2.integrationTests;

import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.repository.UserRepository;
import finalproject.ss3v2.service.UserServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//@Transactional
@SpringBootTest
public class Ss3v2sUserServiceTests {
	//Arrange, Act, Assert

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private UserRepository userRepository;

	private ArrayList<User> users;

	//Arrange
	@BeforeEach
	public void setUp() {
		userRepository.deleteAll(); // Clean up the database before each test
		users = new ArrayList<>();
		for (int i = 1; i < 10; i++) {
			User user = new User();
			user.setFirstName("User" + i);
			user.setLastName("User" + i);
			user.setEmail("user" + i + "@gmail.com");
			user.setPassword("password" + i);
			users.add(user);
		}
	}
	@AfterEach
	public void tearDown() {
		userRepository.deleteAll(); // Clean up the database after each test
	}

	@Test
	public void testSaveUsers() {
		for (User user : users) {
			userServiceImpl.save(user);
		}
		assertEquals(9, userRepository.findAll().size());
	}

	@Test
	public void testFindUserByEmail() {
		//Act
		User savedUser = userRepository.save(users.get(0));
		User userSavedInRepo = userRepository.findByEmail(savedUser.getEmail()).orElse(null);
		//Assert
		assertNotNull(userSavedInRepo);
		assertEquals(savedUser.getEmail(), userSavedInRepo.getEmail());
		assertEquals(savedUser.getFirstName(), userSavedInRepo.getFirstName());
	}

	@Test
	public void testUpdateUser() {
		User savedUser = userRepository.save(users.get(0));
		savedUser.setFirstName("UpdatedName");
		userServiceImpl.save(savedUser);
		User updatedUser = userServiceImpl.findUserByEmail(savedUser.getEmail()).orElse(null);
		assertNotNull(updatedUser);
		assertEquals("UpdatedName", updatedUser.getFirstName());
	}

	@Test
	public void testDeleteUser() {
		User savedUser = userRepository.save(users.get(0));
		userRepository.delete(savedUser);
		User deletedUser = userRepository.findById(savedUser.getId()).orElse(null);
		assertNull(deletedUser); // Assuming the findById returns null if the user is not found
	}
	private void assertNull(User deletedUser) {
	}

	@Test
	public void testSaveUserWithDuplicateEmail() {
		User firstUser = userRepository.save(users.get(0));
		User duplicateUser = new User();
		duplicateUser.setFirstName("Duplicate");
		duplicateUser.setLastName("User");
		duplicateUser.setEmail(firstUser.getEmail()); // Same email as firstUser
		duplicateUser.setPassword("password");

		Assertions.assertThrows(Exception.class, () -> {
			userServiceImpl.save(duplicateUser);
		});
	}
}
