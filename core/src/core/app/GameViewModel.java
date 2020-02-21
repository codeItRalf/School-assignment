package core.app;


import core.app.entity.Division;
import core.app.entity.Fighter;
import core.fsdb.Identity;
import core.app.entity.Team;
import core.fsdb.FileSystem;
import core.fsdb.MyDatabase;
import core.fsdb.ViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class GameViewModel  extends ViewModel {


    private int roundCount = -1;

    public GameViewModel() {
      super(Division.class);
    }


    public Division getDivision(int id) {
      return (Division) get(id,Division.class);
    }

    public ArrayList<Division> getAllDivisions() {
        return (ArrayList<Division>) getAll(Division.class);
    }

    public ArrayList<Team> getAllTeams() {
        return  getAll(Team.class);
    }

    public ArrayList<Fighter> getAllFighters() {
        return getAll(Fighter.class);
    }


    public Team getTeamForFighter(Fighter fighter) {
        return (Team) getParent(fighter);

    }

    public Division getDivisionForTeam(Team team) {
        return (Division) getParent(team);
    }

    public Division getDivisionForFighter(Fighter fighter) {
        return (Division) getParent(getParent(fighter));
    }

    public void insertDivision(Division division) {
      insertEntity(division);
    }

    public void insertTeam(Team team) {
       insertEntity(team);
    }

    public void insertFighter(Fighter fighter) {
     insertEntity(fighter);
    }

    public void deleteEntity(Identity entity) {
     deleteEntity(entity);
    }

    public int getActualRoundCount() {
        if(roundCount == -1){
            String path = MyDatabase.class.getSimpleName() + "/fightCount";
            if (!FileSystem.exists(path)) {
                FileSystem.writeFile(path, "1");
            }
            roundCount = Integer.parseInt(FileSystem.readFile(path));
        }
        return roundCount;
    }

    public void incrementRoundCount() {
        String path = MyDatabase.class.getSimpleName() + "/fightCount";
        roundCount++;
        FileSystem.writeFile(path, String.valueOf(roundCount));
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
