package core.fsdb;


import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public abstract class ViewModel<T extends Identity> {

    private  Repository<? extends Identity> repository;
    private  List<? extends  Identity> entities;
    private  MyObserver<? extends Identity> myObserver;

    public ViewModel(Class<?> SuperParentClass) {
        repository = new Repository<>();
        entities = new ArrayList<>(repository.getAllOf(SuperParentClass.getSimpleName()));
        myObserver = new MyObserver<T>(entities, repository);
    }


    public T get(int id, Class<?> entityClass) {
       if(ReflectionUtil.getParentClassName(entityClass) == null && entities.get(id).getClass().equals(entityClass)){
            return (T) entities.get(id);
       }else {
           return (T) ReflectionUtil.findNextEntity(entities,entityClass,id);
       }
    }

    public ArrayList<Division> getAllDivisions() {
        return entities;
    }

    public ArrayList<Team> getAllTeams() {
        return getAllDivisions()
                .stream()
                .parallel()
                .map(Division::getTeams)
                .flatMap(List::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Fighter> getAllFighters() {
        return getAllTeams()
                .stream()
                .map(Team::getFighters)
                .flatMap(List::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public Team getTeamForFighter(Fighter fighter) {
        return entities.stream().parallel()
                .map(Division::getTeams)
                .flatMap(List::stream)
                .filter(e -> e.getId() == fighter.getTeamId())
                .findAny()
                .get();

    }

    public Division getDivisionForTeam(Team team) {
        return entities.stream()
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
        entities.add(division);
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

    public void deleteEntity(Identity entity) {
        if (entity.getClass().equals(Fighter.class)) {
            Team team = getTeamForFighter((Fighter) entity);
            team.getFighters().remove(entity);
        } else if (entity.getClass().equals(Team.class)) {
            Division division = getDivisionForTeam((Team) entity);
            division.getTeams().remove(entity);
        } else entities.remove(entity);
        repository.remove(entity);
    }



    public Team getTheBestTeamInDiv(Division division) {
        return division.getTeams()
                .stream()
                .max(Comparator.comparing(Team::getWins))
                .get();
    }

    public Team getTheWorstTeamInDiv(Division division) {
        return division.getTeams()
                .stream()
                .min(Comparator.comparing(Team::getWins))
                .get();
    }


}
