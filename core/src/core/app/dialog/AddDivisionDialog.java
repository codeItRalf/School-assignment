package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.app.Core;
import core.app.entity.Division;
import core.database.Identity;

import java.util.ArrayList;


public class AddDivisionDialog<T extends Identity> extends BaseDialog<T> {


    public AddDivisionDialog(Skin skin, Stage stage, Core core, T t) {
        super("Create new Division", skin, stage, core, t);
        posButton = "Create";
    }


    @Override
    protected void actionRequest() {
        gameViewModel.insertDivision(new Division(inputText, -1, new ArrayList<>()));
        core.showStartScreen();

    }
}
