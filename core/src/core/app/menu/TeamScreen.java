package core.app.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.app.GdxUtils;
import core.app.entity.Division;
import core.app.entity.Team;
import core.fsdb.ViewModel;

import java.util.stream.IntStream;

import static core.app.Core.CELL_PADDING;
import static core.app.Core.CELL_WIDTH;

public class TeamScreen extends ScreenAdapter {

    public static float WIDTH = 640f;
    public static float HEIGHT = 720f;

    private Team team;
    private Stage stage;
    private Viewport viewport;
    private Skin skin;

    public TeamScreen(Team team, Skin skin ) {
        this.team = team;
        this.skin = skin;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height,true);
    }

    @Override
    public void show() {
        viewport = new FitViewport(WIDTH, HEIGHT);
        stage = new Stage(viewport);

        Gdx.input.setInputProcessor(stage);
        
        initUi();
    }

    private void initUi() {
        Table table =  new Table(skin);
        table.setBackground("bg");
        table.add(getTeamTable());
        table.center();
        table.setFillParent(true);
        table.pack();
        stage.addActor(table);

    }

    private Table getTeamTable() {

            Table rootTable = new Table();
            Table table = new Table();
            Label label = new Label( team.getName(), skin,"bg");
            label.setTouchable(Touchable.disabled);
            label.setAlignment(Align.center);
            table.add(label);
            rootTable.add(table);
            rootTable.row();
            table = new Table();
            label = new Label("Name:" ,skin);
            table.add(label).width(CELL_WIDTH).align(Align.left).padLeft(CELL_PADDING);
            label = new Label("Damage", skin);
            table.add(label).expandX().align(Align.center);
            label = new Label("Hp", skin);
            table.add(label).expandX().align(Align.right);
            rootTable.add(table).expandX().growX();
            rootTable.row();
            table = new Table();
            rootTable.add(table).growX();
            Table finalTable = table;
            IntStream.range(0,team.getFighters().size()).forEach(i ->{
                Table listItemTable = new Table();
                Label itemLabel = new Label(team.getFighters().get(i).getName() ,skin);
                listItemTable.add(itemLabel).width(CELL_WIDTH).align(Align.left).padLeft(CELL_PADDING);
                itemLabel = new Label(team.getFighters().get(i).getDmg()+"", skin);
                listItemTable.add(itemLabel).expandX().align(Align.center);
                itemLabel = new Label(team.getFighters().get(i).getHp()+"", skin);
                listItemTable.add(itemLabel).expandX().align(Align.right);
                finalTable.add(listItemTable).growX();
                finalTable.row();
                listItemTable.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {

                    }
                });
            });

            return rootTable;
        }



    @Override
    public void render(float delta) {
        GdxUtils.clearScreen();
        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
       skin.dispose();
       stage.dispose();
    }
}
