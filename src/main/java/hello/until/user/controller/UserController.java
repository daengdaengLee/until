package hello.until.user.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.until.exception.CustomException;
import hello.until.exception.ExceptionCode;
import hello.until.user.dto.request.CreateUserRequest;
import hello.until.user.dto.request.UpdateUserRequest;
import hello.until.user.dto.response.GetUserResponse;
import hello.until.user.dto.response.UserResponse;
import hello.until.user.entity.User;
import hello.until.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	@PostMapping("/join")
	public void join(@RequestBody @Valid CreateUserRequest createUserRequest) {
		userService.createUser(createUserRequest.getEmail(), createUserRequest.getPassword());
	}

	@PatchMapping("/{id}")
	public UserResponse updateUser(@PathVariable Long id,
												   @RequestBody @Valid UpdateUserRequest updateUserRequest){
		User user = userService.updateUser(
				id,
				updateUserRequest.email(),
				updateUserRequest.password(),
				updateUserRequest.role());
		return new UserResponse(user);
	}

	@GetMapping("/{id}")
	public GetUserResponse getUserById(@PathVariable long id) {

		return userService.getUserById(id)
				.map(GetUserResponse::new)
				.orElseThrow(() -> new CustomException(ExceptionCode.NO_USER_TO_GET));

	}
	
	@GetMapping("/getUsers")
	public  Page<GetUserResponse> getUsers(Optional<Integer> page) {
		Pageable pageable  = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        Page<User> userPage = userService.getUsers(pageable);
        return userPage.map(user -> new GetUserResponse(user));

	}

}
