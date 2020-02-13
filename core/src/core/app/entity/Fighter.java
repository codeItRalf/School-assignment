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

    public enum attribute {
        DMG,
        HP
    }

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
        int oldValue = this.teamId;
        this.teamId = teamId;
        support.firePropertyChange("teamId", oldValue, this.teamId);

    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        int oldValue = this.hp;
        this.hp = hp;
        support.firePropertyChange("hp", oldValue, this.hp);

    }

    public int getDmg() {
        return dmg;
    }

    public void setDmg(int dmg) {
        int oldValue = this.dmg;
        this.dmg = dmg;
        support.firePropertyChange("dmg", oldValue, this.dmg);

    }

}
