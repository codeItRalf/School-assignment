package core.app.entity;


import core.annotation.Entity;
import core.annotation.ForeignKey;
import core.annotation.Ignore;

import java.util.ArrayList;

import static core.annotation.ForeignKey.CASCADE;


@Entity( primaryKey = "id", foreignKey = @ForeignKey(
        parent = Division.class,
        parentId = "divisionId",
        listOfChildren = "fighters",
        child = Fighter.class,
        onDelete = CASCADE))
public class Team  extends Identity{
    private int divisionId;
    private int wins;
    private int losses;

    @Ignore
   private ArrayList<Fighter> fighters;

    public Team() {
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
        support.firePropertyChange("divisionId", this.divisionId, divisionId);
        this.divisionId = divisionId;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        support.firePropertyChange("wins", this.wins, wins);
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        support.firePropertyChange("losses", this.losses, losses);
        this.losses = losses;
    }

    public ArrayList<Fighter> getFighters() {
        return fighters;
    }

    public void setFighters(ArrayList<Fighter> fighters) {
        this.fighters = fighters;
    }

    @Override
    public String getNameColored() {
        return super.getNameColored();
    }
}
