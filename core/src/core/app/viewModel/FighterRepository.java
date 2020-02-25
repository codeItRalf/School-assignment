package core.app.viewModel;

import core.app.entity.Fighter;
import core.database.Identity;
import core.database.ReflectionUtil;
import core.database.Repository;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class FighterRepository extends Repository<Fighter> {


    public   FighterRepository(String pathString) {
        super(pathString);
    }



    ArrayList<Fighter> fightersSortedByTeam(GameViewModel viewModel) {
        return getAll()
                .stream()
                .parallel()
                .sorted(Comparator.comparing(e-> viewModel.getTeamForFighter(e).getName()))
                .collect(Collectors.toCollection(ArrayList::new));

    }

    ArrayList<Fighter> fightersSortedByDamage() {
        return getAll()
                .stream()
                .parallel()
                .sorted(Comparator.comparing(Fighter::getDmg).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

    }

    ArrayList<Fighter> fightersSortedByName() {
        return getAll()
                .stream()
                .parallel()
                .sorted(Comparator.comparing(Fighter::getName))
                .collect(Collectors.toCollection(ArrayList::new));

    }


    ArrayList<Fighter> filterFighterByNameContains(String subString) {
         return (ArrayList<Fighter>) getWithStringFieldContains(ReflectionUtil.getSearchVariableName(Identity.class)[0],subString);

    }


}
