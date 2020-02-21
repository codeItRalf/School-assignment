package core.fsdb;



import core.fsdb.annotation.Entity;
import core.fsdb.annotation.ForeignKey;
import core.fsdb.annotation.Ignore;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static core.fsdb.FileSystem.generateId;
import static core.fsdb.ReflectionUtil.getChildrenFromParent;



public  class Repository<E extends Identity> implements RepositoryInterface<E> {
    private final String dbName = MyDatabase.class.getSimpleName();
    private final MyDatabase myDatabase = MyDatabase.getDatabase();




    @Override
    public  E get(String entityType, int id) {
        E e = (E) FileSystem.deserialize(entityType, id);
        if (ReflectionUtil.childrenExist(e.getClass())) {
            e = addChildrenToParent(e);
        }
        return e;
    }


    @Override
    public  void insert(E entity) {
        if (entity.getId() == -1) {
            setNewId(entity);
        }
        if (!FileSystem.exists(dbName + "/" + entity.getClass().getSimpleName() + "/" + entity.getId())) {
            List<E> list =  getChildrenFromParent(entity);
            if (list != null) {
                list.forEach(this::insert);
                setIgnoreFieldsToNull(entity);
            }
            FileSystem.serialize(entity);
        }
    }

    @Override
    public void remove(E entity) {
        if (ReflectionUtil.childrenExist(entity.getClass()) && deepRemove(entity)) {
            removeChildren(entity);
        }
        removeFile(entity);
    }


    @Override
    public void update(E entity) {
        FileSystem.serialize(entity);
    }



    private void setIgnoreFieldsToNull(E entity) {

        Arrays.stream(entity.getClass().getDeclaredFields()).forEach(e -> {
            if (e.getAnnotation(Ignore.class) != null) {
                try {
                    Field field = entity.getClass().getDeclaredField(e.getName());
                    field.setAccessible(true);
                    field.set(entity, null);
                } catch (NoSuchFieldException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }




    private  void removeFile(E entity) {
        String path = dbName + "/" + entity.getClass().getSimpleName() + "/" + entity.getId();
        FileSystem.delete(path);
    }

    private boolean deepRemove(E entity) {
        return entity.getClass().getAnnotation(Entity.class).foreignKey()[0].onDelete() == ForeignKey.CASCADE;
    }

    private  void removeChildren(E entity) {
        List<E> childList = deserializeChildrenToList(entity);
        IntStream.range(0, childList.size()).forEach(index -> {
            if (index != childList.size() - 1) {
                removeFile(childList.get(index));
            }
                remove(childList.get(index));

        });
    }

    public   List<E> deserializeChildrenToList(E entity) {
        return  getAllOf(ReflectionUtil.getChildClass(entity).getSimpleName()).stream().filter(e -> ifParentIdMatchesChild(entity,  e)).collect(Collectors.toList());
    }

    private   boolean ifParentIdMatchesChild(E parent, E child) {
        String parentId = child.getClass().getAnnotation(Entity.class).foreignKey()[0].parentId();
        if (parentId.length() < 1) {
            return false;
        }
        Field field;
        int fieldValue = -1;
        try {
            field = child.getClass().getDeclaredField(parentId);
            field.setAccessible(true);
            fieldValue = (int) field.get(child);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return fieldValue == parent.getId();
    }



    public void setNewId(Identity identity) {
        identity.setId(generateId(MyDatabase.class.getSimpleName() + "/" + identity.getClass().getSimpleName()));
    }


    public  E getChildren(E e) {
        if (ReflectionUtil.childrenExist(e.getClass())) {
            e = addChildrenToParent(e);
        }
        return e;
    }

    public   List<E> getAllOf(String type) {
        List<Integer> ids = FileSystem.getAllIds(MyDatabase.class.getSimpleName() + "/" + type);
        return ids.stream().map(e -> {
            E object = (E) FileSystem.deserialize(type, e);
            return getChildren(object);
        }).collect(Collectors.toList());
    }



      E addChildrenToParent(E entity) {
        List<E> listOfChildren = deserializeChildrenToList(entity).stream().map(this::getChildren).collect(Collectors.toList());
        String fieldName = ReflectionUtil.getChildVariableName(entity);
        return ReflectionUtil.setField(fieldName, listOfChildren, entity);
    }


}
