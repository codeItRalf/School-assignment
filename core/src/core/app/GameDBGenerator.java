package core.app;


import core.app.entity.Division;
import core.app.entity.Fighter;
import core.app.entity.Team;
import core.fsdb.FileSystem;
import core.fsdb.MyDatabase;
import core.fsdb.Util;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class GameDBGenerator {

    public static void createDatabaseData() {
        int divisionCount = 3;
        int teamsInEachDiv = 5;
        int fightersInEachTeam = 4;
        AtomicInteger teamId = new AtomicInteger();
        AtomicInteger fighterId = new AtomicInteger();
        var listOfNames = Util.getListOfNames();
        IntStream.range(0,divisionCount)
                .forEach(index -> {
                    FileSystem.serialize(MyDatabase.class.getSimpleName(), new Division("Division " + (index +1),index,new ArrayList<>()));
                    IntStream.range(0,teamsInEachDiv)
                            .forEach(i -> {
                                FileSystem.serialize(MyDatabase.class.getSimpleName(),  new Team("Team " + (teamId.get() +1), teamId.get(), index, new ArrayList<>()));
                                IntStream.range(0,fightersInEachTeam)
                                        .forEach(j -> {
                                            FileSystem.serialize(MyDatabase.class.getSimpleName(),  new Fighter(listOfNames.remove(0), fighterId.getAndIncrement(), teamId.get()));
                                            System.out.printf("Index: %d, teamId: %d, fighterId %d\n",index,teamId.get(),fighterId.get());
                                        });
                                teamId.incrementAndGet();
                            });
                });
    }
}
