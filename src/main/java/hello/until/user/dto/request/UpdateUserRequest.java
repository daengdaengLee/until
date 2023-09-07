package hello.until.user.dto.request;

import hello.until.user.constant.Role;

public record UpdateUserRequest(
        String email,
        String password,
        Role role){

}
