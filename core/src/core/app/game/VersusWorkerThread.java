package core.app.game;

import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Team;
import core.app.viewModel.GameViewModel;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class VersusWorkerThread extends GameWorkerThread implements Runnable {


    public VersusWorkerThread(GameViewModel gameViewModel, Team team1, Team team2) {
        super(gameViewModel);
        teamA = deepClone(team1);
        teamB = deepClone(team2);

    }


    @Override
    public void run() {
        workCommand();
    }

    @Override
    protected void workCommand() {
        boolean isGameOver = false;
        while (!isGameOver && teamA.size() >0 && teamB.size() > 0) {
            if (r.nextBoolean()) {
                isGameOver = gameMechanic(teamA, teamB);
            } else {
                isGameOver = gameMechanic(teamB, teamA);
            }
        }
    }

    private ArrayList<Fighter> deepClone(Team team) {
        return team.getFighters()
                .stream()
                .map(Fighter::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    protected void updateDatabaseWithResult(ArrayList<Fighter> winner, ArrayList<Fighter> loser) {
        super.updateDatabaseWithResult(winner, loser);
        Team winnerTeam = gameViewModel.getTeamForFighter(winner.get(0));
        Team loserTeam = gameViewModel.getTeamForFighter(loser.get(0));
        if (winnerTeam.getDivisionId() > loserTeam.getDivisionId()) {
            Division winnersDiv = gameViewModel.getDivisionForTeam(winnerTeam);
            int winnersIndex = winnersDiv.getTeams().indexOf(winnerTeam);
            Division losersDiv = gameViewModel.getDivisionForTeam(loserTeam);
            int losersIndex = losersDiv.getTeams().indexOf(loserTeam);
            int winnersDivId = winnersDiv.getId();
            winnerTeam.setDivisionId(loserTeam.getDivisionId());
            loserTeam.setDivisionId(winnersDivId);
            winnersDiv.getTeams().add(losersDiv.getTeams().remove(losersIndex));
            losersDiv.getTeams().add(winnersDiv.getTeams().remove(winnersIndex));
            winnerTeam.setDivStatus(Team.DivStatus.UP);
            loserTeam.setDivStatus(Team.DivStatus.DOWN);
         }
    }
}
