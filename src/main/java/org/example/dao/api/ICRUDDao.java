package org.example.dao.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICRUDDao<T> {

    Optional<T> get(UUID uuid);

    List<T> get();

    T save(T t);

    T update(T t);

    void delete(T t);

}
