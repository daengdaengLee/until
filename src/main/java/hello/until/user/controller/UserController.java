package hello.until.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.until.user.dto.request.CreateUserRequest;
import hello.until.user.dto.response.GetUserResponse;
import hello.until.user.entity.User;
import hello.until.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
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
	
	
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable long id){
        User user = userService.getUserById(id).orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));
        return ResponseEntity.ok(new GetUserResponse(user));
    }
	
}
