package core.fsdb;


import core.annotation.Entity;
import core.annotation.ForeignKey;
import core.app.entity.Identity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public abstract class  ViewModel<T extends Identity> {




    <E extends Identity> E addChildrenToParent(E entity) {
        List<E> listOfChildren = getChildrenToParent(entity);
        String fieldName = ReflectionUtil.getNameOfChildrenList(entity);
        return ReflectionUtil.updateField(fieldName, listOfChildren, entity);
    }




    public  <E extends  Identity>  List<E> getChildrenToParent(E entity) {
        return (List<E>) getRepository(this, ReflectionUtil.getChildClass(entity))
                .getAll()
                .stream()
                .filter(e -> ifParentIdMatchesChild(entity, (E) e))
                .collect(Collectors.toList());
    }

    private   <E extends Identity> boolean ifParentIdMatchesChild(E parent, E child) {
        String parentId = child.getClass().getAnnotation(Entity.class).foreignKey()[0].parentId();
        if (parentId.length() < 1) {
            return false;
        }
        int fieldValue = -1;
        fieldValue = (int) ReflectionUtil.getField(child,parentId);
        return fieldValue == parent.getId();
    }

    private <E extends Identity> boolean deepRemove(E entity) {
        return entity.getClass().getAnnotation(Entity.class).foreignKey()[0].onDelete() == ForeignKey.CASCADE;
    }

    private <E extends Identity> void removeChildren(E entity) {
        List<E> childList = getChildrenToParent(entity);
        IntStream.range(0, childList.size()).forEach(index -> {
            if (index != childList.size() - 1) {
                getRepository(this, entity.getClass()).removeFile(childList.get(index));
            }
            removeChildren(childList.get(index));
        });
    }


    protected <E extends  RepositoryInterface<T>> E getRepository(ViewModel<T> classInstance, Class<?>  table){
        return ReflectionUtil.getRepository(classInstance, table);
    }

}
