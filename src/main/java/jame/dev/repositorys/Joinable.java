package jame.dev.repositorys;

import java.util.List;
@FunctionalInterface
public interface Joinable<T> {
    List<T> getJoins();
}
