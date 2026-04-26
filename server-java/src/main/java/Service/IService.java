package Service;

import Domain.Entity;
import Repository.Filter;

import java.util.List;
import java.util.Optional;

public interface IService<ID, E extends Entity<ID>> {
    void add(E entity);
    boolean remove(ID id);
    Optional<E> findById(ID id);
    List<E> getAll();
    boolean update(E entity);
    List<E> filter(Filter filter);
}