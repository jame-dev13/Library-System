package jame.dev.service;

import jame.dev.dtos.UserDto;
import jame.dev.repositorys.IAuthRepo;
import jame.dev.utils.DQLActions;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService implements IAuthRepo  {
    @Override
    public boolean signIn(UserDto user) {
        String sql = "SELECT email, password FROM users WHERE email = ? AND verified = 1";
        UserDto dto = DQLActions.selectWhere(sql, rs -> UserDto.builder()
                .username(rs.getString(1))
                .password(rs.getString(2))
                .build(), user.username())
                .getFirst();
        return user.username().equals(dto.username()) && BCrypt.checkpw(user.password(), dto.password());
    }
}
