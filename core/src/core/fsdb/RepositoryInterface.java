package core.fsdb;



import core.app.entity.Division;
import core.app.entity.Identity;

import java.util.List;

public interface RepositoryInterface<E extends Identity> {
     E get(int id);
     void insert(E entity);
     void remove(E entity);
     void update(E entity);
     List<E> getAll();
     List<E> getWithIntFieldEqual(String field, int i);
     List<E> getWithIntFieldLessOrEqual(String field, int i);
     List<E> getWithIntFieldMoreOrEqual(String field, int i);
     List<E> getWithStringFieldContains(String field, String value);
     void removeFile(E entity);

}
