package core.app;

import com.badlogic.gdx.Game;
import core.app.entity.Division;
import core.app.menu.StartScreen;
import core.app.menu.TeamScreen;
import core.fsdb.ViewModel;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.stream.IntStream;

public class Core extends Game {
    private Stage stage;
    private Skin skin;
    public static DesktopWorker desktopWorker;
    private int dragStartX, dragStartY;
    private int windowStartX, windowStartY;
    private int focusIndex;
    private Array<TextButton> buttons;


    public static final float CELL_WIDTH = 150f;
    public static final float CELL_PADDING = 30f;

    //Logic and ViewModel
    private ViewModel viewModel;
    private Logic logic;

    public Core(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void create() {
        showStartScreen();
    }

    public void showStartScreen(){
        setScreen(new StartScreen(viewModel, this));
    }
}