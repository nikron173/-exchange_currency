package com.nikron.conversion.repository;

import java.util.List;
import java.util.Optional;

public interface ImpRepository<K, E> {
    Optional<E> findById(K id);
    Optional<List<E>> findAll();
    Optional<E> save(E tObject);

    void delete(K id);

    Optional<E> change(K id, E tObject);
}
