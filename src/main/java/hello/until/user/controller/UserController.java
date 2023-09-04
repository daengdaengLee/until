package hello.until.user.controller;

import hello.until.user.dto.request.UpdateUserRequest;
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
	public ResponseEntity<?> join(@RequestBody @Valid CreateUserRequest createUserRequest) {
		userService.createUser(createUserRequest.getEmail(), createUserRequest.getPassword());
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id,
												   @RequestBody @Valid UpdateUserRequest updateUserRequest){
		Optional<User> user = userService.updateUser(
				id,
				updateUserRequest.getEmail(),
				updateUserRequest.getPassword());
		if (user.isPresent())
			return ResponseEntity.ok(new GetUserResponse(user.get()));
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저를 찾을 수 없습니다.");
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable long id) {

		Optional<User> user = userService.getUserById(id);

		if (user.isPresent())
			return ResponseEntity.ok(new GetUserResponse(user.get()));
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저를 찾을 수 없습니다.");
	}

}
