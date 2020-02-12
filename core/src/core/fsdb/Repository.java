package core.fsdb;



import core.annotation.Entity;
import core.annotation.ForeignKey;
import core.annotation.Ignore;
import core.app.entity.Identity;
import core.app.entity.NoClass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static core.fsdb.FileSystem.generateId;


public  class  Repository<T extends Identity> implements  RepositoryInterface<T> {
    private String dbName = MyDatabase.class.getSimpleName();
    private MyDatabase myDatabase = MyDatabase.getDatabase();


    @Override
    public  T get(String entityType, int id) {
        T t = deserializeFile(entityType,id);
        String child = getChildName(t);
        if(child!= null){
         t = addChildrenToParent(t);
            }
        return t;
       }




    @Override
    public <T extends Identity>  void insert(T entity) {
        if (entity.getId() == -1) {
            entity.setId(generateId(dbName + "/" + entity.getClass().getSimpleName()));
        }
        if (!FileSystem.exists(dbName + "/" + entity.getClass().getSimpleName() + "/" + entity.getId())){
            List<T> list =  extractChildrenFromParent(entity);
            if(list != null){
                list.forEach(this::insert);
                setIgnoreFieldsToNull(entity);
            }
            myDatabase.serialize(entity);
        }
    }

    @Override
     public  <T extends Identity> void remove(T entity) {
        if (getChildName(entity) != null && deepRemove(entity)) {
            removeChildren(entity);
        }
        removeFile(entity);
    }


    @Override
    public  <T extends Identity> void update(T entity) {
//       List<T> list =  extractChildrenFromParent(entity);
//       if(list != null){
//           list.forEach(this::update);
//           setIgnoreFieldsToNull(entity);
//       }
        myDatabase.serialize(entity);
    }

    @Override
    public  List<T> getAllOf(String type) {
        List<Integer> ids = FileSystem.getAllIds(dbName + "/" + type);
        return ids.stream().map(e -> {
         T object =   deserializeFile(type, e);
         return  get((T) object);
        }).collect(Collectors.toList());
    }

    private  <T extends Identity> T get(T t) {
        String child = getChildName(t);
        if(child!= null){
            t = addChildrenToParent(t);
        }
        return t;
    }

    private <T extends Identity>  List<T> extractChildrenFromParent(T entity){
        String fieldName = entity.getClass().getAnnotation(Entity.class).foreignKey()[0].listOfChildren();
        if(fieldName.length() < 1){
            return null;
        }
        Field field;
        List<T> list = new ArrayList<>();
        try {
            field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            list = (List<T>) field.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }

    private  <T extends Identity> T addChildrenToParent(T entity){
        List<T> listOfChildren = getListOfChildren(entity).stream().map(this::get).collect(Collectors.toList());
        String fieldName = entity.getClass().getAnnotation(Entity.class).foreignKey()[0].listOfChildren();
        return updateField(fieldName,listOfChildren,entity);
    }

    private  <T extends Identity> T updateField(String fieldName, List<T> list, T entity){
        Field field;
        try {
            field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(entity,list);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return  entity;
    }

    private <T extends Identity>  void setIgnoreFieldsToNull(T entity){

        Arrays.stream(entity.getClass().getDeclaredFields()).forEach(e -> {
            if (e.getAnnotation(Ignore.class) != null) {
                try {
                    Field field = entity.getClass().getDeclaredField(e.getName());
                    field.setAccessible(true);
                    field.set(entity,null);
                } catch (NoSuchFieldException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private  <T extends Identity> T deserializeFile(String path, int id){
        return (T) myDatabase.deserialize(path, id);
    }

    private  <T extends Identity> String getChildName(T entity) {
        String child = entity.getClass().getAnnotation(Entity.class).foreignKey()[0].child().getSimpleName();
        return child.equals(NoClass.class.getSimpleName()) ? null : child;
    }

    private  <T extends Identity> void removeFile(T entity) {
        String path = dbName + "/" + entity.getClass().getSimpleName() + "/" + entity.getId();
        FileSystem.delete(path);
    }

    private  <T extends Identity> boolean deepRemove(T entity) {
        return entity.getClass().getAnnotation(Entity.class).foreignKey()[0].onDelete() == ForeignKey.CASCADE;
    }

    private  <T extends Identity> void removeChildren(T entity) {
        List<T> childList = getListOfChildren(entity);
        IntStream.range(0, childList.size()).forEach(index -> {
            if (index != childList.size() -1) {
            removeFile(childList.get(index));
            }else{
             remove(childList.get(index));
            }
        });
    }

    private  <T extends Identity> List<T> getListOfChildren(T entity){
        return (List<T>) getAllOf(getChildName(entity)).stream().filter(e -> ifParentIdMatchesChild(entity,e)).collect(Collectors.toList());
    }

    private  <T extends Identity> boolean ifParentIdMatchesChild(T parent, T child) {
        String parentId = child.getClass().getAnnotation(Entity.class).foreignKey()[0].parentId();
        if(parentId.length() < 1){
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
}
