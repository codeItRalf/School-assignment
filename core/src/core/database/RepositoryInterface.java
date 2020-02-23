package core.database;



import java.util.List;

public interface RepositoryInterface<E extends Identity> {
     E get(int id);
     void insert(E entity);
     void remove(E entity);
     void update(E entity);
     void removeFile(E e);
     List<E> getAll();
     List<E> getWithIntFieldEqual(String field, int i);
     List<E> getWithIntFieldLessOrEqual(String field, int i);
     List<E> getWithIntFieldMoreOrEqual(String field, int i);
     E getWithIntFieldLowestValue(String field);
     E getWithIntFieldHighestValue(String field);
     List<E> getWithStringFieldContains(String field, String value);


}
