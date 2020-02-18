package core.app.Repositary;

import core.app.entity.Identity;
import core.fsdb.Repository;

public class FighterRepository<T extends Identity> extends Repository<T> {


    public FighterRepository(String entityName) {
        super(entityName);
    }
}
