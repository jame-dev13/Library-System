package jame.dev.service;

import jame.dev.models.entitys.UserEntity;
import jame.dev.repositorys.CRUDRepo;

import java.util.List;
import java.util.Optional;

public class UserService implements CRUDRepo<UserEntity> {
    @Override
    public List<UserEntity> getAll() {
        return List.of();
    }

    @Override
    public void save(UserEntity user) {

    }

    @Override
    public Optional<UserEntity> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public void updateById(UserEntity t) {

    }

    @Override
    public void deleteById(Integer id) {

    }
}
