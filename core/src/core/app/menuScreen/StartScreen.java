package core.app.menuScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import core.app.Core;
import core.app.GameLogic;
import core.app.dialog.AddDivisionDialog;
import core.app.entity.Division;
import core.fsdb.ViewModel;

import java.util.stream.IntStream;

public class StartScreen extends BaseScreen<Division> {


    //Logic and ViewModel
    private ViewModel viewModel;
    private GameLogic gameLogic;


    public StartScreen(ViewModel viewModel, Core core) {
        super(null, core);
        this.viewModel = viewModel;
    }



    @Override
    protected Table getBody() {
        return getAllDivisionTables();
    }


    private Table getAllDivisionTables() {
        Table table = new Table();
        table.defaults().growX();
        IntStream.range(0, viewModel.getAllDivisions().size()).forEach(i -> {
            table.add(getDivisionTable(i));
            table.row();
        });
        return table;
    }

    private Table getDivisionTable(int divIndex) {
        Division division = viewModel.getDivision(divIndex);
        Table rootTable = new Table();
        rootTable.defaults().growX();
        Table table = new Table();
        Label label = new Label(division.getName(), skin, "bg");
      //   label.setTouchable(Touchable.disabled);
        label.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                core.setScreen(new DivisionScreen(division, core));
            }
        });
        label.setAlignment(Align.center);
        table.add(label);
        rootTable.add(table);
        rootTable.row();
        rootTable.add(getDivisionContent(division));

        return rootTable;
    }


    @Override
    protected Table getFooter() {
        Table subTable = new Table();
        subTable.defaults().fillX();
        TextButton textButton = new TextButton("Create Div", skin);
        subTable.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                 new AddDivisionDialog(uiSkin, stage, core, t).createDialog();
            }
        });
        textButton = new TextButton("Quit", skin);
        subTable.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        return subTable;
    }

}
