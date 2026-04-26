package Repository.Interfaces;

import Domain.Entity;
import Repository.Filter;

import java.util.List;
import java.util.Optional;

public interface IRepository<ID, E extends Entity<ID>> {
    void add(E e);
    boolean remove(ID id);
    Optional<E> findById(ID id);
    List<E> getAll();
    boolean update(E e);
    List<E> filter(Filter f);
}
