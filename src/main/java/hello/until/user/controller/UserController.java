package hello.until.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	
	
}