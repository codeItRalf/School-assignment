package core.database;


import core.annotation.Ignore;
import core.annotation.Positive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static core.database.FileSystem.generateId;



public abstract class Repository<E extends Identity> implements RepositoryInterface<E> {
    private final String pathWithSlash;


    protected List<E> entities;
    private final MyObserver<E> myObserver;

    public Repository(String pathWithSlash) {
        this.entities =  initEntityList(pathWithSlash);
        this.pathWithSlash = pathWithSlash;
        this.myObserver = new MyObserver<E>(entities,this);
    }

    @Override
    public E get(int id) {
        return (E) entities.parallelStream()
                .filter(e -> e.getId() == id)
                .findAny()
                .get();
    }


    @Override
     public void insert(E entity) {
        if (entity.getId() == -1) {
            setNewId(entity);
        }
        if (!FileSystem.exists(pathWithSlash + entity.getId())
        && checkPositiveValueIsValid(entity)) {
            entity.addPropertyChangeListener(myObserver);
            entities.add(entity);
            FileSystem.serialize(pathWithSlash, entity);
        }
    }

    @Override
    public void remove(E entity) {
        entities.remove(entity);
        removeFile(entity);
    }


    @Override
    public void update(E entity) {
        if (checkPositiveValueIsValid(entity)){
            setIgnoreFieldsToNull(entity);
            checkPositiveValueIsValid(entity);
            FileSystem.serialize(pathWithSlash, entity);
        }
    }


    @Override
    public List<E> getAll() {
        return (List<E>) entities;
    }

    @Override
    public List<E> getWithIntFieldEqual(String fieldName, int i) {
        return (List<E>) entities.stream()
                .parallel()
                .filter(e-> (int) ReflectionUtil.getField(e,fieldName) == i)
                .collect(Collectors.toList());
    }

    @Override
    public  List<E> getWithIntFieldLessOrEqual(String fieldName, int i) {
        return (List<E>) entities.stream()
                .parallel()
                .filter(e-> (int) ReflectionUtil.getField(e,fieldName) <= i)
                .collect(Collectors.toList());
    }

    @Override
    public List<E> getWithIntFieldMoreOrEqual(String fieldName, int i) {
        return (List<E>) entities.stream()
                .parallel()
                .filter(e-> (int) ReflectionUtil.getField(e,fieldName) >= i)
                .collect(Collectors.toList());
    }

    @Override
    public  List<E> getWithStringFieldContains(String fieldName, String value) {
        return (List<E>) entities.stream()
                .parallel()
                .filter(e->  ReflectionUtil.getField(e,fieldName).toString().toLowerCase().contains(value.toLowerCase()))
                .collect(Collectors.toList());
    }

    private  void setIgnoreFieldsToNull(E entity) {
        Arrays.stream(entity.getClass().getDeclaredFields()).forEach(e -> {
            if (e.getAnnotation(Ignore.class) != null) {
                ReflectionUtil.setField(entity,e.getName(),null);
            }
        });
    }

    private  boolean checkPositiveValueIsValid(E entity) {
     return  Arrays.stream(entity.getClass().getDeclaredFields()).allMatch(e -> {
            boolean isValid = true;
            if (e.getAnnotation(Positive.class) != null) {
                if((int) ReflectionUtil.getField(entity,e.getName()) < 0){
                    isValid = false;
                }
            }
           return isValid;
        });
    }


    @Override
    public E getWithIntFieldLowestValue(String field) {
        return entities.parallelStream()
                .min(Comparator.comparingInt(a -> (int) ReflectionUtil.getField(a, field)))
                .get();
    }

    @Override
    public E getWithIntFieldHighestValue(String field) {
        return entities.parallelStream()
                .max(Comparator.comparingInt(a -> (int) ReflectionUtil.getField(a, field)))
                .get();
    }


    public void removeFile(E entity) {
        String path = pathWithSlash + entity.getId();
        FileSystem.delete(path);
    }



    protected   void setNewId(E entity) {
        entity.setId(generateId(pathWithSlash));
    }



    protected   List<E> initEntityList(String path) {
        List<Integer> ids = FileSystem.getAllIds(path);
        if(ids == null || ids.size() < 1){
            return new ArrayList<>();
        }
        return ids.stream().map(e -> {
            E object = (E) FileSystem.deserialize(path, e);
            return object;
        }).collect(Collectors.toList());
    }

}
