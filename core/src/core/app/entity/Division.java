package core.app.entity;



import core.annotation.Entity;
import core.annotation.ForeignKey;
import core.annotation.Ignore;

import java.util.ArrayList;

import static core.annotation.ForeignKey.CASCADE;

@Entity(primaryKey = "id", foreignKey = @ForeignKey(
        child = Team.class,
        listOfChildren = "teams",
        onDelete = CASCADE))
public class Division extends Identity {

  @Ignore
  private ArrayList<Team> teams = new ArrayList<>();

    public Division() {
        super("undefined");
    }

    public Division(String name) {
        super(name);
    }

    public Division(String name, int id, ArrayList<Team> teams) {
        super(name,id);
        this.teams = teams;
    }

    public ArrayList<Team> getTeams() {
        if(teams == null){
            teams = new ArrayList<>();
        }
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

}
