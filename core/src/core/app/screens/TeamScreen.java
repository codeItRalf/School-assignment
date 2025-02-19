package core.app.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import core.app.Core;
import core.app.dialog.AddFighterDialog;
import core.app.dialog.MoveToDivisionDialog;
import core.app.entity.Fighter;
import core.app.entity.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


@SuppressWarnings("DuplicatedCode")
public class TeamScreen extends BaseScreen<Team> {

    private final Team team;

    public TeamScreen(Team team,  Core core ) {
        super(team, core);
        this.team = team;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected Table getBody() {
        Table rootTable = new Table();
        rootTable.defaults().growX();
        Table table = new Table();
        table.add(setNameLabel());
        rootTable.add(table);
        rootTable.row();
        Label label = new Label("Division: " + core.getGameViewModel().getDivisionForTeam(team).getName(), skin);
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new MoveToDivisionDialog<>(uiSkin, stage, core, t).createDialog();
            }
        });
        label.setAlignment(Align.center);
        rootTable.add(label);
        rootTable.row();
        label = new Label("Wins: " + team.getWins() + " | Losses: " + team.getLosses(), skin);
        label.setTouchable(Touchable.disabled);
        label.setAlignment(Align.center);
        rootTable.add(label);
        rootTable.row();
        table = new Table();
        label = new Label("Name:" ,skin);
        label.setAlignment(Align.left);
        table.add(label).align(Align.center).width(CELL_WIDTH);
        label = new Label("Damage", skin);
        label.setAlignment(Align.center);
        table.add(label).align(Align.center).width(CELL_WIDTH);
        label = new Label("Hp", skin);
        label.setAlignment(Align.right);
        table.add(label).align(Align.center).width(CELL_WIDTH);
        rootTable.add(table).growX();
        rootTable.row();
        table = new Table();
        rootTable.add(table).growX();
        Table finalTable = table;
        List<Fighter> fighterList = gameViewModel.getFightersForTeam(team);
        if(fighterList.size() > 0){
            fighterList.forEach(i ->{
                Table listItemTable = new Table();
                Label itemLabel = new Label(i.getName() ,skin);
                itemLabel.setAlignment(Align.left);
                listItemTable.add(itemLabel).align(Align.center).width(CELL_WIDTH);
                itemLabel = new Label(i.getDmg()+"", skin);
                itemLabel.setAlignment(Align.center);
                listItemTable.add(itemLabel).align(Align.center).width(CELL_WIDTH);
                itemLabel = new Label(i.getHp() + "", skin);
                itemLabel.setAlignment(Align.right);
                listItemTable.add(itemLabel).align(Align.center).width(CELL_WIDTH);
                finalTable.add(listItemTable).growX();
                finalTable.row();
                listItemTable.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        core.setScreen(new FighterScreen(i,core));
                    }
                });
            });
        }


        return rootTable;
    }


    @Override
    protected Table getFooter() {
        Table table = new Table();
        TextButton textButton = new TextButton("Create Fighter", skin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new AddFighterDialog(uiSkin,stage,core, team).createDialog();
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


    @Override
    public void update(boolean update) {

    }
}
