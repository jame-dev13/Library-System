package jame.dev.repositorys;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CRUDRepo<T> {
    List<T> getAll();
    void save(T t);
    Optional<T> findByUuid(UUID uuid);
    void update(T t);
    void deleteByUuid(UUID uuid);
}
