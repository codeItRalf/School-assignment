package core.app.menuScreen;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.app.Core;
import core.app.dialog.ChangeValueDialog;
import core.app.dialog.MoveToDivisionDialog;
import core.app.dialog.MoveToTeamDialog;
import core.app.entity.Fighter;


public class FighterScreen extends BaseScreen<Fighter> {

    private final Fighter fighter;

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
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
             new ChangeValueDialog<>(uiSkin,stage,core,fighter, Fighter.attribute.DMG).createDialog();
            }
        });
        table.add(label);
        table.row();
        label = new Label("Hp: " + fighter.getHp(), skin);
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new ChangeValueDialog<>(uiSkin, stage, core, fighter, Fighter.attribute.HP).createDialog();
            }
        });
        table.add(label);
        table.row();
        label = new Label("Team: " + core.getViewModel().getTeamForFighter(fighter).getName(), skin);
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new MoveToTeamDialog<>(uiSkin, stage, core, t).createDialog();
            }
        });
        table.add(label);
        table.row();
        label = new Label("Division: " + core.getViewModel().getDivisionForFighter(fighter).getName(), skin);
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new MoveToDivisionDialog<>(uiSkin, stage, core, t).createDialog();
            }
        });
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

    @Override
    public void update() {

    }
}
