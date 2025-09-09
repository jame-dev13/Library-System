package jame.dev.repositorys;

import jame.dev.dtos.SessionDto;
import jame.dev.dtos.UserDto;

public interface IAuthRepo {
    SessionDto signIn(UserDto user);
}
