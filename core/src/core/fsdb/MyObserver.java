package core.fsdb;


import core.app.entity.Division;
import core.app.entity.Identity;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyObserver<T extends Identity> implements PropertyChangeListener {

    final List<? extends Identity> listOfParentEntities;
    final Repository<? extends Identity> repository;
    private ConcurrentLinkedQueue<Object> queuedEntities = new ConcurrentLinkedQueue<>();

    private boolean isWriting = false;

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
                addListeners(Objects.requireNonNull(ReflectionUtil.getChildrenFromParent(entity)));
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      queuedEntities.add(evt.getSource());
//        repository.update((Identity) evt.getSource());
        if(queuedEntities.size() > 0 && !isWriting){
            isWriting = true;
            Thread serializeQueueFiles = new Thread(this::writeToFile);
            serializeQueueFiles.start();
        }
    }

    private void writeToFile(){
        while (queuedEntities.size() > 0){
         //   repository.update((Identity) queuedEntities.remove(0));
        }
        isWriting = false;
    }
}
