package org.example.service.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ICRUDService<T, C> {

    T get(UUID uuid);

    List<T> get();

    T save(C c);

    T update(C c, UUID uuid, LocalDateTime dtUpdate);

    void delete(UUID uuid, LocalDateTime dtUpdate);

}
