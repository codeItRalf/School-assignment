package core.app.game;

import core.app.Core;
import core.fsdb.Identity;
import core.app.entity.Team;
import core.app.menuScreen.BaseScreen;
import core.app.GameViewModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;


public class GameThreadPool {

    private Core core;
    private GameViewModel gameViewModel;
    private int value;
    private RoundChangeListener roundChangeListener;


    public GameThreadPool(Core core, int round, BaseScreen<? extends Identity> tBaseScreen) {
        this.core = core;
        this.value = round;
        this.gameViewModel = core.getGameViewModel();
        this.roundChangeListener = tBaseScreen;
    }

    public interface RoundChangeListener {
         void update(boolean update);
    }

    public void run() {
        roundChangeListener.update(true);
        int actualRound = gameViewModel.getActualRoundCount();
        IntStream.range(actualRound, value).forEach(e -> {
            ExecutorService executorService = Executors.newFixedThreadPool(gameViewModel.getAllDivisions().size());
            IntStream.range(0, gameViewModel.getAllDivisions().size()).forEach(index -> {
                Runnable worker = new GameWorkerThread(gameViewModel, index);
                executorService.execute(worker);
            });
            executorService.shutdown();
            while (!executorService.isTerminated()) {
            }
            //If end of season, match worst and best teams of division and reset stats.
            if (gameViewModel.getActualRoundCount() % 10 == 0) {
                runSeasonEnding();
                gameViewModel.getAllTeams().forEach(Team::resetStats);
            }
            gameViewModel.incrementRoundCount();
        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        roundChangeListener.update(false);
    }

    private void runSeasonEnding() {
        var teams = new ArrayList<Team>();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        IntStream.range(0, gameViewModel.getAllDivisions().size()).forEach(index -> {
            if (index != 0) {
                teams.add(gameViewModel.getTheBestTeamInDiv(gameViewModel.getDivision(index)));
            }
            teams.add(gameViewModel.getTheWorstTeamInDiv(gameViewModel.getDivision(index)));
            if (teams.size() > 1) {
                Runnable worker = new VersusWorkerThread(gameViewModel, teams.remove(0), teams.remove(0));
                executorService.execute(worker);
            }
        });
        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }

    }
}
