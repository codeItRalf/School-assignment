package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import core.app.Core;
import core.app.entity.Division;
import core.fsdb.ViewModel;

import java.util.ArrayList;


public class BaseDialog extends Dialog {

    protected static final int RESULT_OK = 0;
    protected static final int RESULT_CANCEL = 1;
    protected static final int RESULT_WARNING = 2;

    protected ViewModel viewModel;
    protected Stage stage;
    protected Skin skin;
    protected static String text = "";
    protected Core core;

    protected BaseDialog(String title, Skin skin, Stage stage, Core core) {
        super(title, skin);
        this.stage = stage;
        this.viewModel = core.getViewModel();
        this.skin = skin;
        this.core = core;
    }

    protected BaseDialog(String s, Skin skin, Stage stage) {
        super(s, skin);
    }

    public static void createDialog(String title, String dialogMsg, Skin skin, Stage stage, Core core){
       BaseDialog baseDialog = new  BaseDialog(title,skin, stage, core);
       Table table = baseDialog.getContentTable();
        baseDialog.text(dialogMsg);
        TextField textField = new TextField("",skin);
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
               text = textField.getText();
            }
        });
        table.row();
        textField.setMessageText("Type name..");
        table.add(textField);
        baseDialog.button("Create",RESULT_OK);
        baseDialog.button("Cancel",RESULT_CANCEL);
        baseDialog.show(stage);
    }

    private void alertDialog(){
        BaseDialog baseDialog = new  BaseDialog("Warning!",skin, stage);
        baseDialog.text("Invalid Input");
        baseDialog.button("OK",RESULT_WARNING);
        baseDialog.show(stage);
    }


    @Override
    protected void result(Object object) {
        int result = (Integer) object;
        System.out.println(text);
         if(text != null && text.length() > 0 && result == RESULT_OK){
             viewModel.insertDivision(new Division(text,-1,new ArrayList<>()));
             System.out.println("Div created!");
             text = "";
             core.showStartScreen();
         }else if(result != RESULT_CANCEL && result != RESULT_WARNING) {
          alertDialog();
         }
    }
}
