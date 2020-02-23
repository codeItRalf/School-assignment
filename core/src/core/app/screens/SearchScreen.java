package core.app.screens;

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
import core.app.entity.Division;
import core.app.entity.Fighter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SearchScreen extends BaseScreen<Division> {


    private String inputText;
    private final List<Fighter> allFighters;
    private ArrayList<Fighter> filteredList;
    private final Table scrollTable;

    public SearchScreen(Core core) {
        super(null, core);
        allFighters = gameViewModel.getAllFighters();
        filteredList = new ArrayList<>(allFighters);
        scrollTable = new Table();
    }


    @Override
    protected Table getHeader() {
        Table headerTable = new Table();
        headerTable.align(Align.center);
        TextField textField = new TextField("", uiSkin);
        textField.setTextFieldListener((textField1, c) -> {
            if (c == '\n') {
                filterList();
            }
            inputText = textField1.getText().trim();
        });
        headerTable.add(textField);
        headerTable.row();
        Table table = new Table();
        Label label = new Label("Name:", skin);
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sortByName();
            }
        });
        label.setAlignment(Align.left);
        table.add(label).align(Align.center).width(CELL_WIDTH);
        label = new Label("Damage:", skin);
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sortByDamage();
            }
        });
        label.setAlignment(Align.center);
        table.add(label).align(Align.center).width(CELL_WIDTH);
        label = new Label("Team:", skin);
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sortByTeam();
            }
        });
        label.setAlignment(Align.right);
        table.add(label).align(Align.center).width(CELL_WIDTH);
        headerTable.add(table).growX();
        return headerTable;
    }

    private void sortByTeam() {
        filteredList = allFighters
                .stream()
                .parallel()
                .sorted(Comparator.comparing(e -> gameViewModel.getTeamForFighter(e).getName()))
                .collect(Collectors.toCollection(ArrayList::new));
        getBody();
    }

    private void sortByDamage() {
        filteredList = allFighters
                .stream()
                .parallel()
                .sorted(Comparator.comparing(Fighter::getDmg).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
        getBody();
    }

    private void sortByName() {
        filteredList = allFighters
                .stream()
                .parallel()
                .sorted(Comparator.comparing(Fighter::getName))
                .collect(Collectors.toCollection(ArrayList::new));
        getBody();
    }


    private void filterList() {
        filteredList = allFighters
                .stream()
                .parallel()
                .filter(a -> a.getName().toLowerCase().contains(inputText.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
        getBody();
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected Table getBody() {
        scrollTable.clearChildren();
        Table table = new Table();
        scrollTable.add(table).growX();
        if (filteredList != null && filteredList.size() > 0) {
            IntStream.range(0, filteredList.size()).forEach(i -> {
                Table listItemTable = new Table();
                Label itemLabel = new Label(filteredList.get(i).getName(), skin);
                itemLabel.setAlignment(Align.left);
                listItemTable.add(itemLabel).align(Align.center).width(CELL_WIDTH);
                itemLabel = new Label(filteredList.get(i).getDmg() + "", skin);
                itemLabel.setAlignment(Align.center);
                listItemTable.add(itemLabel).align(Align.center).width(CELL_WIDTH);
                itemLabel = new Label(gameViewModel.getTeamForFighter(filteredList.get(i)).getName(), skin);
                itemLabel.setAlignment(Align.right);
                listItemTable.add(itemLabel).align(Align.center).width(CELL_WIDTH);
                table.add(listItemTable).growX();
                table.row();
                listItemTable.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        core.setScreen(new FighterScreen(filteredList.get(i), core));
                    }
                });
            });
        }
        return scrollTable;
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

    @Override
    public void update(boolean update) {

    }
}
