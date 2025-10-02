package jame.dev.repositorys;

import jame.dev.dtos.users.SessionDto;
import jame.dev.dtos.users.UserDto;

public interface IAuthRepo {
    SessionDto signIn(UserDto user);
}
