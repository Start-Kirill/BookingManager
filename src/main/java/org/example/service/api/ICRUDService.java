package org.example.service.api;

import java.util.List;

public interface ICRUDService<T, C> {
    List<T> get();

    T save(C c);


}
