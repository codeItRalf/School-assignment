package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.app.Core;
import core.app.entity.Fighter;

import java.util.ArrayList;


public class AddFighterDialog extends BaseDialog {

    private int teamId;

    public AddFighterDialog(Skin skin, Stage stage, Core core, int teamId) {
        super("Create new Fighter", skin, stage, core);
        this.teamId = teamId;
    }


    @Override
    protected void createEntity() {
        Fighter fighter = new Fighter(inputText,-1, teamId);
        viewModel.insertFighter(fighter);
        core.showTeamScreen(fighter);
    }
}
