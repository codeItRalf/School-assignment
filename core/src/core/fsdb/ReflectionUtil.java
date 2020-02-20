package core.fsdb;

import core.fsdb.annotation.Entity;
import core.app.entity.NoClass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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

    static  <E extends Identity> String getChildClassName(E entity) {
        String child = entity.getClass().getAnnotation(Entity.class).foreignKey()[0].child().getSimpleName();
        return child.equals(NoClass.class.getSimpleName()) ? null : child;
    }

    static  <E extends Identity> String getParentClassName(Class<?> entity) {
        String parent = entity.getAnnotation(Entity.class).foreignKey()[0].parent().getSimpleName();
        return parent.equals(NoClass.class.getSimpleName()) ? null : parent;
    }
    static  <E extends Identity> String getChildClassName(Class<?> entity) {
        String child = entity.getAnnotation(Entity.class).foreignKey()[0].child().getSimpleName();
        return child.equals(NoClass.class.getSimpleName()) ? null : child;
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
          var children = ReflectionUtil.getChildrenFromParent(e);
          if (children != null && children.get(0).getClass().equals(entityClass)){
              Optional<? extends Identity> child = children.stream()
                      .parallel()
                      .filter(o-> o.getId() == id)
                      .findAny();
              if(child.isPresent()){
                  return child.get();
              }
          }else {
            return findNextEntity(children,entityClass,id);
          }
          return null;
      });

  }

}
