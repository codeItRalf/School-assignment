package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.app.Core;
import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Identity;
import core.app.entity.Team;


public  class ChangeNameDialog<T extends Identity> extends BaseDialog {

    private T t;

    public  ChangeNameDialog(Skin skin, Stage stage, Core core, T t) {
        super("Change Name!", skin, stage, core);
       this.t = t;
    }



    @Override
    protected void actionRequest() {
      String className =  t.getClass().getSimpleName();
      if(t!= null)t.setName(inputText);
      if(className.equals(Division.class.getSimpleName())){
            core.showDivisionScreen((Division) t);
      }else if(className.equals(Team.class.getSimpleName())){
            core.showTeamScreen((Team) t);
      }else if(className.equals(Fighter.class.getSimpleName())){
            core.showFighterScreen((Fighter) t);
      }else {
           throw new ClassCastException();
      }



    }
}
