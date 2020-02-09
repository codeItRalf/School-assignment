package core.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import core.app.menu.StartScreen;
import core.fsdb.ViewModel;

public class Core extends Game {
    private Stage stage;
    private Skin skin;
    private BitmapFont bitmapFont;
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
        generateBitmapFont();
        showStartScreen();
    }

    public void showStartScreen() {
        setScreen(new StartScreen(viewModel, this));
    }

    private void generateBitmapFont(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Cinzel-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        bitmapFont = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose();
        // don'
    }

    public BitmapFont getBitmapFont() {
        return bitmapFont;
    }
}