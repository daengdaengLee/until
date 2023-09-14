package hello.until.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import hello.until.jwt.JwtService;
import hello.until.user.constant.Role;
import hello.until.user.entity.User;
import hello.until.user.service.UserService;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UserService userService;
	private User testUser;
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	@MockBean
	private JwtService jwtService;

	@BeforeEach
	void beforeEach() {
		testUser = User.builder().id(1L).email("test@test.com").password("12345678").role(Role.BUYER)
				.createdAt(LocalDateTime.now().minusDays(1L)).updatedAt(LocalDateTime.now()).build();
	}

	@Test
	@DisplayName("단건 고객 조회 - 존재")
	void getExistUser() throws Exception {
		//given
		when(this.userService.getUserById(anyLong()))
		.thenReturn(Optional.of(this.testUser));
		
		//when & then
		this.mockMvc
			.perform(get("/users/" + this.testUser.getId()))
			.andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(this.testUser.getId().toString()))
            .andExpect(jsonPath("$.email").value(this.testUser.getEmail()))
            .andExpect(jsonPath("$.role").value(this.testUser.getRole().toString()))
            .andExpect(jsonPath("$.createdAt").value(this.testUser.getCreatedAt().format(dateTimeFormatter)))
            .andExpect(jsonPath("$.updatedAt").value(this.testUser.getUpdatedAt().format(dateTimeFormatter)));
		verifyUserSerivceGetUser();
 
	}

	@Test
	@DisplayName("단건 고객 조회 - 미존재")
    void readNotExistingItem() throws Exception {
		when(this.userService.getUserById(anyLong()))
		.thenReturn(Optional.empty());
		
		//when & then
		this.mockMvc
			.perform(get("/users/" + this.testUser.getId()))
			.andExpect(status().isBadRequest());
			verifyUserSerivceGetUser();
    }

	private void verifyUserSerivceGetUser() {
		var idCaptor = ArgumentCaptor.forClass(Long.class);
		verify(this.userService, times(1)).getUserById(idCaptor.capture());
		var capturedId = idCaptor.getValue();
		assertThat(capturedId).isEqualTo(this.testUser.getId());
	}

	@Nested
	class GetUserAll {
		private List<User> testUsers;

		@BeforeEach
		void beforeEach() {
			
            this.testUsers = new ArrayList<>();
			for (long id = 100; id > 0; id--) {
				this.testUsers.add(
						User.builder().id(id).email("test" + id + "@test.com").password("12345678").role(Role.BUYER)
								.createdAt(LocalDateTime.now().minusDays(1L)).updatedAt(LocalDateTime.now()).build()
								);
			}
		}

		@Test
		@DisplayName("페이징 조회")
		void getUsers() throws Exception {
			int page = 1;
			int size = 5;

	        Pageable pageable  = (Pageable) PageRequest.of(page, size);
            int startIdx = (int) pageable.getOffset();	
            int endIdx = Math.min(startIdx + pageable.getPageSize(), testUsers.size());
            
			Page<User> pageRes = new PageImpl<>(this.testUsers.subList(startIdx, endIdx),pageable,endIdx);
            
			//given
			when(userService.getUsers(pageable))
			.thenReturn(pageRes);
			

			// when
			ResultActions resultActions = mockMvc
					.perform(get("/users/getUsers?page=" + page));

			// then
			resultActions.andExpect(status().isOk());
			int resultIdx = 0;
			int userIdx = startIdx;

			for (; resultIdx < size; resultIdx++, userIdx++) {
				String base = String.format("$.content[%d]", resultIdx);
				resultActions.andExpect(jsonPath(base + ".id").value(this.testUsers.get(userIdx).getId().toString()))
						.andExpect(jsonPath(base + ".email").value(this.testUsers.get(userIdx).getEmail()))
						.andExpect(jsonPath(base + ".role").value(this.testUsers.get(userIdx).getRole().name()))
						.andExpect(jsonPath(base + ".createdAt")
								.value(this.testUsers.get(userIdx).getCreatedAt().format(dateTimeFormatter)))
						.andExpect(jsonPath(base + ".updatedAt")
								.value(this.testUsers.get(userIdx).getUpdatedAt().format(dateTimeFormatter)));
			}

			var pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
			verify(userService, times(1)).getUsers(pageableCaptor.capture());
			var passedPageable = pageableCaptor.getValue();
            assertThat(passedPageable.equals(pageable)).isTrue();
		}
	}

}
