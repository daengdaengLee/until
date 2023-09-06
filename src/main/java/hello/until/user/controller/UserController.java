package hello.until.user.controller;

import hello.until.user.dto.request.UpdateUserRequest;
import hello.until.user.dto.response.UserResponse;
import hello.until.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import hello.until.exception.CustomException;
import hello.until.exception.ExceptionCode;
import hello.until.user.dto.request.CreateUserRequest;
import hello.until.user.dto.response.GetUserResponse;
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
				updateUserRequest.password());
		return new UserResponse(user);
	}

	@GetMapping("/{id}")
	public GetUserResponse getUserById(@PathVariable long id) {

		return userService.getUserById(id)
				.map(GetUserResponse::new)
				.orElseThrow(() -> new CustomException(ExceptionCode.NO_USER_TO_GET));

	}

}
