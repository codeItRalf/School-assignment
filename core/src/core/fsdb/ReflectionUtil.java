package core.fsdb;

import core.annotation.Entity;
import core.annotation.Ignore;
import core.annotation.Table;
import core.app.entity.Identity;
import core.app.entity.NoClass;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


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

    static  <E extends Identity> Class getChildClass(E entity) {
        return entity.getClass().getAnnotation(Entity.class).foreignKey()[0].child();
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

    static void setField(Object object, String fieldName, Object newValue){
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, newValue);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    static Object getField(Object object, String fieldName){
        Field field;
        Object fieldValue = null;
        try {
            field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            fieldValue =  field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return fieldValue;
    }


     static <E extends RepositoryInterface> E  getRepository(Object instance , Class<?> entityClass) {
         E repository = null;
         Field field = Arrays.stream(instance.getClass().getDeclaredFields())
                .filter(e -> e.getAnnotation(Table.class).entity().equals(entityClass))
                .findFirst()
                .get();
        try {
            field.setAccessible(true);
           repository = (E) field.get(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  repository;
    }



}
