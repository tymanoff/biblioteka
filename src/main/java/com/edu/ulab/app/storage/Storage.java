package com.edu.ulab.app.storage;

import com.edu.ulab.app.entity.DistributedEntity;
import com.edu.ulab.app.exception.NotFoundException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Storage<T extends DistributedEntity> implements CrudMemoryRepository<T> {

    private final Map<Long, T> data = new ConcurrentHashMap<>();

    private long count;

    @Override
    public T save(T entity) {
        Long uuid = nextLongIdentifier();
        entity.setId(uuid);
        data.put(uuid, entity);
        return data.get(uuid);
    }

    @Override
    public T update(T entity) {
        data.put(entity.getId(), entity);
        return data.get(entity.getId());
    }

    @Override
    public T findById(Long id) {
        return data.get(id);
    }

    @Override
    public List<T> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public void deleteById(Long id) {
        data.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return data.get(id) != null;
    }

    public Long nextLongIdentifier() {
        long value;
        synchronized (this) {
            value = count++;
            if (count == Long.MAX_VALUE) {
                throw new NotFoundException
                        ("The maximum number of identifiers has been reached");
            }
        }
        return value;
    }
}
