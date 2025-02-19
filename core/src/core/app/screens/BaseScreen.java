package core.app.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.app.Core;
import core.app.DesktopWorker;
import core.app.GdxUtils;
import core.app.dialog.ChangeNameDialog;
import core.app.dialog.DeleteDialog;
import core.app.dialog.GameRoundDialog;
import core.app.entity.Division;
import core.database.Identity;
import core.app.entity.Team;
import core.app.game.GameThreadPool.RoundChangeListener;
import core.app.appViewModel.GameViewModel;

import java.util.Comparator;
import java.util.List;

public abstract class BaseScreen<T extends Identity> extends ScreenAdapter implements RoundChangeListener {


    protected final T t;
    protected Stage stage;
    protected Viewport viewport;
    protected final Skin skin;
    protected final Skin uiSkin;
    protected final TextField.TextFieldStyle textFieldStyle;
    protected final Core core;
    protected final GameViewModel gameViewModel;
    public static DesktopWorker desktopWorker;
    private int dragStartX, dragStartY;
    private int windowStartX, windowStartY;
    protected Table upperLeftTable;
    protected  Table upperRightTable;
    protected Table bodyTable;



    public static final float CELL_WIDTH = 150f;
    public static final float CELL_PADDING = 30f;

Logger logger  = new Logger("BaseScreen", Logger.DEBUG);

    public BaseScreen(T t, Core core) {
        this.t = t;
        this.skin = core.getSkin();
        this.core = core;
        this.gameViewModel = core.getGameViewModel();
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
        root.setFillParent(true);
        root.setBackground("bg");
        stage.addActor(root);
        root.add(getHeader()).growX();
        root.row();


        bodyTable = new Table();
        bodyTable.defaults().padRight(CELL_PADDING).padLeft(CELL_PADDING);
        ScrollPane scrollPane = new ScrollPane(bodyTable);
        scrollPane.setScrollY(0);

        root.add(scrollPane).growX().growY().align(Align.top);

        bodyTable.row();
        bodyTable.add(getBody()).growX();
        bodyTable.row();

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


    protected Table getHeader() {
        boolean isStartPage = this.getClass() == StartScreen.class;
        String headerTitle = isStartPage ? "The Arena" : t.getClass().getSimpleName();
        Table table = new Table();
        upperLeftTable = new Table();
        table.add(upperLeftTable).expandX();
        Table column2 = new Table();
        table.add(column2).expandX();
        upperRightTable = new Table();
        table.add(upperRightTable).expandX();
        if (isStartPage) upperLeftTable.add(getRoundButton()).align(Align.left);
        Label label = new Label(headerTitle, skin, "bg");
        label.setTouchable(Touchable.disabled);
        label.setAlignment(Align.center);
        column2.add(label).align(Align.center);
        if (isStartPage)
            upperRightTable.add(getSeason()).align(Align.right);
        column2.row();
        if (!isStartPage) {
            column2.add(getDeleteButton()).colspan(2);
        } else {
            column2.add(getSearchButton()).colspan(2);
        }
        return table;
    }

    protected Label getSeason() {
        return new Label("Season " + gameViewModel.getActualRoundCount() / 10, skin);
    }

    private Table getSearchButton() {
        Table table = new Table();
        TextButton textButton = new TextButton("Search", skin);
        textButton.align(Align.center);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                core.showSearchScreen();
            }
        });
        table.add(textButton);
        return table;
    }

    protected abstract Table getFooter();

    protected Table getTeamListItem(Team team) {
        Table listItemTable = new Table();
        listItemTable.defaults().growX();
        Label itemLabel = new Label(team.getName(), skin);
        itemLabel.setAlignment(Align.center);
        listItemTable.add(itemLabel).align(Align.center);
        itemLabel = new Label(team.getWins() + " - " + team.getLosses(), skin);
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
        List<Team> teams = gameViewModel.getTeamsForDiv(division);
        if(teams.size() > 0) {
            table = new Table();
            rootTable.add(table).growX();
            Table finalTable = table;
            teams
                    .stream()
                    .sorted(Comparator.comparing(Team::getWins).reversed())
                    .sorted(Comparator.comparing(Team::getDivStatus))
                    .forEach(e -> {
                        Table listItemTable = getTeamListItem(e);
                        finalTable.add(listItemTable).growX();
                        finalTable.row();
                        listItemTable.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                core.setScreen(new TeamScreen(e, core));
                            }
                        });
                        if(e.getDivStatus() != Team.DivStatus.UNCHANGED){
                            listItemTable.setBackground(getBackground(e.getDivStatus()));
                        }
                    });
        }

        return rootTable;
    }

    private Drawable getBackground(Team.DivStatus divStatus) {
        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
        if (divStatus.equals(Team.DivStatus.UP))
            bgPixmap.setColor(Color.GREEN);
            else bgPixmap.setColor(Color.RED);
        bgPixmap.fill();
        return  new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
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

    protected Label setNameLabel(){
        Label label = new Label( t.getName(), skin,"bg");
        label.setAlignment(Align.center);
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new ChangeNameDialog<>(uiSkin, stage, core, t).createDialog();
                System.out.println("Label clicked!");
            }
        });
        return label;
    }

    protected Label getDeleteButton() {
        Label label = new Label("Delete", skin);
        label.setAlignment(Align.center);
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new DeleteDialog<>(uiSkin, stage, core, t).createDialog();
            }
        });
        return label;
    }

    protected Label getRoundButton() {
        Label label = new Label("Round: " + gameViewModel.getActualRoundCount(), skin);
        label.setAlignment(Align.left);
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new GameRoundDialog<>(uiSkin, stage, core, BaseScreen.this).createDialog();
            }
        });
        return label;
    }


}
