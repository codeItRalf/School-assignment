package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.app.Core;
import core.app.entity.Division;

import java.util.ArrayList;


public class AddDivisionDialog extends BaseDialog {


    public AddDivisionDialog(Skin skin, Stage stage, Core core) {
        super("Create new Division", skin, stage, core);
    }


    @Override
    protected void actionRequest() {
        viewModel.insertDivision(new Division(inputText,-1,new ArrayList<>()));
        core.showStartScreen();
    }
}
