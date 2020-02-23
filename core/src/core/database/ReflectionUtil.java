package core.database;

import core.annotation.Entity;
import core.annotation.Table;
import core.app.entity.NoClass;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;


public class ReflectionUtil<E extends  Identity> {



    static  <E extends Identity> boolean childrenExist(Class<?> clazz) {
        return clazz.getAnnotation(Entity.class).foreignKey()[0].child() != NoClass.class;
    }

    static  <E extends Identity> boolean parentExist(Class<?> clazz) {
        return clazz.getAnnotation(Entity.class).foreignKey()[0].parent() != NoClass.class;
    }

   public static  <E extends Identity> String getParentIdVariableName(Class<E> clazz){
        return clazz.getAnnotation(Entity.class).foreignKey()[0].parentId();
    }

    public static <E extends Identity> String getChildVariableName(Class<E> clazz){
        return  clazz.getAnnotation(Entity.class).foreignKey()[0].listOfChildren();
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

  static <E extends Identity> String getNameOfChildrenList(E entity){
      return entity.getClass().getAnnotation(Entity.class).foreignKey()[0].listOfChildren();
  }

}
