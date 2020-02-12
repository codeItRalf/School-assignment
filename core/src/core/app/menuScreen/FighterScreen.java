package core.app.menuScreen;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import core.app.Core;
import core.app.entity.Fighter;


public class FighterScreen extends BaseScreen<Fighter> {

    private Fighter fighter;

    public FighterScreen(Fighter fighter, Core core ) {
        super(fighter, core);
        this.fighter = fighter;
    }

    @Override
    protected Table getBody() {

        Table table = new Table();
        table.add(setNameLabel());
        table.row();
        Label label = new Label("Damage: " + fighter.getDmg(), skin);
        table.add(label);
        table.row();
        label = new Label("Hp: " + fighter.getHp(), skin);
        table.add(label);
        table.row();
        label = new Label("Team: " + core.getViewModel().getTeamForFighter(fighter).getName(), skin);
        table.add(label);
        table.row();
        label = new Label("Division: " + core.getViewModel().getDivisionForFighter(fighter).getName(), skin);
        table.add(label);
        table.row();
        return table;
    }

    @Override
    protected Table getFooter() {
        Table table = new Table();
        TextButton textButton = new TextButton("Back", skin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                core.showTeamScreen(core.getViewModel().getTeamForFighter(fighter));
            }
        });
        table.add(textButton);
        return table;
    }

}
