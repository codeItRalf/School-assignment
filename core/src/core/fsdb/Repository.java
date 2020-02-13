package core.fsdb;



import core.annotation.Entity;
import core.annotation.ForeignKey;
import core.annotation.Ignore;
import core.app.entity.Identity;
import core.app.entity.NoClass;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static core.fsdb.FileSystem.generateId;


public  class  Repository<T extends Identity> implements  RepositoryInterface<T> {
    private String dbName = MyDatabase.class.getSimpleName();
    private MyDatabase myDatabase = MyDatabase.getDatabase();


    @Override
    public <E extends Identity> E get(String entityType, int id) {
        E e = deserializeFile(entityType, id);
        String child = getChildName(e);
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
            List<E> list = extractChildrenFromParent(entity);
            if (list != null) {
                list.forEach(this::insert);
                setIgnoreFieldsToNull(entity);
            }
            myDatabase.serialize(entity);
        }
    }

    @Override
    public <E extends Identity> void remove(E entity) {
        if (getChildName(entity) != null && deepRemove(entity)) {
            removeChildren(entity);
        }
        removeFile(entity);
    }


    @Override
    public <E extends Identity> void update(E entity) {
        myDatabase.serialize(entity);
    }

    @Override
    public <E extends Identity> List<E> getAllOf(String type) {
        List<Integer> ids = FileSystem.getAllIds(dbName + "/" + type);
        return ids.stream().map(e -> {
            E object = deserializeFile(type, e);
            return get((E) object);
        }).collect(Collectors.toList());
    }

    private <E extends Identity> E get(E e) {
        String child = getChildName(e);
        if (child != null) {
            e = addChildrenToParent(e);
        }
        return e;
    }

    private <E extends Identity> List<E> extractChildrenFromParent(E entity) {
        String fieldName = entity.getClass().getAnnotation(Entity.class).foreignKey()[0].listOfChildren();
        if (fieldName.length() < 1) {
            return null;
        }
        Field field;
        List<E> list = null;
        try {
            field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            list = (List<E>) field.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }

    private <E extends Identity> E addChildrenToParent(E entity) {
        List<E> listOfChildren = getListOfChildren(entity).stream().map(this::get).collect(Collectors.toList());
        String fieldName = entity.getClass().getAnnotation(Entity.class).foreignKey()[0].listOfChildren();
        return updateField(fieldName, listOfChildren, entity);
    }

    private <E extends Identity> E updateField(String fieldName, List<E> list, E entity) {
        Field field;
        try {
            field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(entity, list);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return entity;
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

    private <E extends Identity> E deserializeFile(String path, int id) {
        return (E) myDatabase.deserialize(path, id);
    }

    private <E extends Identity> String getChildName(E entity) {
        String child = entity.getClass().getAnnotation(Entity.class).foreignKey()[0].child().getSimpleName();
        return child.equals(NoClass.class.getSimpleName()) ? null : child;
    }

    private <E extends Identity> void removeFile(E entity) {
        String path = dbName + "/" + entity.getClass().getSimpleName() + "/" + entity.getId();
        FileSystem.delete(path);
    }

    private <E extends Identity> boolean deepRemove(E entity) {
        return entity.getClass().getAnnotation(Entity.class).foreignKey()[0].onDelete() == ForeignKey.CASCADE;
    }

    private <E extends Identity> void removeChildren(E entity) {
        List<E> childList = getListOfChildren(entity);
        IntStream.range(0, childList.size()).forEach(index -> {
            if (index != childList.size() - 1) {
                removeFile(childList.get(index));
            } else {
                remove(childList.get(index));
            }
        });
    }

    private <E extends Identity> List<E> getListOfChildren(E entity) {
        return (List<E>) getAllOf(getChildName(entity)).stream().filter(e -> ifParentIdMatchesChild(entity, e)).collect(Collectors.toList());
    }

    private <E extends Identity> boolean ifParentIdMatchesChild(E parent, E child) {
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

    <E extends Identity> void setNewId(Identity identity) {
        identity.setId(generateId(MyDatabase.class.getSimpleName() + "/" + identity.getClass().getSimpleName()));
    }
}
