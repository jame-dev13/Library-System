package jame.dev.repositorys;

import jame.dev.dtos.UserDto;
import jame.dev.models.enums.ERole;

public interface IAuthRepo {
    ERole signIn(UserDto user);
}
