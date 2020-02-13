package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import core.app.Core;
import core.app.entity.Division;
import core.app.entity.Team;

import java.util.ArrayList;


public class AddTeamDialog extends BaseDialog<Division> {


    public AddTeamDialog(Skin skin, Stage stage, Core core, Division division) {
        super("Create new Team", skin, stage, core, division);

    }


    @Override
    protected void actionRequest() {
        Team team = new Team(inputText, -1, t.getId(), new ArrayList<>());
        viewModel.insertTeam(team);
        core.showDivisionScreen(t);
        posButton = "Create";
    }
}
