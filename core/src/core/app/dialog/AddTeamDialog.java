package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import core.app.Core;
import core.app.entity.Division;
import core.app.entity.Team;

import java.util.ArrayList;


public class AddTeamDialog extends BaseDialog {

    private int divisionId;

    public AddTeamDialog(Skin skin, Stage stage, Core core, int divisionId) {
        super("Create new Team", skin, stage, core);
        this.divisionId = divisionId;
    }


    @Override
    protected void createEntity() {
        Team team = new Team(inputText,-1, divisionId, new ArrayList<>());
        viewModel.insertTeam(team);
        core.showDivisionScreen(team);
    }
}
