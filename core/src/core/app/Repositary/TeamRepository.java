package core.app.Repositary;

import core.app.entity.Identity;
import core.fsdb.Repository;

public class TeamRepository<T extends Identity> extends Repository<T> {


    public TeamRepository(String entityName) {
        super(entityName);
    }
}
