package core.fsdb;


import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Identity;
import core.app.entity.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static core.fsdb.FileSystem.generateId;

public class ViewModel {

        private Repository<? extends Identity> repository;
        private ArrayList<Division> divisions;
        private MyObserver myObserver;

    public ViewModel() {
     repository = new Repository<>();
     divisions = (ArrayList<Division>) repository.getAllOf(Division.class.getSimpleName());
     myObserver = new MyObserver(divisions);
    }


    public Division getDivision(int id){
        return divisions.get(id);
    }

    public ArrayList<Division> getAllDivisions(){
        return divisions;
    }

    public Team getTeamForFighter(Fighter fighter){
       return divisions.stream().map(Division::getTeams)
              .flatMap(List::stream)
               .parallel()
          .filter(e-> e.getId() == fighter.getTeamId())
        .collect(Collectors.toList()).get(0);

    }

    public Division getDivisionForTeam(Team team){
        return   divisions.stream()
                .parallel()
                .filter(div -> div.getId() == team.getDivisionId())
                .collect(Collectors.toList()).get(0);
    }

    public Division getDivisionForFighter(Fighter fighter){
        return getDivisionForTeam(getTeamForFighter(fighter));
    }

    public void insertDivision(Division division){
        division.setId(generateId(MyDatabase.class.getSimpleName() + "/" + division.getClass().getSimpleName()));
        division.addPropertyChangeListener(myObserver);
        divisions.add(division);
        repository.insert(division);
    }

    public void insertTeam(Team team){
        team.setId(generateId(MyDatabase.class.getSimpleName() + "/" + team.getClass().getSimpleName()));
        team.addPropertyChangeListener(myObserver);
       getDivisionForTeam(team).getTeams().add(team);
       repository.insert(team);
    }

    public void insertFighter(Fighter fighter){
        int divIndex = getDivisionForFighter(fighter).getId();
        fighter.setId(generateId(MyDatabase.class.getSimpleName() + "/" + fighter.getClass().getSimpleName()));
        fighter.addPropertyChangeListener(myObserver);
        getTeamForFighter(fighter).getFighters().add(fighter);
        repository.insert(fighter);
    }
}
