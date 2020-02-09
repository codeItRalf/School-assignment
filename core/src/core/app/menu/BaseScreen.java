package core.app.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.app.Core;
import core.app.GdxUtils;
import core.app.entity.Identity;

public abstract class BaseScreen<T extends Identity> extends ScreenAdapter {

    public static float WIDTH = 640f;
    public static float HEIGHT = 720f;

    protected T t;
    protected Stage stage;
    protected Viewport viewport;
    protected Skin skin;
    protected Core core;


    public BaseScreen(T t, Skin skin, Core core) {
        this.t = t;
        this.skin = skin;
        this.core = core;
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
        table.add(getTable()).growX();
        table.center();
        table.setFillParent(true);
        table.pack();
        stage.addActor(table);

    }

    protected abstract Table getTable();



    @Override
    public void render(float delta) {
        GdxUtils.clearScreen();
        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
   //TODO need to dispose??
    }

    @Override
    public void dispose() {
       skin.dispose();
       stage.dispose();
    }
}
