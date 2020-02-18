package core.fsdb;

import core.app.GameDBGenerator;




public class MyDatabase {


    MyDatabase() { databaseBuilder();
    }

    private static void databaseBuilder() {
        boolean dataBaseExists = FileSystem.exists(MyDatabase.class.getSimpleName());
        if(!dataBaseExists){
            FileSystem.createDir(MyDatabase.class.getSimpleName());
            GameDBGenerator.createDatabaseData();
        }
    }


}
