package jame.dev.service;

import jame.dev.models.entitys.CopyEntity;
import jame.dev.repositorys.CRUDRepo;

import java.util.List;
import java.util.Optional;

public class CopyService implements CRUDRepo<CopyEntity> {
    @Override
    public List<CopyEntity> getAll() {
        return List.of();
    }

    @Override
    public void save(CopyEntity copyEntity) {

    }

    @Override
    public Optional<CopyEntity> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public void updateById(CopyEntity t) {

    }

    @Override
    public void deleteById(Integer id) {

    }
}
