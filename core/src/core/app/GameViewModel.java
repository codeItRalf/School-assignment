package core.app;


import core.annotation.Table;
import core.app.Repositary.DivisionRepository;
import core.app.Repositary.FighterRepository;
import core.app.Repositary.TeamRepository;
import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Identity;
import core.app.entity.Team;
import core.fsdb.FileSystem;
import core.fsdb.MyDatabase;
import core.fsdb.ViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class GameViewModel extends ViewModel {

    @Table(entity = Division.class)
    private  DivisionRepository<Division> divRepo;

    @Table(entity = Team.class)
    private  TeamRepository<Team> teamRep;

    @Table(entity = Fighter.class)
    private  FighterRepository<Fighter> fighterRepo;

    private int roundCount = -1;

    public GameViewModel() {
       divRepo = new DivisionRepository<>(Division.class.getSimpleName());
       teamRep = new TeamRepository<>(Team.class.getSimpleName());
       fighterRepo = new FighterRepository<>(Fighter.class.getSimpleName());
    }


    public Division getDivision(int id) {
        return  getRepository(this,Division.class).get(id);
    }

    public ArrayList<Division> getAllDivisions() {
        return divisions;
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

    public void deleteEntity(Identity entity) {
        if (entity.getClass().equals(Fighter.class)) {
            Team team = getTeamForFighter((Fighter) entity);
            team.getFighters().remove(entity);
        } else if (entity.getClass().equals(Team.class)) {
            Division division = getDivisionForTeam((Team) entity);
            division.getTeams().remove(entity);
        } else divisions.remove(entity);
        repository.remove(entity);
    }

    public int getActualRoundCount() {
        if (roundCount == -1) {
            roundCount = getRoundCount();
        }
        return roundCount;
    }

    public void incrementRoundCount() {
        roundCount++;
        updateRoundCount(roundCount);
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

    public int getRoundCount() {
        String path = MyDatabase.class.getSimpleName() + "/fightCount";
        if (!FileSystem.exists(path)) {
            FileSystem.writeFile(path, "1");
        }
        return Integer.parseInt(Objects.requireNonNull(FileSystem.readFile(path)));
    }

    public void updateRoundCount(int fightCount) {
        String path = MyDatabase.class.getSimpleName() + "/fightCount";
        FileSystem.writeFile(path, String.valueOf(fightCount));
    }

}
