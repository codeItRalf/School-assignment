package core.app.entity;


import core.fsdb.annotation.Entity;
import core.fsdb.annotation.ForeignKey;
import core.fsdb.Identity;

import java.util.Random;

@Entity( primaryKey = "id", foreignKey = @ForeignKey(
        parent = Team.class,
        parentId = "teamId"))
public class Fighter extends Identity {
    private int teamId;
    private int hp = 15;
    private int dmg = 3;

    public enum attribute {
        DMG,
        HP
    }

    public Fighter() {
    }

    public Fighter(String name, int id, int teamId) {
        super(name, id);
        this.teamId = teamId;
    }

    public Fighter(Fighter fighter) {
        super(fighter.getName(), fighter.getId());
        this.teamId = fighter.getTeamId();
        this.dmg = fighter.getDmg();
        this.hp = fighter.getHp();
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


    public void upgradeStats() {
        Random r = new Random();
        int oldSum = this.hp + this.dmg;
        this.hp += r.nextInt(30) == 0 ? 150 : 3;
        this.dmg += r.nextInt(50) == 0 ? 50 : 1;
        support.firePropertyChange("upgrade", oldSum, this.hp + this.dmg);
    }

}
