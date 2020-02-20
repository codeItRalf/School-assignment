package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.app.Core;
import core.app.entity.Fighter;
import core.app.entity.Team;


public class AddFighterDialog extends BaseDialog<Team> {


    public AddFighterDialog(Skin skin, Stage stage, Core core, Team team) {
        super("Create new Fighter", skin, stage, core, team);
        posButton = "Create";
    }


    @Override
    protected void actionRequest() {
        Fighter fighter = new Fighter(inputText, -1, t.getId());
        gameViewModel.insertFighter(fighter);
        core.showTeamScreen(t);

    }
}
