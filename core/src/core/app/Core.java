package core.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Team;
import core.app.menuScreen.*;
import core.fsdb.ViewModel;

public class Core extends Game {
    private Skin skin;
    private BitmapFont bitmapFont;


    public static final float CELL_WIDTH = 150f;
    public static final float CELL_PADDING = 30f;

    //Logic and ViewModel
    private ViewModel viewModel;
    private GameLogic gameLogic;

    public Core(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void create() {
        generateBitmapFont();
        skin = initSkin();
        showStartScreen();


    }

    public void showStartScreen() {
        setScreen(new StartScreen(this));
    }

    public void showSearchScreen() {
        setScreen(new SearchScreen(this));
    }


    public void showDivisionScreen(Division division) {
        setScreen(new DivisionScreen(division, this));
    }

    public void showTeamScreen(Team team) {
        setScreen(new TeamScreen(team, this));
    }

    public void showFighterScreen(Fighter fighter){
        setScreen(new FighterScreen(fighter, this));
    }

    private void generateBitmapFont(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Cinzel-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        bitmapFont = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose();
        // don'
    }

    private Skin initSkin() {
        return  new Skin(Gdx.files.internal("shadow-walker-ui.json")) {
            //Override json loader to process FreeType fonts from skin JSON
            @Override
            protected Json getJsonLoader(final FileHandle skinFile) {
                Json json = super.getJsonLoader(skinFile);
                final Skin skin = this;

                json.setSerializer(FreeTypeFontGenerator.class, new Json.ReadOnlySerializer<FreeTypeFontGenerator>() {
                    @Override
                    public FreeTypeFontGenerator read(Json json,
                                                      JsonValue jsonData, Class type) {
                        String path = json.readValue("font", String.class, jsonData);
                        jsonData.remove("font");

                        FreeTypeFontGenerator.Hinting hinting = FreeTypeFontGenerator.Hinting.valueOf(json.readValue("hinting",
                                String.class, "AutoMedium", jsonData));
                        jsonData.remove("hinting");

                        Texture.TextureFilter minFilter = Texture.TextureFilter.valueOf(
                                json.readValue("minFilter", String.class, "Nearest", jsonData));
                        jsonData.remove("minFilter");

                        Texture.TextureFilter magFilter = Texture.TextureFilter.valueOf(
                                json.readValue("magFilter", String.class, "Nearest", jsonData));
                        jsonData.remove("magFilter");

                        FreeTypeFontGenerator.FreeTypeFontParameter parameter = json.readValue(FreeTypeFontGenerator.FreeTypeFontParameter.class, jsonData);
                        parameter.hinting = hinting;
                        parameter.minFilter = minFilter;
                        parameter.magFilter = magFilter;
                        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(skinFile.parent().child(path));
                        BitmapFont font = generator.generateFont(parameter);
                        skin.add(jsonData.name, font);
                        if (parameter.incremental) {
                            generator.dispose();
                            return null;
                        } else {
                            return generator;
                        }
                    }
                });

                return json;
            }
        };
    }



    public Skin getSkin() {
        return skin;
    }

    public TextField.TextFieldStyle getTextFieldStyle() {
        return   new TextField.TextFieldStyle(bitmapFont,
                Color.WHITE,
                null,
                null,
                null);
    }

    public ViewModel getViewModel() {
        return viewModel;
    }
}