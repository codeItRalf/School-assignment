package core.fsdb;


import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Identity;
import core.app.entity.Team;

import java.util.ArrayList;
import java.util.List;

import static core.fsdb.FileSystem.generateId;

public class ViewModel {

    private Repository<? extends Identity> repository;
    private ArrayList<Division> divisions;
    private MyObserver myObserver;

    public ViewModel() {
        repository = new Repository<>();
        divisions = new ArrayList<>(repository.getAllOf(Division.class.getSimpleName()));
        myObserver = new MyObserver(divisions, repository);
    }


    public Division getDivision(int id) {
        return divisions.get(id);
    }

    public ArrayList<Division> getAllDivisions() {
        return divisions;
    }


    public Team getTeamForFighter(Fighter fighter) {
        return divisions.stream().parallel()
                 .map(Division::getTeams)
                .flatMap(List::stream)
                .filter(e -> e.getId() == fighter.getTeamId())
                .findAny()
                .get();

    }

    public Division getDivisionForTeam(Team team) {
        return divisions.stream()
                .parallel()
                .filter(div -> div.getId() == team.getDivisionId())
                .findAny()
                .get();
    }

    public Division getDivisionForFighter(Fighter fighter) {
        return getDivisionForTeam(getTeamForFighter(fighter));
    }

    public void insertDivision(Division division) {
        repository.setNewId(division);
        division.addPropertyChangeListener(myObserver);
        divisions.add(division);
        repository.insert(division);
    }

    public void insertTeam(Team team) {
        repository.setNewId(team);
        team.addPropertyChangeListener(myObserver);
        getDivisionForTeam(team).getTeams().add(team);
        repository.insert(team);
    }

    public void insertFighter(Fighter fighter) {
        repository.setNewId(fighter);
        fighter.addPropertyChangeListener(myObserver);
        getTeamForFighter(fighter).getFighters().add(fighter);
        repository.insert(fighter);
    }
}
