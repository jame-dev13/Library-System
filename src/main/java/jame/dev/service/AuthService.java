package jame.dev.service;

import jame.dev.dtos.UserDto;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.IAuthRepo;
import jame.dev.utils.DQLActions;

import java.util.NoSuchElementException;

public class AuthService implements IAuthRepo  {
    @Override
    public ERole signIn(UserDto user) {
        String sql = "SELECT email, password, role FROM users WHERE email = ? AND verified = 1";
        try{
            UserDto dto = DQLActions.selectWhere(sql, rs ->
                            UserDto.builder()
                                    .username(rs.getString(1))
                                    .password(rs.getString(2))
                                    .role(ERole.valueOf(rs.getString(3)))
                                    .build(), user.username())
                    .getFirst();
            return dto.role();
        } catch (NoSuchElementException e){
            return ERole.UNDEFINED;
        }
    }
}
