package com.nikron.conversion.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<K, E> {
    Optional<E> findById(K id);

    List<E> findAll();

    Optional<E> save(E tObject);

    void delete(K id);

    Optional<E> update(K id, E tObject);
}
