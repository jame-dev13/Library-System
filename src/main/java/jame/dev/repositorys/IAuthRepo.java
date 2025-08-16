package jame.dev.repositorys;

import jame.dev.dtos.UserDto;

public interface IAuthRepo {
    void signIn(UserDto user);
    void verify(Integer id, String token);
}
