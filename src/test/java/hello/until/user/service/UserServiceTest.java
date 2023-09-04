package hello.until.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import hello.until.user.entity.User;
import hello.until.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	private UserService userService;

	private User testUser;

	@BeforeEach
	void beforeEach() {
		userService = new UserService(userRepository);

	}

	@Test
	@DisplayName("회원가입 테스트")
	void createUser() {

		String email = "test@test.com";
		String password = "12341234";

	    testUser = new User(); 
	    testUser.setEmail(email);
	    testUser.setPassword(password);
		
		Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> {
	        User savedUser = invocation.getArgument(0);
	        assertEquals(email, savedUser.getEmail()); 
	        assertEquals(password, savedUser.getPassword()); 
	        return testUser; 
	    });
		userService.createUser(email, password);

		Mockito.verify(this.userRepository, Mockito.times(1)).save(Mockito.any(User.class));

	}

}
