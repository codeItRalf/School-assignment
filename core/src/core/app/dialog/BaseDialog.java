package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.app.Core;
import core.app.entity.Identity;
import core.app.GameViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;


public abstract class BaseDialog<T extends Identity> extends Dialog {

    protected static final int RESULT_OK = 0;
    protected static final int RESULT_CANCEL = 1;
    protected static final int RESULT_WARNING = 2;

    protected String posButton;
    protected final String negButton = "Cancel";


    protected final GameViewModel gameViewModel;
    protected final Stage stage;
    protected final Skin skin;
    protected static String inputText = "";
    protected final Core core;
    protected final T t;

    protected BaseDialog(String title, Skin skin, Stage stage, Core core, T t) {
        super(title, skin);
        this.stage = stage;
        this.gameViewModel = core.getGameViewModel();
        this.skin = skin;
        this.core = core;
        this.t = t;
    }


    public void createDialog() {
        TextField textField = new TextField("", skin);
        textField.setTextFieldListener((textField1, c) -> {
            if (c == '\n') {
                result(RESULT_OK);
            }
            inputText = textField1.getText();
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
        if (inputText == null || inputText.length() < 1 && result != RESULT_WARNING && result != RESULT_CANCEL) {
            alertDialog();
        } else if (result == RESULT_OK) {
            actionRequest();
            inputText = "";
        }
        inputText = "";

    }

    protected Table getListOfEntities(ArrayList<? extends Identity> list, int currentParentIndex) {
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
