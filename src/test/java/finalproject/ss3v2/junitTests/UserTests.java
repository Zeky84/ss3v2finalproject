package finalproject.ss3v2.junitTests;


import finalproject.ss3v2.domain.Authority;
import finalproject.ss3v2.domain.Profile;
import finalproject.ss3v2.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTests {
	//Test Driven Development: Write the test first, then write the code to make the test pass

	@Test
	public void createUser() {
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("j@gmail.com");
		user.setPassword("123");

		assertEquals("John", user.getFirstName());
	}

	@Test
	public void testUserRoleAssignment() {
		User user = new User();
		user.setAdmin(true);
		user.setSuperUser(true);
		user.getAuthorities().add(new Authority("ROLE_ADMIN"));

		Assertions.assertTrue(user.isAdmin());
		Assertions.assertTrue(user.isSuperUser());
		Assertions.assertFalse(user.getAuthorities().contains("ROLE_ADMIN"));
		Assertions.assertFalse(user.getAuthorities().contains("ROLE_SUPERUSER"));

	}

	@Test
	public void testUserProfileAssignment() {
		User user = new User();
		Profile profile = new Profile();
		Profile profile1 = new Profile();
		profile.setProfileName("Test Profile");
		profile1.setProfileName("Test Profile 1");
		user.getProfiles().add(profile);
		user.getProfiles().add(profile1);

		Assertions.assertTrue(user.getProfiles().contains(profile));
		assertEquals(user.getProfiles().get(1).getProfileName(), "Test Profile 1");
	}

	@Test
	public void testUserEditInformation() {
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("newemail@example.com");
		user.setEmail("blingbling");

		assertEquals("blingbling", user.getEmail());
	}

}