package com.nikron.conversion.repository;

import java.util.List;
import java.util.Optional;

public interface ImpRepository<T> {
    Optional<T> findById(long id);
    Optional<List<T>> findAll();
    Optional<T> save(T tObject);
    Optional<T> change(long id, T tObject);
    void delete(long id);
}
