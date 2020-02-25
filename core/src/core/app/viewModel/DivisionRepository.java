package core.app.viewModel;

import core.app.entity.Division;
import core.database.Identity;
import core.database.Repository;

 class DivisionRepository extends Repository<Division> {


     public  DivisionRepository(String pathString) {
        super(pathString);
    }
}
