package core.app.menuScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import core.app.Core;
import core.app.GameLogic;
import core.app.dialog.AddDivisionDialog;
import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Identity;
import core.app.entity.Team;
import core.fsdb.ViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SearchScreen extends BaseScreen<Division> {


    private String inputText;
    private ArrayList<Fighter> allFighters;

    public SearchScreen(Core core) {
        super(null, core);
        allFighters = getAllFighters();
    }

    private ArrayList<Fighter> getAllFighters() {
        return viewModel.getAllDivisions()
                .stream()
                .parallel()
                .map(Division::getTeams)
                .flatMap(List::stream)
                .map(Team::getFighters)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(Identity::getName))
                .collect(Collectors.toCollection(ArrayList::new));
    }


    @Override
    protected Table getHeader() {
        Table headerTable = new Table();
        headerTable.align(Align.center);
        TextField textField = new TextField("", uiSkin);
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (c == '\n') {

                }
                inputText = textField.getText();
            }
        });
        headerTable.add(textField);
        headerTable.row();
        Table table = new Table();
        Label label = new Label("Name:", skin);
        label.setAlignment(Align.left);
        table.add(label).align(Align.center).width(CELL_WIDTH);
        label = new Label("Damage:", skin);
        label.setAlignment(Align.center);
        table.add(label).align(Align.center).width(CELL_WIDTH);
        label = new Label("Team:", skin);
        label.setAlignment(Align.right);
        table.add(label).align(Align.center).width(CELL_WIDTH);
        headerTable.add(table).growX();
        return headerTable;
    }

    @Override
    protected Table getBody() {
        Table rootTable = new Table();
        Table table = new Table();
        rootTable.add(table).growX();
        Table finalTable = table;
        if (allFighters != null && allFighters.size() > 0) {
            IntStream.range(0, allFighters.size()).forEach(i -> {
                Table listItemTable = new Table();
                Label itemLabel = new Label(allFighters.get(i).getName(), skin);
                itemLabel.setAlignment(Align.left);
                listItemTable.add(itemLabel).align(Align.center).width(CELL_WIDTH);
                itemLabel = new Label(allFighters.get(i).getDmg() + "", skin);
                itemLabel.setAlignment(Align.center);
                listItemTable.add(itemLabel).align(Align.center).width(CELL_WIDTH);
                itemLabel = new Label(viewModel.getTeamForFighter(allFighters.get(i)).getName(), skin);
                itemLabel.setAlignment(Align.right);
                listItemTable.add(itemLabel).align(Align.center).width(CELL_WIDTH);
                finalTable.add(listItemTable).growX();
                finalTable.row();
                listItemTable.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        core.setScreen(new FighterScreen(allFighters.get(i), core));
                    }
                });
            });
        }
        return rootTable;
    }


    @Override
    protected Table getFooter() {
        Table table = new Table();
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
