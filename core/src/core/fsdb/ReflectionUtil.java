package core.fsdb;

import core.app.entity.Division;
import core.fsdb.annotation.Entity;
import core.app.entity.NoClass;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ReflectionUtil<E extends  Identity> {


     static  <E extends Identity> boolean childrenExist(Class<?> clazz) {
        return clazz.getAnnotation(Entity.class).foreignKey()[0].child() != NoClass.class;
    }

    static  <E extends Identity> boolean parentExist(Class<?> clazz) {
        return clazz.getAnnotation(Entity.class).foreignKey()[0].parent() != NoClass.class;
    }

    static <E extends Identity> String getParentIdVariableName(E entity){
        return  entity.getClass().getAnnotation(Entity.class).foreignKey()[0].parentId();
    }

    static <E extends Identity> String getChildVariableName(E entity){
        return  entity.getClass().getAnnotation(Entity.class).foreignKey()[0].listOfChildren();
    }

    static  <E extends Identity> ArrayList<E> getChildrenFromParent(E entity) {
        String fieldName = getChildVariableName(entity);
        if (fieldName.length() < 1) {
            return null;
        }
        Field field;
        ArrayList<E> list = null;
        try {
            field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            list = (ArrayList<E>) field.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }

    static  <E extends Identity> Class<?> getChildClass(E entity) {
        Class<?> child = entity.getClass().getAnnotation(Entity.class).foreignKey()[0].child();
        return child;
    }

    static  <E extends Identity> Class<?> getParentClass(Class<?> entity) {
        Class<?> parent = entity.getAnnotation(Entity.class).foreignKey()[0].parent();
        return parent;
    }
    static  <E extends Identity> Class<?> getChildClass(Class<?> entity) {
        Class<?> child = entity.getAnnotation(Entity.class).foreignKey()[0].child();
        return child;
    }

    static <E extends Identity> E setField(String fieldName, List<E> list, E entity) {
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

     static <E extends Identity> E findNextEntity(List<? extends Identity> entities, Class<?> entityClass, int id){
        return (E) entities.stream().map(e->{
            if(e.getClass().getSimpleName().equals(Division.class.getSimpleName())){
                System.out.println("Division id: " + e.getId());
            }
            E entity = null;
            if(!e.getClass().getSimpleName().equals(entityClass.getSimpleName())){
                var children = getChildrenFromParent(e);
                entity = findNextEntity(children,entityClass,id);
            }else {
                if(e.getId() == id){
                    entity = (E) e;
                }
            }
            return  entity;
        }).collect(Collectors.toList()).stream().filter(Objects::nonNull).findAny().get();
  }

    static <E extends Identity> ArrayList<E> findAllEntities(ArrayList<E> entities, Class<?> entityClass){
        ArrayList<E> listOfEntities = new ArrayList<>();
             entities.forEach(e->{
                    if(entityClass.getSimpleName().equals(e.getClass().getSimpleName())){
                        listOfEntities.addAll(entities);
                    }else if (childrenExist(e.getClass())) {
                        var children = ReflectionUtil.getChildrenFromParent(e);
                        listOfEntities.addAll(findAllEntities(children,entityClass));
                    }
             });
        return listOfEntities;
    }

}
