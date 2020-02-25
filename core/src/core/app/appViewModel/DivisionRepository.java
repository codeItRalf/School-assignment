package core.app.appViewModel;

import core.app.entity.Division;
import core.database.Repository;

 public class DivisionRepository extends Repository<Division> {


     public  DivisionRepository(String pathString) {
        super(pathString);
    }
}
