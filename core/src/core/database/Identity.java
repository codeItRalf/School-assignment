package core.database;

import core.annotation.Searchable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

abstract public class Identity implements Serializable {

    private static final long serialVersionUID = 3333L;

    private int id;

    @Searchable
    private String name;

    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);


    public Identity() {
        this.name = "undefined";
        id = -1;
    }

    public Identity(String name, int id) {
        this.id = id;
        this.name = name;

    }

    public Identity(String name) {
        this.name = name;
        this.id = -1;
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

    public void setName(String value) {
        String oldValue = this.name;
        this.name = value;
        support.firePropertyChange("name",oldValue,this.name);

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Identity identity = (Identity) o;

        if (getId() != identity.getId()) return false;
        return getName().equals(identity.getName());
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getName().hashCode();
        return result;
    }
}
