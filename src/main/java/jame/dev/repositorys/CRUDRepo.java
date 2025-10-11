package jame.dev.repositorys;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DAO pattern to perform basic CRUD operation.
 *
 * @param <T> the in/out type object that is going to be implemented.
 */
public interface CRUDRepo<T> {
   /**
    * Retrieves a list of objects of type {@code T}.
    * @return a {@link List} of {@code T} objects.
    */
   List<T> getAll();

   /**
    * Saves an Object of Type {@code T} determined by the class that implements it.
    * @param t The Object generic Type
    */
   void save(T t);

   /**
    * Retrieves a specific object of typee {@code T} determined by the
    * class that implements it.
    * @param uuid the {@link UUID} identifier.
    * @return an {@link Optional} of {@code T}
    */
   Optional<T> findByUuid(UUID uuid);

   /**
    * Updates an object of type {@code T } determined by the implementation class.
    * @param t and Object {@code T}
    */
   void update(T t);

   /**
    * Deletes an object of type {@code T} determined by the implementation class.
    * @param uuid the {@link UUID} identifier.
    */
   void deleteByUuid(UUID uuid);
}
