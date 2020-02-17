package core.app.game;

import core.app.Core;
import core.app.entity.Identity;
import core.app.entity.Team;
import core.app.menuScreen.BaseScreen;
import core.app.ViewModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;


public class GameThreadPool {

    private Core core;
    private ViewModel viewModel;
    private int value;
    private RoundChangeListener roundChangeListener;
    private ReentrantLock lock = new ReentrantLock();

    public GameThreadPool(Core core, int round, BaseScreen<? extends Identity> tBaseScreen) {
        this.core = core;
        this.value = round;
        this.viewModel = core.getViewModel();
        this.roundChangeListener = tBaseScreen;
    }

    public interface RoundChangeListener {
         void update();
    }

    public void run() {
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
            lock.lock();
            try {
                roundChangeListener.update();
            }finally {
                lock.unlock();
            }


        });

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
