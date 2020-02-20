package core.app.entity;



import core.fsdb.annotation.Entity;
import core.fsdb.annotation.ForeignKey;
import core.fsdb.annotation.Ignore;
import core.fsdb.Identity;

import java.util.ArrayList;
import java.util.List;

import static core.fsdb.annotation.ForeignKey.CASCADE;

@Entity(primaryKey = "id", foreignKey = @ForeignKey(
        child = Team.class,
        listOfChildren = "teams",
        onDelete = CASCADE))
public class Division extends Identity {

  @Ignore
  private List<Team> teams = new ArrayList<>();

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

    public List<Team> getTeams() {
        if(teams == null){
            teams = new ArrayList<>();
        }
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

}
