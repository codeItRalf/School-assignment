package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.app.Core;
import core.app.entity.Identity;
import core.fsdb.ViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;


public abstract class BaseDialog<T extends Identity> extends Dialog {

    protected static final int RESULT_OK = 0;
    protected static final int RESULT_CANCEL = 1;
    protected static final int RESULT_WARNING = 2;

    protected String posButton;
    protected String negButton = "Cancel";


    protected ViewModel viewModel;
    protected Stage stage;
    protected Skin skin;
    protected static String inputText = "";
    protected Core core;
    protected T t;

    protected BaseDialog(String title, Skin skin, Stage stage, Core core, T t) {
        super(title, skin);
        this.stage = stage;
        this.viewModel = core.getViewModel();
        this.skin = skin;
        this.core = core;
        this.t = t;
    }


    public void createDialog() {
        TextField textField = new TextField("",skin);
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (c == '\n') {
                    result(RESULT_OK);
                }
                inputText = textField.getText();
            }
        });
        getContentTable().row();
        textField.setMessageText("Type name..");
        getContentTable().add(textField);
        button(posButton, RESULT_OK);
        button(negButton, RESULT_CANCEL);
        show(stage);
        stage.setKeyboardFocus(textField);
    }

    protected void alertDialog(){
        Dialog baseDialog = new  Dialog("Warning!",skin);
        baseDialog.text("Invalid Input");
        baseDialog.button("OK",RESULT_WARNING);
        baseDialog.show(stage);
    }


    @Override
    protected void result(Object object) {
        int result = (Integer) object;
        if (inputText != null && inputText.length() > 0 && result == RESULT_OK) {
            actionRequest();
            inputText = "";
        } else if (result != RESULT_CANCEL && result != RESULT_WARNING) {
            alertDialog();
        }
        inputText = "";

    }

    protected Table getListOfEntities(ArrayList<T> list, int currentParentIndex) {
        Table table = new Table();
        list
                .stream()
                .map(e -> {
                    Label label = null;
                    if (e.getId() != currentParentIndex) {
                        label = new Label(e.getName(), getSkin());
                        label.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                result(e);
                            }
                        });
                    }

                    return label;
                }).filter(Objects::nonNull)
                .sorted(Comparator.comparing(e -> e.getText().toString()))
                .collect(Collectors.toList())
                .forEach(e -> {
                    table.add(e).grow();
                    table.row();
                });
        return table;
    }

    protected abstract void actionRequest();
}
