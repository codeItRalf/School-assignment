package core.app.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.app.Core;
import core.app.DesktopWorker;
import core.app.GdxUtils;
import core.app.entity.Division;
import core.app.entity.Identity;

import java.util.stream.IntStream;

public abstract class BaseScreen<T extends Identity> extends ScreenAdapter {


    protected T t;
    protected Stage stage;
    protected Viewport viewport;
    protected Skin skin;
    protected Skin uiSkin;
    protected TextField.TextFieldStyle textFieldStyle;
    protected Core core;
    public static DesktopWorker desktopWorker;
    private int dragStartX, dragStartY;
    private int windowStartX, windowStartY;

    public static final float CELL_WIDTH = 150f;
    public static final float CELL_PADDING = 30f;



    public BaseScreen(T t, Core core) {
        this.t = t;
        this.skin = core.getSkin();
        this.core = core;
        this.textFieldStyle = core.getTextFieldStyle();
        uiSkin = new Skin(Gdx.files.internal("default_skin/uiskin.json"));
    }


    @Override
    public void show() {
        viewport = new ScreenViewport();
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        final Table root = new Table(skin);
        root.setTouchable(Touchable.enabled);
        root.setFillParent(true);;
        root.setBackground("bg");
        stage.addActor(root);
        root.add(getHeader(t == null? "The Arena" : t.getClass().getSimpleName())).growX().align(Align.top);
        root.row();


        Table table = new Table();
        table.defaults().padRight(CELL_PADDING).padLeft(CELL_PADDING);
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setScrollY(0);

        root.add(scrollPane).growX().growY();

        table.row();
        table.add(getBody()).growX();
        table.row();

        root.row();
        root.add(getFooter()).align(Align.bottom).growX().align(Align.bottom);

        stage.addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (event.getTarget() == root) {
                    desktopWorker.dragWindow(windowStartX + (int) x - dragStartX, windowStartY + dragStartY - (int) y);
                    windowStartX =  BaseScreen.desktopWorker.getWindowX();
                    windowStartY =  BaseScreen.desktopWorker.getWindowY();
                }
            }

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                dragStartX = (int) x;
                dragStartY = (int) y;
                windowStartX = BaseScreen.desktopWorker.getWindowX();
                windowStartY = BaseScreen.desktopWorker.getWindowY();
            }
        });

        stage.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return super.mouseMoved(event, x, y);
            }


        });
    }

    protected abstract Table getBody();

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height,true);
    }




    protected Table getHeader(String headerTitle){
        Table table = new Table();
        Label label = new Label(headerTitle, skin, "bg");
        label.setTouchable(Touchable.disabled);
        label.setAlignment(Align.center);
         table.add(label);
         return table;
    }

    protected abstract Table getFooter();

    protected Table getTeamItem(Division division, int i){
        Table listItemTable =  new Table();
        listItemTable.defaults().growX();
        Label itemLabel = new Label(division.getTeams().get(i).getName() ,skin);
        itemLabel.setAlignment(Align.center);
        listItemTable.add(itemLabel).align(Align.center);
        itemLabel = new Label(division.getTeams().get(i).getWins() +" - " + division.getTeams().get(i).getLosses() , skin);
        itemLabel.setAlignment(Align.center);
        listItemTable.add(itemLabel).align(Align.center);
        return listItemTable;
    }

    protected Table getDivisionContent(Division division){
        Table rootTable = new Table();
        rootTable.defaults().growX();
        Table  table = new Table();
        table.defaults().growX();
        Label label = new Label("Team Name:" ,skin);
        label.setAlignment(Align.center);
        table.add(label).align(Align.center);
        label = new Label("Win - Loss", skin);
        label.setAlignment(Align.center);
        table.add(label).align(Align.center);
        rootTable.add(table);
        rootTable.row();
        table = new Table();
        rootTable.add(table).growX();
        Table finalTable = table;
        IntStream.range(0,division.getTeams().size()).forEach(i -> {
            Table listItemTable = getTeamItem(division, i);
            finalTable.add(listItemTable).growX();
            finalTable.row();
            listItemTable.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println(division.getTeams().get(i).getName() + " clicked! ");
                    core.setScreen(new TeamScreen(division.getTeams().get(i), core));
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
    stage.dispose();
    }

    @Override
    public void dispose() {
       skin.dispose();
       stage.dispose();
    }
}
