package core.fsdb;



import core.app.entity.Division;
import core.app.entity.Identity;

import java.util.List;

public interface RepositoryInterface<T extends Identity> {
    <E extends Identity> E get(int id);
    <E extends Identity> void insert(E entity);
    <E extends Identity> void remove(E entity);
    <E extends Identity> void update(E entity);
    <E extends Identity> List<E> getAll();
    <E extends Identity> List<E> getWithIntFieldEqual(String field, int i);
    <E extends Identity> List<E> getWithIntFieldLessOrEqual(String field, int i);
    <E extends Identity> List<E> getWithIntFieldMoreOrEqual(String field, int i);
    <E extends Identity> List<E> getWithStringFieldContains(String field, String value);
    <E extends Identity> void removeFile(E entity);

}
