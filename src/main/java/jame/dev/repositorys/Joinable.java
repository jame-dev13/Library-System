package jame.dev.repositorys;

import java.util.List;

/**
 * Defines the contract to retrieve data from the DB using joins in the queries.
 * @param <T> the return type of object implemented.
 */
@FunctionalInterface
public interface Joinable<T> {
    List<T> getJoins();
}
