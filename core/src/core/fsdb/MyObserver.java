package core.fsdb;


import core.app.entity.Division;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class MyObserver implements PropertyChangeListener {

    ArrayList<Division> divisions;
    public MyObserver(ArrayList<Division> divisions) {
        this.divisions = divisions;
        addListeners();
    }

    private void addListeners() {
        divisions.forEach(division -> {
            division.addPropertyChangeListener(this);
            division.getTeams().forEach(team -> {
                team.addPropertyChangeListener(this);
                team.getFighters().forEach(fighter -> {
                    fighter.addPropertyChangeListener(this);
                });
            });
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("Listener fired! Bean= " + evt.getSource().getClass().getSimpleName());
    }
}
