package core.database;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Util {


    public static ArrayList<String> getListOfNames() {
        String path = "core/assets/names.txt";
        ArrayList<String> personNames = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            personNames = (ArrayList<String>) stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.shuffle(personNames);
        return personNames;
    }


}
