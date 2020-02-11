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


public abstract class BaseDialog extends Dialog {

    protected static final int RESULT_OK = 0;
    protected static final int RESULT_CANCEL = 1;
    protected static final int RESULT_WARNING = 2;

    protected ViewModel viewModel;
    protected Stage stage;
    protected Skin skin;
    protected static String inputText = "";
    protected Core core;

    protected BaseDialog(String title, Skin skin, Stage stage, Core core) {
        super(title, skin);
        this.stage = stage;
        this.viewModel = core.getViewModel();
        this.skin = skin;
        this.core = core;
    }


    public void createDialog() {
        Table table = getContentTable();
        TextField textField = new TextField("",skin);
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                inputText = textField.getText();
            }
        });
        table.row();
        textField.setMessageText("Type name..");
        table.add(textField);
        button("Create",RESULT_OK);
        button("Cancel",RESULT_CANCEL);
        show(stage);
    }

    private void alertDialog(){
        Dialog baseDialog = new  Dialog("Warning!",skin);
        baseDialog.text("Invalid Input");
        baseDialog.button("OK",RESULT_WARNING);
        baseDialog.show(stage);
    }


    @Override
    protected void result(Object object) {
        int result = (Integer) object;
        System.out.println(inputText);
         if(inputText != null && inputText.length() > 0 && result == RESULT_OK){
             createEntity();
             System.out.println("Div created!");
             inputText = "";
         }else if(result != RESULT_CANCEL && result != RESULT_WARNING) {
          alertDialog();
         }
        inputText = "";
    }

    protected abstract void createEntity();
}
