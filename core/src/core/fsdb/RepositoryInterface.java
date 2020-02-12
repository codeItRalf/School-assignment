package core.fsdb;



import core.app.entity.Identity;

import java.util.List;

public interface RepositoryInterface<T extends Identity> {
    <T extends Identity> T get(String entity, int id);
    <T extends Identity> void insert( T entity);
    <T extends Identity> void remove(T entity);
    <T extends Identity> void update(T entity);
    <T extends Identity> List<T> getAllOf(String type);
}
