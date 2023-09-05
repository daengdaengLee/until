package hello.until.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
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

import hello.until.user.entity.User;
import hello.until.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	private UserService userService;

	private User testUser;
	
	private Page<User> testUsers;
	
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
