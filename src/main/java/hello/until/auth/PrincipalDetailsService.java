package hello.until.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import hello.until.exception.CustomException;
import hello.until.exception.ExceptionCode;
import hello.until.user.entity.User;
import hello.until.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userEntity = userRepository.findByEmail(username)
				.orElseThrow(() -> new CustomException(ExceptionCode.NO_USER_TO_GET));
		return new PrincipalDetails(userEntity);
	}

}
