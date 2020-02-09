package core.app.menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.app.Core;
import core.app.DesktopWorker;
import core.app.GdxUtils;
import core.app.Logic;
import core.app.entity.Division;
import core.fsdb.ViewModel;

import java.util.stream.IntStream;

public class StartScreen extends ScreenAdapter {
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
    private Core core;

    public StartScreen(ViewModel viewModel, Core core) {
     this.viewModel = viewModel;
     this.core = core;
    }

    @Override
    public void show() {
        focusIndex = -1;
        skin =  getSkin();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        final Table root = new Table();
        root.setTouchable(Touchable.enabled);
        root.setFillParent(true);
        stage.addActor(root);

        Stack stack = new Stack();
        root.add(stack).growX();


        Image image = new Image(skin, "bg");

        image.setTouchable(Touchable.disabled);
        stack.add(image);

        Table table = new Table();
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setScrollY(0);

        stack.add(scrollPane);
        Label label = new Label("The Arena", skin, "bg");
        label.setTouchable(Touchable.disabled);
        label.setAlignment(Align.center);
        table.add(label).growX();

        buttons = new Array<TextButton>();

        table.row();
        table.add(getAllDivisionTables()).growX().padLeft(CELL_PADDING).padRight(CELL_PADDING);
        table.row();
        Table subTable = new Table();
        table.add(subTable);
        subTable.defaults().fillX();
        TextButton textButton = new TextButton("Create Div", skin);
        subTable.add(textButton);
        buttons.add(textButton);

        subTable.row();
        textButton = new TextButton("Quit", skin);
        subTable.add(textButton);
        buttons.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });


        
        stage.addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (event.getTarget() == root) {
                    desktopWorker.dragWindow(windowStartX + (int) x - dragStartX, windowStartY + dragStartY - (int) y);
                    windowStartX =  Core.desktopWorker.getWindowX();
                    windowStartY =  Core.desktopWorker.getWindowY();
                }
            }

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                dragStartX = (int) x;
                dragStartY = (int) y;
                windowStartX = Core.desktopWorker.getWindowX();
                windowStartY = Core.desktopWorker.getWindowY();
            }
        });
        
        stage.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                removeFocus();
                return super.mouseMoved(event, x, y);
            }
            
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Keys.TAB:
                        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
                            focusIndex--;
                        } else {
                            focusIndex++;
                        }

                        updateFocus();
                        break;
                        
                    case Keys.UP:
                        focusIndex--;
                        updateFocus();
                        break;
                        
                    case Keys.DOWN:
                        focusIndex++;
                        updateFocus();
                        break;
                        
                    case Keys.SPACE:
                    case Keys.ENTER:
                        if (focusIndex >=0 && focusIndex < buttons.size) {
                            InputEvent touchEvent = new InputEvent();
                            touchEvent.setType(InputEvent.Type.touchDown);
                            buttons.get(focusIndex).fire(touchEvent);
                            
                            touchEvent = new InputEvent();
                            touchEvent.setType(InputEvent.Type.touchUp);
                            buttons.get(focusIndex).fire(touchEvent);
                        }
                        break;
                }
                
                return super.keyDown(event, keycode);
            }
            
        });



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
                    core.setScreen(new TeamScreen(division.getTeams().get(i),skin,core));
                }
            });
        });

return rootTable;
    }

    private Skin getSkin() {
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

                        Hinting hinting = Hinting.valueOf(json.readValue("hinting",
                                String.class, "AutoMedium", jsonData));
                        jsonData.remove("hinting");

                        TextureFilter minFilter = TextureFilter.valueOf(
                                json.readValue("minFilter", String.class, "Nearest", jsonData));
                        jsonData.remove("minFilter");

                        TextureFilter magFilter = TextureFilter.valueOf(
                                json.readValue("magFilter", String.class, "Nearest", jsonData));
                        jsonData.remove("magFilter");

                        FreeTypeFontParameter parameter = json.readValue(FreeTypeFontParameter.class, jsonData);
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

    private void updateFocus() {
        if (focusIndex < 0) {
            focusIndex = buttons.size - 1;
        } else if (focusIndex >= buttons.size) {
            focusIndex = 0;
        }
        
        stage.setKeyboardFocus(buttons.get(focusIndex));
    }
    
    private void removeFocus() {
        focusIndex = -1;
        stage.setKeyboardFocus(null);
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
