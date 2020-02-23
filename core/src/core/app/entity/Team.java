package core.app.entity;


import core.annotation.Entity;
import core.annotation.ForeignKey;
import core.annotation.Ignore;
import core.database.Identity;

import java.util.ArrayList;

import static core.annotation.ForeignKey.CASCADE;


@Entity( primaryKey = "id", foreignKey = @ForeignKey(
        parent = Division.class,
        parentId = "divisionId",
        listOfChildren = "fighters",
        child = Fighter.class,
        onDelete = CASCADE))
public class Team extends Identity {
    private int divisionId;
    private int wins;
    private int losses;

    @Ignore
    private ArrayList<Fighter> fighters = new ArrayList<>();

    public Team() {
        super("undefined", -1);
    }

    public Team(String name, int id, int divisionId, ArrayList<Fighter> fighters) {
        super(name, id);
        this.divisionId = divisionId;
        this.fighters = fighters;
    }

    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        int oldValue = this.divisionId;
        this.divisionId = divisionId;
        support.firePropertyChange("divisionId", oldValue, this.divisionId);
    }

    public int getWins() {
        return wins;
    }

    public void incrementWinCount() {
        int oldValue = this.wins;
        this.wins++;
        support.firePropertyChange("wins", oldValue, this.wins);
    }

    public int getLosses() {
        return losses;
    }

    public void incrementLossCount() {
        int oldValue = this.losses;
        this.losses++;
        support.firePropertyChange("losses", oldValue, this.losses);

    }

    public void resetStats() {
        int oldValue = wins + losses;
        this.wins = 0;
        this.losses = 0;
        support.firePropertyChange("resetStats", oldValue, this.losses + this.wins);
    }

    public ArrayList<Fighter> getFighters() {
        if (fighters == null) {
            fighters = new ArrayList<>();
        }
        return fighters;
    }

    public void setFighters(ArrayList<Fighter> fighters) {
        this.fighters = fighters;
    }

}
