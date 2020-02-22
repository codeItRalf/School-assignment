package core.fsdb;


import core.annotation.Entity;
import core.annotation.ForeignKey;
import core.app.entity.Identity;
import core.app.entity.NoClass;

import java.util.List;
import java.util.stream.IntStream;


public abstract class  ViewModel<E extends Identity> {

    public ViewModel() {
        new MyDatabase();
    }


    //    <E extends Identity> E addChildrenToParent(E entity) {
//        List<E> listOfChildren = getChildrenToParent(entity);
//        String fieldName = ReflectionUtil.getNameOfChildrenList(entity);
//        return ReflectionUtil.updateField(fieldName, listOfChildren, entity);
//    }




    public    void setChildrenToParent(E entity, ViewModel<?> viewModel) {
        var child =  getChildClass(entity);
        if(child != null && !child.equals(NoClass.class)){
            String parentID = getParentIdVariableName(child);
           List<E> listOfChildren =  getRepository(viewModel,child).getWithIntFieldEqual(parentID,entity.getId());
           listOfChildren.forEach(e -> setChildrenToParent(e,viewModel));
           ReflectionUtil.setField(ReflectionUtil.getNameOfChildrenList(entity),listOfChildren,entity);
        }
    }

    private String getParentIdVariableName(Class<?> clazz){
        return clazz.getAnnotation(Entity.class).foreignKey()[0].parentId();
    }

    private   boolean ifParentIdMatchesChild(E parent, E child) {
        String parentId = getParentIdVariableName(child.getClass());
        if (parentId.length() < 1) {
            return false;
        }
        int fieldValue = -1;
        fieldValue = (int) ReflectionUtil.getField(child,parentId);
        return fieldValue == parent.getId();
    }

    protected boolean deepRemove(E entity) {
        return entity.getClass().getAnnotation(Entity.class).foreignKey()[0].onDelete() == ForeignKey.CASCADE;
    }

    protected   void removeChildren(E entity) {
        List<E> childList = ReflectionUtil.getChildrenFromParent(entity);
        IntStream.range(0, childList != null ? childList.size() : 0).forEach(index -> {
                getRepository(this, entity.getClass()).removeFile(childList.get(index));
            if (index != childList.size() - 1 && ReflectionUtil.childrenExist(childList.get(index))) {
                removeChildren(childList.get(index));
            }
        });
    }

     protected    Class<?> getChildClass(E entity) {

        return entity.getClass().getAnnotation(Entity.class).foreignKey()[0].child();
    }


    protected <T extends RepositoryInterface<E>> T getRepository(ViewModel<?> viewModel, Class<?>  table){
        return ReflectionUtil.getRepository(viewModel, table);
    }

}
