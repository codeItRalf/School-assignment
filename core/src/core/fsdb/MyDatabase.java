package core.fsdb;


import core.annotation.Database;
import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Identity;
import core.app.entity.Team;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Database(entities = {Division.class, Fighter.class, Team.class })
public class MyDatabase {

   private static String dbName = MyDatabase.class.getSimpleName();

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

    private static void createDatabaseData() {
        int divisionCount = 3;
        int teamsInEachDiv = 5;
        int fightersInEachTeam = 4;
        AtomicInteger teamId = new AtomicInteger();
        AtomicInteger fighterId = new AtomicInteger();
        var listOfNames = Util.getListOfNames();
        IntStream.range(0,divisionCount)
                .forEach(index -> {
                    FileSystem.serialize(dbName, new Division("Division " + (index +1),index,new ArrayList<>()));
                    IntStream.range(0,teamsInEachDiv)
                            .forEach(i -> {
                                FileSystem.serialize(dbName,  new Team("Team " + (teamId.get() +1), teamId.get(), index, new ArrayList<>()));
                                IntStream.range(0,fightersInEachTeam)
                                        .forEach(j -> {
                                            FileSystem.serialize(dbName,  new Fighter(listOfNames.remove(0), fighterId.getAndIncrement(), teamId.get()));
                                            System.out.printf("Index: %d, teamId: %d, fighterId %d\n",index,teamId.get(),fighterId.get());
                                        });
                                teamId.incrementAndGet();
                            });
                });
    }

    private static void createDatabaseDirs() {
        FileSystem.createDir(dbName);
        Arrays.asList(MyDatabase.class.getAnnotation(Database.class).entities()).forEach(e ->
                FileSystem.createDir(dbName + "/" + e.getSimpleName()));
    }

    synchronized void serialize( Identity obj){
        String path = dbName + "/" + obj.getClass().getSimpleName() + "/" + obj.getId();
        try (var out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized Object deserialize(String path, int id){
        String rootPath = dbName + "/" + path +  "/" + id;
        Object o = null;
        try(var in = new ObjectInputStream(new FileInputStream(rootPath))){
            o = in.readObject();
        }
        catch(IOException ex )
        {
            System.out.println("IOException is caught");
        }
        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }
        return o;
    }

}
