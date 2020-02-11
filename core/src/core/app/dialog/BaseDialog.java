package core.app.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import java.awt.*;
import java.util.Arrays;


public class BaseDialog extends Dialog {

        static String text;
    private BaseDialog(String title, Skin skin) {
        super(title, skin);
    }

    public static void createDialog(String title, Skin skin, Stage stage){
       BaseDialog baseDialog = new  BaseDialog(title,skin);
       Table table = baseDialog.getContentTable();
        baseDialog.text("Create Division");
        TextField textField = new TextField("",skin);
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                text = textField.getText();
                System.out.println(text);
            }
        });
        table.row();
        textField.setMessageText("Type name..");
        table.add(textField);
        baseDialog.button("Create"); //sends "true" as the result
        baseDialog.button("Cancel", false); //sends "false" as the result
        baseDialog.show(stage);

    }




    @Override
    protected void result(Object object) {

            System.out.println("Result = " +  text);

    }
}
