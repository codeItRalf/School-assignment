package core.fsdb;

import core.annotation.Entity;
import core.app.entity.Identity;
import core.app.entity.NoClass;

import java.lang.reflect.Field;
import java.util.List;


public class ReflectionUtil<E extends  Identity> {


    public static  <E extends Identity> boolean childrenExist(E entity) {
        return entity.getClass().getAnnotation(Entity.class).foreignKey()[0].child() != NoClass.class;
    }

    static public <E extends Identity> List<E> getChildrenFromParent(E entity) {
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

    static  <E extends Identity> String getChildClassName(E entity) {
        String child = entity.getClass().getAnnotation(Entity.class).foreignKey()[0].child().getSimpleName();
        return child.equals(NoClass.class.getSimpleName()) ? null : child;
    }

    static <E extends Identity> E updateField(String fieldName, List<E> list, E entity) {
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


}
