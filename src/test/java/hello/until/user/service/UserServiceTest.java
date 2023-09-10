package hello.until.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import hello.until.jwt.JwtService;
import hello.until.user.constant.Role;
import hello.until.user.entity.User;
import hello.until.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	private UserService userService;
	
	private PasswordEncoder passwordEncoder;
	
	private JwtService jwtService;
	
    private AuthenticationManager authenticationManager;
	
	private User testUser;
	
	private Page<User> testUsers;
	
	@BeforeEach
	void beforeEach() {
		
	    passwordEncoder = new BCryptPasswordEncoder();
		userService = new UserService(userRepository, passwordEncoder, jwtService, authenticationManager );
		
	    testUser = new User(); 
	    testUser.setId(1L);
	    testUser.setEmail("test@test.com");
	    testUser.setPassword("12341234");
	    testUser.setRole(Role.BUYER);
	}

	@Test
	@DisplayName("회원가입 테스트")
	void createUser() {

		String email = "test@test.com";
		String password = "12341234";

	    testUser = new User(); 
	    testUser.setEmail(email);
	    testUser.setPassword(password);
		
	    //given
		Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);

		//when
		userService.createUser(email, password);

		//then
		var userCaptor = ArgumentCaptor.forClass(User.class);
	    Mockito.verify(this.userRepository, times(1)).save(userCaptor.capture());
	    var passedItem = userCaptor.getValue();
		
	    assertThat(passedItem.getEmail()).isEqualTo(email);
	    assertThat(passwordEncoder.matches(password, passedItem.getPassword())).isTrue();
	    
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

	
	@Test
	@DisplayName("다중회원조회 테스트")
	void getUsers() {
		Pageable pageable  = PageRequest.of(0, 5);
		
	    List<User> userList = new ArrayList<>();
	    for (int i = 1; i <= 5; i++) {
	        User user = new User();
	        user.setId((long) i);
	        user.setEmail("test" + i + "@test.com");
	        user.setPassword("password" + i);
	        userList.add(user);
	    }
		
	    
	    //given
	    Mockito.when(this.userRepository.findAllByOrderByIdDesc(pageable))
	           .thenReturn(new PageImpl<>(userList, pageable, userList.size()));
	    
	    //when
	    Page<User> users = userService.getUsers(pageable);
	    
	    //then
	    var userCaptor = ArgumentCaptor.forClass(Pageable.class);
	    Mockito.verify(this.userRepository, times(1)).findAllByOrderByIdDesc(userCaptor.capture());
	    var passedItem = userCaptor.getValue();
	    
	    assertThat(passedItem.getPageNumber()).isEqualTo(0);
	    assertThat(passedItem.getPageSize()).isEqualTo(5);
	    
	    
	    assertThat(users).isNotNull();
	    assertThat(users.getContent().size()).isEqualTo(5);
		
		
		
	}

}
