package core.app.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.app.Core;
import core.app.DesktopWorker;
import core.app.GdxUtils;
import core.app.entity.Identity;

public abstract class BaseScreen<T extends Identity> extends ScreenAdapter {

    public static float WIDTH = 640f;
    public static float HEIGHT = 720f;

    protected T t;
    protected Stage stage;
    protected  Table root;
    protected Viewport viewport;
    protected Skin skin;
    protected BitmapFont bitmapFont;
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
        this.bitmapFont = core.getBitmapFont();
    }

    public BaseScreen(Core core) {
        this.skin = core.getSkin();
        this.core = core;
        this.bitmapFont = core.getBitmapFont();
    }


    @Override
    public void show() {
        viewport = new FitViewport(WIDTH, HEIGHT);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        setBackGround();
        root.add(getTable()).growX();

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


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height,true);
    }



    protected void setBackGround() {
        root =  new Table(skin);
        root.setBackground("bg");
        root.center();
        root.setFillParent(true);
        root.pack();
        stage.addActor(root);

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

    protected abstract Table getTable();



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
