package core.app.viewModel;

import core.app.entity.Fighter;
import core.database.Identity;
import core.database.Repository;

 class FighterRepository extends Repository<Fighter> {


     FighterRepository(String entityName) {
        super(entityName);
    }
}
