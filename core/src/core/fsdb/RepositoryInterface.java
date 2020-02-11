package core.fsdb;



import core.app.entity.Identity;

import java.util.List;

public interface RepositoryInterface<T extends Identity> {
    T get(String entity, int id);
    <T extends Identity> void insert( T entity);
    void remove(T entity);
    void update(T entity);
     List<T> getAllOf(String type);
}
