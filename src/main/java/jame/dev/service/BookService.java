package jame.dev.service;

import jame.dev.models.entitys.BookEntity;
import jame.dev.repositorys.CRUDRepo;

import java.util.List;
import java.util.Optional;

public class BookService implements CRUDRepo<BookEntity> {
    @Override
    public List<BookEntity> getAll() {
        return List.of();
    }

    @Override
    public void save(BookEntity bookEntity) {

    }

    @Override
    public Optional<BookEntity> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public void updateById(Integer id) {

    }

    @Override
    public void deleteById(Integer id) {

    }
}
