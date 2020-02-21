package core.fsdb;


import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.NoClass;
import core.app.entity.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public abstract class ViewModel<E extends Identity> {

    private  Repository<E> repository;
    private  List<E> entities;
    private  MyObserver<E> myObserver;

    public ViewModel(Class<?> SuperParentClass) {
        repository = new Repository<>();
        entities = new ArrayList<>(repository.getAllOf(SuperParentClass.getSimpleName()));
        myObserver = new MyObserver<>(entities, repository);
    }


    protected E get(int id, Class<?> entityClass) {
       if(ReflectionUtil.getParentClass(entityClass) != NoClass.class && entities.get(id).getClass().equals(entityClass)){
            return entities.get(id);
       }else {
           return (E) ReflectionUtil.findNextEntity(entities,entityClass,id);
       }
    }

    protected  ArrayList<E> getAll(Class<?> entityClass) {
        if(ReflectionUtil.getParentClass(entityClass) != NoClass.class && entities.get(0).getClass().equals(entityClass)){
            return (ArrayList<E>) entities;
        }else {
            return (ArrayList<E>) ReflectionUtil.findAllEntities(entities,entityClass);
        }
    }



    protected E getParent(E entity) {
        Class<?> parentClass = ReflectionUtil.getParentClass(entity.getClass());
        String parentVariableName = ReflectionUtil.getParentIdVariableName(entity);
        int parentId = (int) ReflectionUtil.getField(entity,parentVariableName);
        return  get(parentId,parentClass);
    }



    public void insertEntity(E entity) {
        Class<E> parentClass = (Class<E>) ReflectionUtil.getParentClass(entity.getClass());
        assert parentClass != null;
        if(parentClass.getSimpleName().equals(NoClass.class.getSimpleName())) {
            entities.add(entity);
        }else {
            E parent = getParent(entity);
            List<E> listOfChildren = ReflectionUtil.getChildrenFromParent(parent);
            assert listOfChildren != null;
            listOfChildren.add(entity);
        }
        repository.setNewId(entity);
        entity.addPropertyChangeListener(myObserver);
        repository.insert(entity);
    }

    public void deleteEntity(E entity) {
        Class<E> parentClass = (Class<E>) ReflectionUtil.getParentClass(entity.getClass());
        assert parentClass != null;
        if(parentClass.getSimpleName().equals(NoClass.class.getSimpleName())) {
            entities.remove(entity);
        }else {
            E parent = getParent(entity);
            List<E> listOfChildren = ReflectionUtil.getChildrenFromParent(parent);
            assert listOfChildren != null;
            listOfChildren.remove(entity);
        }
        repository.remove(entity);
    }



}
