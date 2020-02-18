package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.app.Core;
import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Identity;
import core.app.entity.Team;


public class DeleteDialog<T extends Identity> extends BaseDialog<T> {

    private Identity parent;

    public DeleteDialog(Skin skin, Stage stage, Core core, T t) {
        super("Delete", skin, stage, core, t);
        if (t.getClass().equals(Team.class)) parent = gameViewModel.getDivisionForTeam((Team) t);
        else if (t.getClass().equals(Fighter.class))
            parent = gameViewModel.getTeamForFighter((Fighter) t);
    }

    @Override
    public void createDialog() {
        text("Sure you want to delete " + t.getName() + "?");
        button("Yes", true);
        button("No", false);
        show(stage);
    }

    @Override
    protected void result(Object object) {
        if (object.equals(true)) {
            actionRequest();
        }
    }

    @Override
    protected void actionRequest() {
        gameViewModel.deleteEntity(t);
        if (t.getClass().equals(Fighter.class)) {
            core.showTeamScreen((Team) parent);
        } else if (t.getClass().equals(Team.class)) {
            core.showDivisionScreen((Division) parent);
        } else core.showStartScreen();
    }

}
