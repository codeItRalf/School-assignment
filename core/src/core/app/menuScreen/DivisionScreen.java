package core.app.menuScreen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import core.app.Core;
import core.app.dialog.AddTeamDialog;
import core.app.entity.Division;


public class DivisionScreen extends BaseScreen<Division> {

    private final Division division;

    public DivisionScreen(Division division, Core core ) {
        super(division, core);
        this.division = division;
    }


    @Override
    protected Table getBody() {
        Table table = new Table();
        table.add(setNameLabel());
        table.row();
        table.add(getDivisionContent(division));
        return table;
    }


    @Override
    protected Table getFooter() {
        Table table = new Table();
        TextButton textButton = new TextButton("Create Team", skin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new AddTeamDialog(uiSkin,stage,core, division).createDialog();
            }
        });
        table.add(textButton);
        textButton = new TextButton("Back", skin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                core.showStartScreen();
            }
        });
        table.add(textButton);
        return table;
    }

}
