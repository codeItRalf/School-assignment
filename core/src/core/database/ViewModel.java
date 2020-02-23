package core.database;


import core.annotation.Entity;
import core.annotation.ForeignKey;
import core.app.entity.NoClass;

import java.util.List;
import java.util.stream.IntStream;


public abstract class  ViewModel {

    public ViewModel() {
        new MyDatabase();
    }




    public  <E extends Identity>  void setChildrenToParent(E entity, ViewModel viewModel) {
        var child =  getChildClass(entity);
        if(child != null && !child.equals(NoClass.class)){
            String parentIDVariableName = getParentIdVariableName(child);
           List<E> listOfChildren = (List<E>) getRepository(viewModel,child).getWithIntFieldEqual(parentIDVariableName,entity.getId());
           listOfChildren.forEach(e -> setChildrenToParent(e,viewModel));
           ReflectionUtil.setField(ReflectionUtil.getNameOfChildrenList(entity),listOfChildren,entity);
        }
    }

    private <E extends Identity> String getParentIdVariableName(Class<?> clazz){
        return clazz.getAnnotation(Entity.class).foreignKey()[0].parentId();
    }

    private <E extends Identity>  boolean ifParentIdMatchesChild(E parent, E child) {
        String parentId = getParentIdVariableName(child.getClass());
        if (parentId.length() < 1) {
            return false;
        }
        int fieldValue = -1;
        fieldValue = (int) ReflectionUtil.getField(child,parentId);
        return fieldValue == parent.getId();
    }

    protected <E extends Identity> boolean deepRemove(E entity) {
        return entity.getClass().getAnnotation(Entity.class).foreignKey()[0].onDelete() == ForeignKey.CASCADE;
    }

    protected <E extends Identity> void removeChildren(E entity) {
        List<E> childList = ReflectionUtil.getChildrenFromParent(entity);
        IntStream.range(0, childList != null ? childList.size() : 0).forEach(index -> {
                getRepository(this, entity.getClass()).removeFile( childList.get(index));
            if (index != childList.size() - 1 && ReflectionUtil.childrenExist(childList.get(index).getClass())) {
                removeChildren(childList.get(index));
            }
        });
    }

     protected  <E extends Identity>  Class<?> getChildClass(E entity) {

        return entity.getClass().getAnnotation(Entity.class).foreignKey()[0].child();
    }


    protected <T extends RepositoryInterface<Identity>> T getRepository(ViewModel viewModel, Class<?>  table){
        return ReflectionUtil.getRepository(viewModel, table);
    }

}
