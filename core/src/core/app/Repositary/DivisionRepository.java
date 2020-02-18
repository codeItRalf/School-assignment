package core.app.Repositary;

import core.app.entity.Identity;
import core.fsdb.FileSystem;
import core.fsdb.MyDatabase;
import core.fsdb.Repository;

import java.util.Objects;

public class DivisionRepository<T extends Identity> extends Repository<T> {


    public DivisionRepository(String entityName) {
        super(entityName);
    }
}
