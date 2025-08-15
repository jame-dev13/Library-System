package jame.dev.service;

import jame.dev.models.entitys.FineEntity;
import jame.dev.repositorys.CRUDRepo;

import java.util.List;
import java.util.Optional;

public class FineService implements CRUDRepo<FineEntity> {
    @Override
    public List<FineEntity> getAll() {
        return List.of();
    }

    @Override
    public void save(FineEntity fineEntity) {

    }

    @Override
    public Optional<FineEntity> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public void updateById(Integer id) {

    }

    @Override
    public void deleteById(Integer id) {

    }
}
