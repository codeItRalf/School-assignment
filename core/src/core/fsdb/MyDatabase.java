package core.fsdb;

import core.annotation.Database;
import core.app.GameDBGenerator;
import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Team;

import java.util.Arrays;

@Database(entities = {Division.class, Fighter.class, Team.class })
public class MyDatabase {


    MyDatabase() { databaseBuilder();
    }

    private  void databaseBuilder() {
        boolean dataBaseExists = FileSystem.exists(MyDatabase.class.getSimpleName());
        if(!dataBaseExists){
            createDatabaseDirs();
            GameDBGenerator.createDatabaseData();
        }
    }

    private  void createDatabaseDirs() {
        FileSystem.createDir(MyDatabase.class.getSimpleName());
        Arrays.asList(MyDatabase.class.getAnnotation(Database.class).entities()).forEach(e ->
                FileSystem.createDir(MyDatabase.class.getSimpleName() + "/" + e.getSimpleName()));
    }

}
