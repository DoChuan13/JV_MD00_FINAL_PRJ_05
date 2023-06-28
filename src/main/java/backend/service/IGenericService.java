package backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IGenericService<T> {
    Iterable<T> findAll();

    Page<T> findAll(Pageable pageable);

    void save(T t);

    Optional<T> findById(Long id);

    void deleteById(Long id);
}
