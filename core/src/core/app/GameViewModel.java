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

import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class GameViewModel extends ViewModel{

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
         Division division = (Division) getRepository(this,Division.class).get(id);
         setChildrenToParent(division, this);
         return division;
    }

    public Team getTeam(int id) {
        Team team = (Team) getRepository(this,Team.class).get(id);
        setChildrenToParent(team, this);
        return team;
    }

    public Fighter getFighter(int id) {
        Fighter fighter = (Fighter) getRepository(this,Fighter.class).get(id);
        return fighter;
    }

    public List<Division> getAllDivisions() {
        List<Division> allDivisions = getRepository(this,Division.class).getAll();
        allDivisions.forEach(e -> setChildrenToParent(e,this));
        return allDivisions;
    }

    public List<Team> getAllTeams() {
        List<Team> allTeams = getRepository(this,Team.class).getAll();
        allTeams.forEach(e-> setChildrenToParent(e,this));
        return allTeams;
    }

    public List<Fighter> getAllFighters() {
        return (List<Fighter>) getRepository(this,Fighter.class).getAll();
    }


    public Team getTeamForFighter(Fighter fighter) {
        return (Team) getRepository(this,Team.class).get(fighter.getTeamId());
    }

    public Division getDivisionForTeam(Team team) {
        return (Division) getRepository(this,Division.class).get(team.getDivisionId());
    }

    public Division getDivisionForFighter(Fighter fighter) {
        return getDivisionForTeam(getTeamForFighter(fighter));
    }

    public void insertDivision(Division division) {
        getRepository(this, division.getClass()).insert(division);
    }

    public void insertTeam(Team team) {
        getRepository(this, team.getClass()).insert(team);
    }

    public void insertFighter(Fighter fighter) {
        getRepository(this, fighter.getClass()).insert(fighter);
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
