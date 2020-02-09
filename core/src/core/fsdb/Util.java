package core.fsdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Util {
    /**
     * Takes a string and converts it to a List of strings, separated by newline.
     * @param input String input.
     * @return List with lines.
     */
    public static List<String> stringToLines(String input) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(input.strip()))) {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            return lines;
        } catch (IOException e) {
            return null;
        }
    }

    public static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }


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
