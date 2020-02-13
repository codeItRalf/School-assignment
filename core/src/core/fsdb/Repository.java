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
    public <O extends Identity> O get(String entityType, int id) {
        O o = deserializeFile(entityType,id);
        String child = getChildName(o);
        if(child!= null){
         o = addChildrenToParent(o);
            }
        return o;
       }




    @Override
    public <O extends Identity>  void insert(O entity) {
        if (entity.getId() == -1) {
            entity.setId(generateId(dbName + "/" + entity.getClass().getSimpleName()));
        }
        if (!FileSystem.exists(dbName + "/" + entity.getClass().getSimpleName() + "/" + entity.getId())){
            List<O> list =  extractChildrenFromParent(entity);
            if(list != null){
                list.forEach(this::insert);
                setIgnoreFieldsToNull(entity);
            }
            myDatabase.serialize(entity);
        }
    }

    @Override
     public  <O extends Identity> void remove(O entity) {
        if (getChildName(entity) != null && deepRemove(entity)) {
            removeChildren(entity);
        }
        removeFile(entity);
    }


    @Override
    public  <O extends Identity> void update(O entity) {
        myDatabase.serialize(entity);
    }

    @Override
    public <O extends Identity>  List<O> getAllOf(String type) {
        List<Integer> ids = FileSystem.getAllIds(dbName + "/" + type);
        return ids.stream().map(e -> {
         O object =   deserializeFile(type, e);
         return  get((O) object);
        }).collect(Collectors.toList());
    }

    private  <O extends Identity> O get(O o) {
        String child = getChildName(o);
        if(child!= null){
            o = addChildrenToParent(o);
        }
        return o;
    }

    private <O extends Identity>  List<O> extractChildrenFromParent(O entity){
        String fieldName = entity.getClass().getAnnotation(Entity.class).foreignKey()[0].listOfChildren();
        if(fieldName.length() < 1){
            return null;
        }
        Field field;
         List<O> list = null;
        try {
            field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            list = ( List<O>) field.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }

    private  <O extends Identity> O addChildrenToParent(O entity){
        List<O> listOfChildren = getListOfChildren(entity).stream().map(this::get).collect(Collectors.toList());
        String fieldName = entity.getClass().getAnnotation(Entity.class).foreignKey()[0].listOfChildren();
        return updateField(fieldName,listOfChildren,entity);
    }

    private  <O extends Identity> O updateField(String fieldName, List<O> list, O entity){
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

    private <O extends Identity>  void setIgnoreFieldsToNull(O entity){

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

    private  <O extends Identity> O deserializeFile(String path, int id){
        return (O) myDatabase.deserialize(path, id);
    }

    private  <O extends Identity> String getChildName(O entity) {
        String child = entity.getClass().getAnnotation(Entity.class).foreignKey()[0].child().getSimpleName();
        return child.equals(NoClass.class.getSimpleName()) ? null : child;
    }

    private  <O extends Identity> void removeFile(O entity) {
        String path = dbName + "/" + entity.getClass().getSimpleName() + "/" + entity.getId();
        FileSystem.delete(path);
    }

    private  <O extends Identity> boolean deepRemove(O entity) {
        return entity.getClass().getAnnotation(Entity.class).foreignKey()[0].onDelete() == ForeignKey.CASCADE;
    }

    private  <O extends Identity> void removeChildren(O entity) {
        List<O> childList = getListOfChildren(entity);
        IntStream.range(0, childList.size()).forEach(index -> {
            if (index != childList.size() -1) {
            removeFile(childList.get(index));
            }else{
             remove(childList.get(index));
            }
        });
    }

    private  <O extends Identity> List<O> getListOfChildren(O entity){
        return (List<O>) getAllOf(getChildName(entity)).stream().filter(e -> ifParentIdMatchesChild(entity,e)).collect(Collectors.toList());
    }

    private  <O extends Identity> boolean ifParentIdMatchesChild(O parent, O child) {
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

    <O extends Integer> void setNewId(Identity identity){
        identity.setId(generateId(MyDatabase.class.getSimpleName() + "/" + identity.getClass().getSimpleName()));
    }
}
