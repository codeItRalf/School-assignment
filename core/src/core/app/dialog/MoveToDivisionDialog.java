package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.app.Core;
import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Identity;
import core.app.entity.Team;
import core.fsdb.ViewModel;

import java.util.ArrayList;


public class MoveToDivisionDialog<T extends Identity> extends BaseDialog<T> {

    protected int parentIndex;
    protected Division divDestination;
    protected Team teamDestination;


    public MoveToDivisionDialog(Skin skin, Stage stage, Core core, T t) {
        super("Move to!", skin, stage, core, t);
        if (t.getClass().equals(Team.class)) parentIndex = ((Team) t).getDivisionId();
        else if (t.getClass().equals(Fighter.class)) parentIndex = ((Fighter) t).getTeamId();
    }

    @Override
    public void createDialog() {
        getContentTable().row();
        getContentTable().add(getListOfEntities((ArrayList<T>) viewModel.getAllDivisions(), parentIndex)).space(10f).pad(10f);
        button("Cancel");
        show(stage);
    }

    @Override
    protected void result(Object object) {
        if (object != null && object.getClass().equals(Division.class)) {
            divDestination = (Division) object;
            actionRequest();
        }
    }

    @Override
    protected void actionRequest() {
        moveToDivision();
    }

    private void moveToDivision() {
        Division currentDivision;
        int currentIndex;
        if (t.getClass().equals(Team.class)) {
            currentDivision = viewModel.getDivisionForTeam((Team) t);
            currentIndex = currentDivision.getTeams().indexOf(t);
            divDestination.getTeams().add(currentDivision.getTeams().remove(currentIndex));
            ((Team) t).setDivisionId(divDestination.getId());
            core.showTeamScreen((Team) t);
        }

    }
}
