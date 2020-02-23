package core.app.viewModel;


import core.annotation.Table;
import core.app.entity.Division;
import core.app.entity.Fighter;
import core.database.Identity;
import core.app.entity.Team;
import core.database.FileSystem;
import core.database.MyDatabase;
import core.database.ViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class GameViewModel extends ViewModel{

    @Table(entity = Division.class)
    private  DivisionRepository divRepo;

    @Table(entity = Team.class)
    private  TeamRepository teamRep;

    @Table(entity = Fighter.class)
    private  FighterRepository fighterRepo;

    private int roundCount = -1;

    public GameViewModel() {
        divRepo = new DivisionRepository(Division.class.getSimpleName());
       teamRep = new TeamRepository(Team.class.getSimpleName());
       fighterRepo = new FighterRepository(Fighter.class.getSimpleName());
    }


    public Division getDivision(int id) {
         Division division = divRepo.get(id);
         setChildrenToParent(division, this);
         return division;
    }

    public Team getTeam(int id) {
        Team team = teamRep.get(id);
        setChildrenToParent(team, this);
        return team;
    }

    public Fighter getFighter(int id) {
        Fighter fighter = fighterRepo.get(id);
        return fighter;
    }

    public List<Division> getAllDivisions() {
        List<Division> allDivisions =divRepo.getAll();
        allDivisions.forEach(e -> setChildrenToParent(e,this));
        return allDivisions;
    }

    public List<Team> getAllTeams() {
        List<Team> allTeams = teamRep.getAll();
        allTeams.forEach(e-> setChildrenToParent(e,this));
        return allTeams;
    }

    public List<Fighter> getAllFighters() {
        return (List<Fighter>) fighterRepo.getAll().stream().
                sorted(Comparator.comparing(Fighter::getName))
                .collect(Collectors
                        .toCollection(ArrayList::new));
    }


    public Team getTeamForFighter(Fighter fighter) {
        return (Team) teamRep.get(fighter.getTeamId());
    }

    public Division getDivisionForTeam(Team team) {
        return (Division)divRepo.get(team.getDivisionId());
    }

    public Division getDivisionForFighter(Fighter fighter) {
        return getDivisionForTeam(getTeamForFighter(fighter));
    }

    public void insertDivision(Division division) {
        divRepo.insert(division);
    }

    public void insertTeam(Team team) {
        teamRep.insert(team);
    }

    public void insertFighter(Fighter fighter) {
        fighterRepo.insert(fighter);
    }

    public void deleteEntity(Identity entity) {
        if(deepRemove(entity)){
            removeChildren(entity);
        }
        getRepository(this,entity.getClass()).remove(entity);
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

    public Team getTheBestTeamInDiv(int index) {
        return teamRep.getBestTeamFromDiv(index);
    }

    public Team getTheWorstTeamInDiv(int index) {
        return teamRep.getWorstTeamFromDiv(index);
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
