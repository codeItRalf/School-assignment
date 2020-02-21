package core.fsdb;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MyObserver<T extends Identity> implements PropertyChangeListener {

    final List<? extends Identity> listOfParentEntities;
    final Repository<Identity> repository;
    private HashMap<String, Identity> queueList = new HashMap<>();

    private boolean isWriting = false;

    public MyObserver(List<? extends Identity> listOfParentEntities, Repository<Identity> repository) {
        this.listOfParentEntities = listOfParentEntities;
        this.repository = repository;
        addListeners(listOfParentEntities);
    }

    private void addListeners(List<? extends Identity> entities) {
        entities.forEach(entity -> {
            entity.addPropertyChangeListener(this);
            System.out.println("entity = " + entity.getClass().getSimpleName());
            if(ReflectionUtil.childrenExist(entity.getClass())){
                addListeners(Objects.requireNonNull(ReflectionUtil.getChildrenFromParent(entity)));
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        var queueItem =  (Identity) evt.getSource();

        queueList.put(queueItem.getClass().getSimpleName() + queueItem.getId(), queueItem);
//        repository.update((Identity) evt.getSource());
        if(queueList.size() > 0 && !isWriting){
            isWriting = true;
            Thread serializeQueueFiles = new Thread(this::writeToFile);
            serializeQueueFiles.start();
        }
    }

    private void writeToFile(){
        while (queueList.size() > 0){
            System.out.println("queuedEntities.size()= " + queueList.size());
            queueList.forEach((k,v)-> {
                repository.update( queueList.remove(k));
            });

        }
        isWriting = false;
    }
}
