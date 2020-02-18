package core.app.game;

import core.app.entity.Fighter;
import core.app.entity.Team;
import core.app.GameViewModel;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class GameWorkerThread implements Runnable {
    protected final Random r = new Random();
    protected final GameViewModel gameViewModel;
    private int divIndex;



    protected ArrayList<Fighter> teamA = new ArrayList<>();
    protected ArrayList<Fighter> teamB = new ArrayList<>();

    public GameWorkerThread(GameViewModel gameViewModel) {
        this.gameViewModel = gameViewModel;
    }

    public GameWorkerThread(GameViewModel gameViewModel, int divIndex) {
        this(gameViewModel);
        this.divIndex = divIndex;
    }


    @Override
    public void run() {
        workCommand();
    }

    protected void workCommand() {
        IntStream.range(0, gameViewModel.getDivision(divIndex).getTeams().size() - 1).forEach(indexA -> {
            deepCopyTeam(indexA, teamA);
            IntStream.range(indexA + 1, gameViewModel.getDivision(divIndex).getTeams().size()).forEach(indexB -> {
                deepCopyTeam(indexB, teamB);
                boolean isGameOver = false;
                while (!isGameOver) {
                    if (r.nextBoolean()) {
                        isGameOver = gameMechanic(teamA, teamB);
                    } else {
                        isGameOver = gameMechanic(teamB, teamA);
                    }
                }
                deepCopyTeam(indexA, teamA);
            });
        });
    }

    protected void updateDatabaseWithResult(ArrayList<Fighter> winner, ArrayList<Fighter> loser) {
        Team winnerTeam = gameViewModel.getTeamForFighter(winner.get(0));
        winnerTeam.incrementWinCount();
        winner.forEach(e -> {
            int fighterIndex = winnerTeam.getFighters().indexOf(e);
            winnerTeam.getFighters().get(fighterIndex).upgradeStats();
        });
        Team loserTeam = gameViewModel.getTeamForFighter(loser.get(0));
        loserTeam.incrementLossCount();
    }

    protected boolean gameMechanic(ArrayList<Fighter> team1, ArrayList<Fighter> team2) {
        int dmg = team1.get(r.nextInt(team1.size())).getDmg();
        Fighter fighter = team2.get(r.nextInt(team2.size()));
        if (isKnockOut()) dmg = fighter.getHp();
        fighter.setHp(fighter.getHp() - dmg);
        if (fighter.getHp() <= 0) {
            if (team2.size() == 1) {
                updateDatabaseWithResult(team1, team2);
                return true;
            } else {
                team2.remove(fighter);
            }
        }
        return false;
    }

    private boolean isKnockOut() {
        return r.nextInt(20) == 1;
    }

    protected void deepCopyTeam(int index, ArrayList<Fighter> target) {
        target.clear();
        gameViewModel.getDivision(divIndex).getTeams().get(index).getFighters().forEach(a -> target.add(new Fighter(a)));
    }
}
