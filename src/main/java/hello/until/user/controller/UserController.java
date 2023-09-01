package hello.until.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import hello.until.user.dto.response.getUserResponse;
import hello.until.user.entity.User;
import hello.until.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
    @GetMapping("user/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id){
        User user = userService.getUserById(id);
        return ResponseEntity.ok(new getUserResponse(user));
    }
	
}
