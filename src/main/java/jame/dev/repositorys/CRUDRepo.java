package jame.dev.repositorys;

import java.util.List;
import java.util.Optional;

public interface CRUDRepo<T> {
    List<T> getAll();
    void save(T t);
    Optional<T> findById(Integer id);
    void updateById(Integer id);
    void deleteById(Integer id);
}
