package jame.dev.repositorys;

import java.util.List;

public interface IMultiQuery<T> {
    List<T> getAllWithInfo();
}
