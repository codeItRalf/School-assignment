package core.app.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import core.app.Core;
import core.app.entity.Division;
import core.app.entity.Team;

import java.util.stream.IntStream;


public class DivisionScreen extends BaseScreen<Division> {

    private Division division;

    public DivisionScreen(Division division, Core core ) {
        super(division, core);
        this.division = division;
    }


    @Override
    protected Table getBody() {
        return getDivisionContent(division);
    }


    @Override
    protected Table getFooter() {
        Table table = new Table();
        TextButton textButton = new TextButton("Create Team", skin);
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
