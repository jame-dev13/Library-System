package jame.dev.repositorys;

import jame.dev.dtos.users.SessionDto;
import jame.dev.dtos.users.UserDto;

/**
 * Defines the contract to sign in a user in the application.
 */
public interface IAuthRepo {
    SessionDto signIn(UserDto user);
}
