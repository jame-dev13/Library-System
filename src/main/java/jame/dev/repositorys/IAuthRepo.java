package jame.dev.repositorys;

import jame.dev.dtos.UserDto;

public interface IAuthRepo {
    boolean signIn(UserDto user);
}
