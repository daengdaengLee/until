package hello.until.user.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.until.auth.PrincipalDetails;
import hello.until.exception.CustomException;
import hello.until.exception.ExceptionCode;
import hello.until.jwt.JwtService;
import hello.until.user.constant.Role;
import hello.until.user.dto.response.LoginResponse;
import hello.until.user.entity.User;
import hello.until.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JwtService jwtService;
	
	private final AuthenticationManager authenticationManager;
	
	public LoginResponse login(String email, String password) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		
		User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionCode.NO_USER_TO_GET));
		PrincipalDetails principalDetails = new PrincipalDetails(user);
		String jwtToken = jwtService.generateToken(principalDetails);

		return LoginResponse.builder().accessToken(jwtToken).build();
	}

	
	@Transactional
	public void createUser(String email, String password) {

		validateUser(email);

		User user = new User();
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		user.setRole(Role.BUYER);
		userRepository.save(user);

	}

	private void validateUser(String email) {
		Optional<User> user = userRepository.findByEmail(email);

		if (user.isPresent())
			throw new CustomException(ExceptionCode.DUPLICATE_EMAIL_USER_TO_CREATE);
	}

	public User updateUser(long userId, String email, String password, Role role) {
		User user = this.getUserById(userId).orElseThrow(() -> new CustomException(ExceptionCode.NO_USER_TO_UPDATE));

		user.updateEmail(email);
		user.updatePassword(password, passwordEncoder);
		user.updateRole(role);

		return userRepository.save(user);
	}

	@Transactional(readOnly = true)
	public Optional<User> getUserById(long id) {
		return userRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public Page<User> getUsers(Pageable pageable) {
		Page<User> users = userRepository.findAllByOrderByIdDesc(pageable);

		return users;

	}
}
