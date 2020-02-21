package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.app.Core;
import core.app.entity.Division;
import core.app.entity.Fighter;
import core.fsdb.Identity;
import core.app.entity.Team;

import java.util.ArrayList;


public class MoveToTeamDialog<T extends Identity> extends BaseDialog<T> {


    protected final Division divisionDestination;
    protected Team teamDestination;
    protected int parentIndex;


    public MoveToTeamDialog(Skin skin, Stage stage, Core core, T t) {
        super("Move to!", skin, stage, core, t);
        if (t.getClass().equals(Fighter.class)) parentIndex = ((Fighter) t).getTeamId();
        this.divisionDestination = gameViewModel.getDivisionForFighter((Fighter) t);
    }

    public MoveToTeamDialog(Skin skin, Stage stage, Core core, T t, Division divisionDestination) {
        super("Move to!", skin, stage, core, t);
        parentIndex = -1;
        this.divisionDestination = divisionDestination;
    }


    @Override
    public void createDialog() {
        getContentTable().row();
        getContentTable().add(getListOfEntities((ArrayList<? extends Identity>) divisionDestination.getTeams(), parentIndex)).space(10f).pad(10f);
        button("Cancel");
        show(stage);
    }

    @Override
    protected void result(Object object) {
        if (object != null && object.getClass().equals(Team.class)) {
            teamDestination = (Team) object;
            actionRequest();
        }
    }

    @Override
    protected void actionRequest() {
        moveToTeam();
    }


    private void moveToTeam() {
        Team currentTeam;
        int currentIndex;
            currentTeam = gameViewModel.getTeamForFighter((Fighter) t);
            currentIndex = currentTeam.getFighters().indexOf(t);
            teamDestination.getFighters().add(currentTeam.getFighters().remove(currentIndex));
            ((Fighter) t).setTeamId(teamDestination.getId());
            core.showFighterScreen((Fighter) t);


    }
}
