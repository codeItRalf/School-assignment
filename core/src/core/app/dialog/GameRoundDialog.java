package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.app.Core;
import core.app.game.GameThreadPool;
import core.app.entity.Identity;
import core.app.menuScreen.BaseScreen;


public class GameRoundDialog<T extends Identity> extends BaseDialog<T> {

    private final BaseScreen<T> baseScreen;
    private int value;


    public <T extends Identity> GameRoundDialog(Skin skin, Stage stage, Core core, BaseScreen baseScreen) {
        super("Play rounds!", skin, stage, core, null);
        this.text("How many rounds do you want to play?");
        posButton = "Play";
        value = -1;
        this.baseScreen = baseScreen;
    }


    @Override
    protected void result(Object object) {
        if ((int) object != RESULT_CANCEL && (int) object != RESULT_WARNING) {
            try {
                value = Integer.parseInt(inputText);
                value += viewModel.getActualRoundCount();
            } catch (NumberFormatException e) {
                value = -1;
            }
            if (value != -1 && value > viewModel.getActualRoundCount()) {
                actionRequest();
            } else if (!object.equals(RESULT_CANCEL)) {
                alertDialog();
            }
        }

    }

    @Override
    protected void actionRequest() {
        runGame();
        core.showStartScreen();
    }

    private void runGame() {
        new GameThreadPool(core, value, baseScreen).run();
    }
}
