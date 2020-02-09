package core.fsdb;


import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Identity;
import core.app.entity.Team;

import java.util.ArrayList;

public class ViewModel {

        private Repository<? extends Identity> repository;
        private ArrayList<Division> divisions;
        private MyObserver myObserver;

    public ViewModel() {
     repository = new Repository<>();
     divisions = (ArrayList<Division>) repository.getAllOf(Division.class.getSimpleName());
     myObserver = new MyObserver(divisions);

    }





    public ArrayList<Identity> getTeamList(int divisionId) {
        return null;
    }

    public void insertFighter(Fighter fighter) {
    }

    public void insertDivision(Division division) {
    }

    public void insertTeam(Team team) {

    }

    public Division getDivision(int id){
        return divisions.get(id);
    }

    public ArrayList<Division> getAllDivisions(){
        return divisions;
    }

    public Team getTeam(int id){
        return null;
    }

    public Fighter getFighter(int id){
        return null;
    }


}
