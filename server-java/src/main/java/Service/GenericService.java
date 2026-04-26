package Service;


import Domain.Entity;
import Repository.Filter;
import Repository.GenericRepository;

import java.util.List;
import java.util.Optional;

public abstract class GenericService<ID, E extends Entity<ID>> implements IService<ID, E> {

    protected final GenericRepository<ID, E> repository;

    protected GenericService(GenericRepository<ID, E> repository) {
        this.repository = repository;
    }

    @Override
    public void add(E entity) {
        repository.add(entity);
    }

    @Override
    public boolean remove(ID id) {
        return repository.remove(id);
    }

    @Override
    public Optional<E> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public List<E> getAll() {
        return repository.getAll();
    }

    @Override
    public boolean update(E entity) {
        return repository.update(entity);
    }

    @Override
    public List<E> filter(Filter filter) {
        return repository.filter(filter);
    }
}
