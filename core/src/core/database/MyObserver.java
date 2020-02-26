package core.database;



import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class MyObserver<T extends Identity> implements PropertyChangeListener {

    final List<T> entities;
    final Repository<T> repository;
    private ConcurrentHashMap<Integer,T> queuedEntities = new ConcurrentHashMap<>();

    private boolean isWriting = false;

    public MyObserver(List<T> entities, Repository<T> repository) {
        this.entities = entities;
        this.repository = repository;
        addListeners(entities);
    }

    private void addListeners(List<? extends Identity> entities) {
        entities.forEach(entity -> {
            entity.addPropertyChangeListener(this);
            System.out.println("entity = " + entity.getClass().getSimpleName());

        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        T updatedEntity = (T) evt.getSource();
        queuedEntities.put(updatedEntity.getId(),updatedEntity);
        if(queuedEntities.size() > 0 && !isWriting){
            isWriting = true;
            Thread serializeQueueFiles = new Thread(this::writeToFile);
            serializeQueueFiles.start();
        }
    }

    private void writeToFile(){
        while (queuedEntities.size() > 0){
            queuedEntities.forEach((k,v)-> repository.update(queuedEntities.remove(k)));

        }
        isWriting = false;
        System.out.println("Queue work completed! Size: " + queuedEntities.size());
    }
}
