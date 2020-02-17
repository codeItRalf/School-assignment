package core.fsdb;


import core.app.entity.Division;
import core.app.entity.Identity;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class MyObserver<T extends Identity> implements PropertyChangeListener {

    final List<? extends Identity> listOfParentEntities;
    final Repository<? extends Identity> repository;



    public MyObserver(List<Division> listOfParentEntities, Repository<? extends Identity> repository) {
        this.listOfParentEntities = listOfParentEntities;
        this.repository = repository;
        addListeners(listOfParentEntities);
    }

    private void addListeners(List<? extends Identity> entities) {
        entities.forEach(entity -> {
            entity.addPropertyChangeListener(this);
            System.out.println("entity = " + entity.getClass().getSimpleName());
            if(ReflectionUtil.childrenExist(entity)){
                addListeners(ReflectionUtil.deserializeChildrenToList(entity));
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("Property changed!");
        repository.update((Identity) evt.getSource());
    }
}
