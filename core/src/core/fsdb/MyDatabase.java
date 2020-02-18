package core.fsdb;


import core.annotation.Database;
import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Identity;
import core.app.entity.Team;

import java.io.*;
import java.util.Arrays;


import static core.app.GameDBGenerator.createDatabaseData;

@Database(entities = {Division.class, Fighter.class, Team.class })
public class MyDatabase {

   private static final String dbName = MyDatabase.class.getSimpleName();

    private static MyDatabase INSTANCE = null;


    private MyDatabase() {
    }


    static public MyDatabase getDatabase(){
        if(INSTANCE == null){
            synchronized (MyDatabase.class){
                if(INSTANCE == null){
                  databaseBuilder();
                }
            }
        }
            return INSTANCE;
    }

    private static void databaseBuilder() {
        boolean dataBaseExists = FileSystem.exists(MyDatabase.class.getSimpleName());
        if(!dataBaseExists){
            createDatabaseDirs();
            createDatabaseData();

        }
        INSTANCE = new MyDatabase();
    }



    private static void createDatabaseDirs() {
        FileSystem.createDir(dbName);
        Arrays.asList(MyDatabase.class.getAnnotation(Database.class).entities()).forEach(e ->
                FileSystem.createDir(dbName + "/" + e.getSimpleName()));
    }



}
