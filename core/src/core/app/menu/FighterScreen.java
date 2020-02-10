package core.app.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import core.app.Core;
import core.app.entity.Fighter;


public class FighterScreen extends BaseScreen<Fighter> {

    private Fighter fighter = this.t;

    public FighterScreen(Fighter fighter, Core core ) {
        super(fighter, core);
    }

    @Override
    protected Table getFooter() {
        return null;
    }


    @Override
    protected Table getTable() {

            Table table = new Table();
            Label label = new Label( t.getName(), skin,"bg");
            label.setTouchable(Touchable.disabled);
            label.setAlignment(Align.center);
            table.add(label);
            table.row();
            label = new Label("Damage: " + fighter.getDmg(), skin);
            table.add(label);
            table.row();
       TextField textField = new TextField("banan",new TextField.TextFieldStyle(bitmapFont,
               Color.WHITE,
               null,
               null,
               null));
            table.add(textField);
            table.row();
            label = new Label("Hp: " + fighter.getHp(), skin);
            table.add(label);
            table.row();


        TextButton textButton = new TextButton("Back", skin);
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
