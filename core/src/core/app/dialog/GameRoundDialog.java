package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.app.Core;
import core.app.game.GameWorkerThread;
import core.app.entity.Identity;


public class GameRoundDialog<T extends Identity> extends BaseDialog<T> {


    private int value = -1;

    public GameRoundDialog(Skin skin, Stage stage, Core core) {
        super("Play rounds!", skin, stage, core, null);
        this.text("Until what round do you want to play?");
        posButton = "Play";
    }


    @Override
    protected void result(Object object) {
        try {
            value = Integer.parseInt(inputText);
        } catch (NumberFormatException e) {
            value = -1;
        }
        if (value != -1 && value > viewModel.getActualRoundCount()) {
            actionRequest();
        } else if (!object.equals(RESULT_CANCEL)) {
            alertDialog();
        }
    }

    @Override
    protected void actionRequest() {

    }
}
