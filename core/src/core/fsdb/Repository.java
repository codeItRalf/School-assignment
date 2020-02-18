package core.fsdb;



import core.annotation.Entity;
import core.annotation.ForeignKey;
import core.annotation.Ignore;
import core.app.entity.Identity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static core.fsdb.FileSystem.generateId;
import static core.fsdb.ReflectionUtil.getChildrenFromParent;
import static core.fsdb.ReflectionUtil.getChildClassName;


public abstract class Repository<T extends Identity> implements RepositoryInterface<T> {
    private final String dbName = MyDatabase.class.getSimpleName();
    private final MyDatabase myDatabase = MyDatabase.getDatabase();




    @Override
    public <E extends Identity> E get(String entityType, int id) {
        E e = (E) FileSystem.deserialize(entityType, id);
        String child = getChildClassName(e);
        if (child != null) {
            e = addChildrenToParent(e);
        }
        return e;
    }


    @Override
    public <E extends Identity> void insert(E entity) {
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
    public <E extends Identity> void remove(E entity) {
        if (getChildClassName(entity) != null && deepRemove(entity)) {
            removeChildren(entity);
        }
        removeFile(entity);
    }


    @Override
    public <E extends Identity> void update(E entity) {
        FileSystem.serialize(entity);
    }








    private <E extends Identity> void setIgnoreFieldsToNull(E entity) {

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




    private <E extends Identity> void removeFile(E entity) {
        String path = dbName + "/" + entity.getClass().getSimpleName() + "/" + entity.getId();
        FileSystem.delete(path);
    }

    private <E extends Identity> boolean deepRemove(E entity) {
        return entity.getClass().getAnnotation(Entity.class).foreignKey()[0].onDelete() == ForeignKey.CASCADE;
    }

    private <E extends Identity> void removeChildren(E entity) {
        List<E> childList = deserializeChildrenToList(entity);
        IntStream.range(0, childList.size()).forEach(index -> {
            if (index != childList.size() - 1) {
                removeFile(childList.get(index));
            }
                remove(childList.get(index));

        });
    }

    public  <E extends  Identity>  List<E> deserializeChildrenToList(E entity) {
        return (List<E>) getAllOf(getChildClassName(entity)).stream().filter(e -> ifParentIdMatchesChild(entity, e)).collect(Collectors.toList());
    }

    private   <E extends Identity> boolean ifParentIdMatchesChild(E parent, E child) {
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



    public <E extends Identity> void setNewId(Identity identity) {
        identity.setId(generateId(MyDatabase.class.getSimpleName() + "/" + identity.getClass().getSimpleName()));
    }


    public <E extends Identity> E get(E e) {
        String child = getChildClassName(e);
        if (child != null) {
            e = addChildrenToParent(e);
        }
        return e;
    }

    public   <E extends Identity> List<E> getAllOf(String type) {
        List<Integer> ids = FileSystem.getAllIds(MyDatabase.class.getSimpleName() + "/" + type);
        return ids.stream().map(e -> {
            E object = (E) FileSystem.deserialize(type, e);
            return get(object);
        }).collect(Collectors.toList());
    }



    <E extends Identity> E addChildrenToParent(E entity) {
        List<E> listOfChildren = deserializeChildrenToList(entity).stream().map(this::get).collect(Collectors.toList());
        String fieldName = entity.getClass().getAnnotation(Entity.class).foreignKey()[0].listOfChildren();
        return ReflectionUtil.updateField(fieldName, listOfChildren, entity);
    }

}
