package core.fsdb;



import core.app.entity.Identity;

import java.util.List;

public interface RepositoryInterface<T extends Identity> {
    <O extends Identity> O get(String entity, int id);
    <O extends Identity> void insert(O entity);
    <O extends Identity> void remove(O entity);
    <O extends Identity> void update(O entity);
}
