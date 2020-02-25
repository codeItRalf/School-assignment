package core.app.appViewModel;

import core.app.entity.Team;
import core.database.ReflectionUtil;
import core.database.Repository;

import java.util.Comparator;


public class TeamRepository extends Repository<Team> {


    public TeamRepository(String pathString) {
        super(pathString);
    }


    Team getBestTeamFromDiv(int divisionID){
      return  getWithIntFieldEqual(ReflectionUtil.getParentIdVariableName(Team.class),divisionID)
              .stream()
              .max(Comparator.comparing((Team::getWins)))
              .get();
    }

    Team getWorstTeamFromDiv(int divisionID){
        return  getWithIntFieldEqual(ReflectionUtil.getParentIdVariableName(Team.class),divisionID)
                .stream()
                .min(Comparator.comparing((Team::getWins)))
                .get();
    }

    boolean teamsHasChangedDivision(){
      return getAll().parallelStream()
              .anyMatch(e-> e.getDivStatus() != Team.DivStatus.UNCHANGED);
    }
}
