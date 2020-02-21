package core.fsdb;


public interface RepositoryInterface<E extends Identity> {
     E get(String entity, int id);
     void insert(E entity);
     void remove(E entity);
     void update(E entity);
}
