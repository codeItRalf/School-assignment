package core.app.entity;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

abstract public class Identity implements Serializable {

    private static final long serialVersionUID = 3333L;

    private int id;
    private String name;

    protected PropertyChangeSupport support = new PropertyChangeSupport(this);


    public Identity() {
        id = -1;
    }

    public Identity(String name, int id) {
        this.id = id;
        this.name = name;

    }

    public void addPropertyChangeListener(PropertyChangeListener pcl){
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl){
        support.removePropertyChangeListener(pcl);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        support.firePropertyChange("name",this.name,name);
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if(this.id == -1){
            this.id = id;
        }else{
            System.out.println("Id can't be changed.");
        }

    }

    public String getNameColored(){
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Identity identity = (Identity) o;

        return getId() == identity.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
