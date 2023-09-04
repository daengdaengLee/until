package hello.until.user.controller;

import hello.until.user.dto.request.UpdateUserRequest;
import hello.until.user.dto.response.UserResponse;
import hello.until.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hello.until.user.dto.request.CreateUserRequest;
import hello.until.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	
	private final UserService userService;

	@PostMapping("/join")
	public ResponseEntity<?> join(@RequestBody @Valid CreateUserRequest createUserRequest){
		userService.createUser(createUserRequest.getEmail(), createUserRequest.getPassword());
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{id}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
										@RequestBody @Valid UpdateUserRequest updateUserRequest){
		User user = userService.updateUser(
				id,
				updateUserRequest.getEmail(),
				updateUserRequest.getPassword());
		return ResponseEntity.ok(new UserResponse(user));
	}


}