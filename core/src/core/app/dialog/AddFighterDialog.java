package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.app.Core;
import core.app.entity.Fighter;
import core.app.entity.Team;

import java.util.ArrayList;


public class AddFighterDialog extends BaseDialog {

    private Team team;

    public AddFighterDialog(Skin skin, Stage stage, Core core, Team team) {
        super("Create new Fighter", skin, stage, core);
        this.team = team;
    }


    @Override
    protected void actionRequest() {
        Fighter fighter = new Fighter(inputText,-1, team.getId());
        viewModel.insertFighter(fighter);
        core.showTeamScreen(team);
    }
}
