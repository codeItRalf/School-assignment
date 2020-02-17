package core.fsdb;


import core.app.entity.Division;
import core.app.entity.Identity;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class MyObserver implements PropertyChangeListener {

    final ArrayList<Division> divisions;
    final Repository<? extends Identity> repository;
    private ArrayList<? extends Identity> writeQueue = new ArrayList<>();


    public MyObserver(ArrayList<Division> divisions, Repository<? extends Identity> repository) {
        this.divisions = divisions;
        this.repository = repository;
        addListeners();
    }

    private void addListeners() {
        divisions.forEach(division -> {
            division.addPropertyChangeListener(this);
            division.getTeams().forEach(team -> {
                team.addPropertyChangeListener(this);
                team.getFighters().forEach(fighter -> fighter.addPropertyChangeListener(this));
            });
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        repository.update((Identity) evt.getSource());
    }
}
