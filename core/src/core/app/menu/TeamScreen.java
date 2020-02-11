package core.app.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import core.app.Core;
import core.app.entity.Team;

import java.util.stream.IntStream;


public class TeamScreen extends BaseScreen<Team> {


    public TeamScreen(Team team,  Core core ) {
        super(team,  core);
    }

    @Override
    protected Table getBody() {
        Table rootTable = new Table();
        Table table = new Table();
        Label label = new Label( t.getName(), skin,"bg");
        label.setTouchable(Touchable.disabled);
        label.setAlignment(Align.center);
        table.add(label);
        rootTable.add(table);
        rootTable.row();
        table = new Table();
        label = new Label("Name:" ,skin);
        table.add(label).width(CELL_WIDTH).align(Align.left);
        label = new Label("Damage", skin);
        table.add(label).expandX().align(Align.center);
        label = new Label("Hp", skin);
        table.add(label).expandX().align(Align.right);
        rootTable.add(table).growX().padLeft(CELL_PADDING *2).padRight(CELL_PADDING * 2);;
        rootTable.row();
        table = new Table();
        rootTable.add(table).growX();
        Table finalTable = table;
        IntStream.range(0, t.getFighters().size()).forEach(i ->{
            Table listItemTable = new Table();
            Label itemLabel = new Label(t.getFighters().get(i).getName() ,skin);
            listItemTable.add(itemLabel).width(CELL_WIDTH).align(Align.left);
            itemLabel = new Label(t.getFighters().get(i).getDmg()+"", skin);
            listItemTable.add(itemLabel).expandX().align(Align.center);
            itemLabel = new Label(t.getFighters().get(i).getHp()+"", skin);
            listItemTable.add(itemLabel).expandX().align(Align.right);
            finalTable.add(listItemTable).growX().padLeft(CELL_PADDING *2).padRight(CELL_PADDING * 2);
            finalTable.row();
            listItemTable.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    core.setScreen(new FighterScreen(t.getFighters().get(i),core));
                }
            });
        });
        rootTable.row();
        TextButton textButton = new TextButton("Back", skin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                core.showStartScreen();
            }
        });
        rootTable.add(textButton);
        return rootTable;
    }


    @Override
    protected Table getFooter() {
        return null;
    }






}
