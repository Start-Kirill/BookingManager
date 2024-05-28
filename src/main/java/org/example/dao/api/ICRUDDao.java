package org.example.dao.api;

import java.util.List;

public interface ICRUDDao<T> {
    List<T> get();

    T save(T t);
}
