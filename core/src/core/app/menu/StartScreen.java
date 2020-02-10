package core.app.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.app.Core;
import core.app.GdxUtils;
import core.app.Logic;
import core.app.entity.Division;
import core.fsdb.ViewModel;

import java.util.stream.IntStream;

public class StartScreen extends BaseScreen<Division> {

    //Logic and ViewModel
    private ViewModel viewModel;
    private Logic logic;


    public StartScreen(ViewModel viewModel, Core core) {
        super(core);
        this.viewModel = viewModel;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        setBackGround();
        Gdx.input.setInputProcessor(stage);

        root.setTouchable(Touchable.enabled);
        root.setFillParent(true);
        root.row();
        stage.addActor(root);
        root.add(getHeader("The Arena")).growX();
        root.row();
        Stack stack = new Stack();
        root.add(stack).growX();


        Table table = new Table();
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setScrollY(0);

        stack.add(scrollPane);

        table.row();
        table.add(getAllDivisionTables()).growX().padLeft(CELL_PADDING).padRight(CELL_PADDING);
        table.row();

        root.row();
        root.add(getFooter()).align(Align.bottom).growX();
        

    }

    @Override
    protected Table getFooter() {
        Table subTable = new Table();
        subTable.defaults().fillX();
        TextButton textButton = new TextButton("Create Div", skin);
        subTable.add(textButton);
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

    @Override
    protected Table getTable() {
        return null;
    }

    @Override
    public void render(float delta) {
        GdxUtils.clearScreen();

        //fix incorrect blending
        stage.getBatch().setBlendFunction(-1, -1);
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //render the scene
        stage.act();
        stage.draw();
    }

    private Table getAllDivisionTables() {
        Table table = new Table();
        IntStream.range(0, viewModel.getAllDivisions().size()).forEach(i-> {
            table.add(getDivisionTable(i));
            table.row();
        });
        return table;
    }

    private Table getDivisionTable(int divIndex){
        Division division = viewModel.getDivision(divIndex);
        Table rootTable = new Table();
        Table table = new Table();
        Label label = new Label( division.getName(), skin, "bg");
        label.setTouchable(Touchable.disabled);
        label.setAlignment(Align.center);
        table.add(label);
        rootTable.add(table);
        rootTable.row();
        table = new Table();
        label = new Label("Team:" ,skin);
        table.add(label).width(CELL_WIDTH).align(Align.left).padLeft(CELL_PADDING);
        label = new Label("Win - Loss", skin);
        table.add(label).expandX().align(Align.center);
        rootTable.add(table).expandX().growX();
        rootTable.row();
        table = new Table();
        rootTable.add(table).growX();
        Table finalTable = table;
        IntStream.range(0,division.getTeams().size()).forEach(i ->{
            Table listItemTable = new Table();
            Label itemLabel = new Label(division.getTeams().get(i).getName() ,skin);
            listItemTable.add(itemLabel).width(CELL_WIDTH).align(Align.left).padLeft(CELL_PADDING);
            itemLabel = new Label(division.getTeams().get(i).getWins() +" - " + division.getTeams().get(i).getLosses() , skin);
            listItemTable.add(itemLabel).expandX().align(Align.center);
            finalTable.add(listItemTable).growX();
            finalTable.row();
            listItemTable.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println(division.getTeams().get(i).getName() +" clicked! ");
                    core.setScreen(new TeamScreen(division.getTeams().get(i),core));
                }
            });
        });

return rootTable;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        skin.dispose();
        stage.dispose();
    }


}
