package core.fsdb;


import core.annotation.Ignore;
import core.app.entity.Identity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static core.fsdb.FileSystem.generateId;



public abstract class Repository<E extends Identity> implements RepositoryInterface<E> {
    private final String dbName = MyDatabase.class.getSimpleName();


    private List<E> entities;
    private final MyObserver<E> myObserver;

    public Repository(String entityName) {
        this.entities =  initEntityList(entityName);
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
        if (!FileSystem.exists(dbName + "/" + entity.getClass().getSimpleName() + "/" + entity.getId())) {
//            List<E> list =  getChildrenFromParent(entity);
//            if (list != null) {
//                list.forEach(this::insert);
//                setIgnoreFieldsToNull(entity);
//            }
            entity.addPropertyChangeListener(myObserver);
            entities.add(entity);
            FileSystem.serialize(entity);
        }
    }

    @Override
    public void remove(E entity) {
        entities.remove(entity);
        removeFile(entity);
    }


    @Override
    public void update(E entity) {
        setIgnoreFieldsToNull(entity);
        FileSystem.serialize(entity);
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

    @Override
    public   void removeFile(E entity) {
        String path = dbName + "/" + entity.getClass().getSimpleName() + "/" + entity.getId();
        FileSystem.delete(path);
    }



    public  void setNewId(E entity) {
        entity.setId(generateId(MyDatabase.class.getSimpleName() + "/" + entity.getClass().getSimpleName()));
    }



    protected   List<E> initEntityList(String type) {
        List<Integer> ids = FileSystem.getAllIds(MyDatabase.class.getSimpleName() + "/" + type);
        return ids.stream().map(e -> {
            E object = (E) FileSystem.deserialize(type, e);
            return object;
        }).collect(Collectors.toList());
    }

}
