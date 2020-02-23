package core.app.viewModel;

import core.app.entity.Team;
import core.database.Identity;
import core.database.ReflectionUtil;
import core.database.Repository;

import java.util.Comparator;


class TeamRepository extends Repository<Team> {


     TeamRepository(String entityName) {
        super(entityName);
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
}
