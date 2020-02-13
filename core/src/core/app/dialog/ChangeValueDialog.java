package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.app.Core;
import core.app.entity.Fighter;
import core.app.entity.Identity;


public  class ChangeValueDialog<T extends Identity> extends BaseDialog {

    private T t;
    private Fighter.attribute attribute;

    public ChangeValueDialog(Skin skin, Stage stage, Core core, T t, Fighter.attribute attribute) {
        super("Change Value!", skin, stage, core);
       this.t = t;
       this.attribute = attribute;
    }



    @Override
    protected void actionRequest() {
        Fighter fighter = (Fighter) t;
        if(attribute.equals(Fighter.attribute.DMG)){
            fighter.setDmg(Integer.parseInt(inputText));
        }else if(attribute.equals(Fighter.attribute.HP)){
            fighter.setHp(Integer.parseInt(inputText));
        }
        core.showFighterScreen(fighter);
    }

    @Override
    protected void result(Object object) {
        int value;
        try {
         value = Integer.parseInt(inputText);
        }catch (NumberFormatException e){
            value = -1;
        }
        if(value != -1){
            super.result(object);
        }else {
            alertDialog();
        }
    }
}
