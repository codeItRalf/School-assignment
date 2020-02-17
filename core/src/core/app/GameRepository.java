package core.app;

import core.app.entity.Identity;
import core.fsdb.FileSystem;
import core.fsdb.MyDatabase;
import core.fsdb.Repository;

import java.util.Objects;

public class GameRepository<T extends Identity> extends Repository<T> {

    public int getRoundCount() {
        String path = MyDatabase.class.getSimpleName() + "/fightCount";
        if (!FileSystem.exists(path)) {
            FileSystem.writeFile(path, "1");
        }
        return Integer.parseInt(Objects.requireNonNull(FileSystem.readFile(path)));
    }

    public void updateRoundCount(int fightCount) {
        String path = MyDatabase.class.getSimpleName() + "/fightCount";
        FileSystem.writeFile(path, String.valueOf(fightCount));
    }
}
