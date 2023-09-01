package hello.until.user.service;

import static org.assertj.core.api.Assertions.assertThat;

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
	@DisplayName("회원조회 테스트")
	void getUser() {

        // given
        Long testUserId = this.testUser.getId();
        Mockito.when(this.userRepository.findById(testUserId))
                .thenReturn(Optional.of(this.testUser));

        // when
        User result = this.userService.getUserById(testUserId);

        // then
        assertThat(result.getId()).isEqualTo(this.testUser.getId());
        assertThat(result.getEmail()).isEqualTo(this.testUser.getEmail());

	}

}