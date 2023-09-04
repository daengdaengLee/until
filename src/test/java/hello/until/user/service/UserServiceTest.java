package hello.until.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Optional;

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
		
	    testUser = new User(); 
	    testUser.setId(1L);
	    testUser.setEmail("test@test.com");
	    testUser.setPassword("12341234");
	    testUser.setCreatedAt(LocalDateTime.now());
	    testUser.setUpdatedAt(LocalDateTime.now());
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
	
	@Test
	@DisplayName("가입 회원조회 테스트 - Optional 객체 반환")
	void getUser() {

        // given
        Long testUserId = this.testUser.getId();
        Mockito.when(this.userRepository.findById(testUserId))
                .thenReturn(Optional.of(this.testUser));

        // when
        Optional<User> result = this.userService.getUserById(testUserId);

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(this.testUser);
	}
	
	@Test
	@DisplayName("미가입 회원조회 테스트 - Optional 빈 객체 반환")
	void getNoUser() {

        // given
        Long testUserId = this.testUser.getId();
        Mockito.when(this.userRepository.findById(testUserId))
                .thenReturn(Optional.empty());

        // when
        Optional<User> result = this.userService.getUserById(testUserId);

        // then
        assertThat(result.isEmpty()).isTrue();
	}

}
