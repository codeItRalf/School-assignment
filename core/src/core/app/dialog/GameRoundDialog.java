package core.app.dialog;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.app.Core;
import core.app.entity.Team;
import core.app.game.GameWorkerThread;
import core.app.entity.Identity;
import core.app.game.VersusWorkerThread;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;


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
        runGame();
    }

    private void runGame() {
        int actualRound = viewModel.getActualRoundCount();
        IntStream.range(actualRound, value).forEach(e -> {
            ExecutorService executorService = Executors.newFixedThreadPool(viewModel.getAllDivisions().size());
            IntStream.range(0, viewModel.getAllDivisions().size()).forEach(index -> {
                Runnable worker = new GameWorkerThread(viewModel, index);
                executorService.execute(worker);
            });
            executorService.shutdown();
            while (!executorService.isTerminated()) {
            }
            //If end of season, match worst and best teams of division and reset stats.
            if (viewModel.getActualRoundCount() % 10 == 0) {
                runSeasonEnding();
                viewModel.getAllTeams().forEach(Team::resetStats);
            }
            viewModel.incrementRoundCount();
        });
        core.showStartScreen();
    }

    private void runSeasonEnding() {
        var teams = new ArrayList<Team>();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        IntStream.range(0, viewModel.getAllDivisions().size()).forEach(index -> {
            if (index != 0) {
                teams.add(viewModel.getTheBestTeamInDiv(viewModel.getDivision(index)));
            }
            teams.add(viewModel.getTheWorstTeamInDiv(viewModel.getDivision(index)));
            if (teams.size() > 1) {
                Runnable worker = new VersusWorkerThread(viewModel, teams.remove(0), teams.remove(0));
                executorService.execute(worker);
            }
        });
        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }

    }
}
