package com.mygdx.game.desktop;

import core.app.menuScreen.BaseScreen;
import core.fsdb.ViewModel;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import core.app.Core;
import core.app.DesktopWorker;

public class DesktopLauncher implements DesktopWorker {



    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setDecorated(false);
        config.setWindowedMode(640, 720);
        ViewModel viewModel = new ViewModel();
        Core core = new Core(viewModel);
        BaseScreen.desktopWorker = new DesktopLauncher();
        new Lwjgl3Application(core, config);
    }
    
    @Override
    public void dragWindow(int x, int y) {
        Lwjgl3Window window = ((Lwjgl3Graphics) Gdx.graphics).getWindow();
        window.setPosition(x, y);
    }

    @Override
    public int getWindowX() {
        return ((Lwjgl3Graphics) Gdx.graphics).getWindow().getPositionX();
    }

    @Override
    public int getWindowY() {
        return ((Lwjgl3Graphics) Gdx.graphics).getWindow().getPositionY();
    }

}
