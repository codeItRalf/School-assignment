package core.app.entity;


import core.annotation.Entity;
import core.annotation.ForeignKey;

@Entity( primaryKey = "id", foreignKey = @ForeignKey(
        parent = Team.class,
        parentId = "teamId"))
public class Fighter extends Identity  {
    private int teamId;
    private int hp = 10;
    private int dmg = 2;



    public Fighter() {
    }

    public Fighter(String name, int id, int teamId) {
        super(name,id);
        this.teamId = teamId;
    }



    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        support.firePropertyChange("teamId", this.teamId, teamId);
        this.teamId = teamId;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        support.firePropertyChange("hp", this.hp, hp);
        this.hp = hp;
    }

    public int getDmg() {
        return dmg;
    }

    public void setDmg(int dmg) {
        support.firePropertyChange("dmg", this.dmg, dmg);
        this.dmg = dmg;
    }

}
