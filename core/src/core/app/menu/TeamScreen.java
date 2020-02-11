package core.app.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import core.app.Core;
import core.app.entity.Division;
import core.app.entity.Team;

import java.util.stream.IntStream;


public class TeamScreen extends BaseScreen<Team> {

     private  Team team;
    public TeamScreen(Team team,  Core core ) {
        super(team, core);
        this.team = team;
    }

    @Override
    protected Table getBody() {
        Table rootTable = new Table();
        rootTable.defaults().growX();
        Table table = new Table();
        Label label = new Label( team.getName(), skin,"bg");
        label.setTouchable(Touchable.disabled);
        label.setAlignment(Align.center);
        table.add(label);
        rootTable.add(table);
        rootTable.row();
        label = new Label("Wins: " + team.getWins() + " | Losses: " + team.getLosses(),skin);
        label.setTouchable(Touchable.disabled);
        label.setAlignment(Align.center);
        rootTable.add(label);
        rootTable.row();
        table = new Table();
        table.defaults().growX();
        label = new Label("Name:" ,skin);
        label.setAlignment(Align.left);
        table.add(label).align(Align.center).width(CELL_WIDTH);
        label = new Label("Damage", skin);
        label.setAlignment(Align.center);
        table.add(label).align(Align.center).width(CELL_WIDTH);
        label = new Label("Hp", skin);
        label.setAlignment(Align.center);
        table.add(label).align(Align.center).width(CELL_WIDTH);
        rootTable.add(table).growX();
        rootTable.row();
        table = new Table();
        rootTable.add(table).growX();
        Table finalTable = table;
        IntStream.range(0, team.getFighters().size()).forEach(i ->{
            Table listItemTable = new Table();
            listItemTable.defaults().growX();
            Label itemLabel = new Label(team.getFighters().get(i).getName() ,skin);
            itemLabel.setAlignment(Align.left);
            listItemTable.add(itemLabel).align(Align.center).width(CELL_WIDTH);
            itemLabel = new Label(team.getFighters().get(i).getDmg()+"", skin);
            itemLabel.setAlignment(Align.center);
            listItemTable.add(itemLabel).align(Align.center).width(CELL_WIDTH);
            itemLabel = new Label(team.getFighters().get(i).getHp()+"", skin);
            itemLabel.setAlignment(Align.center);
            listItemTable.add(itemLabel).align(Align.center).width(CELL_WIDTH);
            finalTable.add(listItemTable).growX();
            finalTable.row();
            listItemTable.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    core.setScreen(new FighterScreen(team.getFighters().get(i),core));
                }
            });
        });

        return rootTable;
    }


    @Override
    protected Table getFooter() {
        Table table = new Table();
        TextButton textButton = new TextButton("Create Fighter", skin);
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
