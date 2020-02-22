package core.fsdb;


import core.app.entity.Identity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystem {
    /**
     * Reads data from a file into a string and returns it.
     *
     * @param fileName Path of the file to read from.
     * @return Returns string with the data read, or null if something went wrong.
     */
      public static String readFile(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Writes given data to a file.
     *
     * @param fileName Path of the file to write to.
     * @param data     The data to be written into the file.
     */
      public static void writeFile(String fileName, String data) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
            bufferedWriter.write(data);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests a paths existence.
     *
     * @param filePath Path to check.
     * @return Returns true if the path exists, else returns false.
     */
      public static boolean exists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Removes a file or directory with the given path.
     *
     * @param filePath Path to remove.
     * @return Returns true if the file/directory was removed, else returns false.
     */
     static boolean delete(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a directory at the given path.
     *
     * @param dirName Path of the directory to create.
     * @return Returns true if the directory was created.
     */
     static boolean createDir(String dirName) {
        return new File(dirName).mkdir();
    }

    /**
     * Creates a directory at the given path.
     *
     * @param dirName      Path of the directory to create.
     * @param createNested If all missing directories should be created as well.
     * @return Returns true if the directory was created.
     */
     static boolean createDir(String dirName, boolean createNested) {
        return new File(dirName).mkdirs();
    }

    /**
     * Gets all file paths in a directory and turns them into a java File array.
     *
     * @param path Path of the directory to get files from.
     * @return Returns array of files found or null if something went wrong.
     */
     static File[] getDirFiles(String path) {
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .toArray(File[]::new);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    static <E extends Identity> Object deserialize(String path, int id){
        String rootPath = MyDatabase.class.getSimpleName() + "/" + path +  "/" + id;
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

     public static void serialize(Identity obj){
        String path = MyDatabase.class.getSimpleName() + "/" + obj.getClass().getSimpleName() + "/" + obj.getId();
        try (var out = new ObjectOutputStream(new FileOutputStream(path,false))) {
            out.writeObject(obj);

        } catch (IOException e) {
            System.out.println("serialize un-sucessfull!");
            e.printStackTrace();
        }
    }



       static File[] getSubFolders(String databasePath){
        return new File(databasePath).listFiles(File::isDirectory);
    }

synchronized static int generateId(String path) {
        var ref = new Object() {
            int id = 0;
        };
        File[] dirFiles = getDirFiles(path);
        List<Integer> ids =  Arrays.stream(Objects.requireNonNull(dirFiles))
                .map(File::toString)
                .map(e-> e.replaceAll("[^0-9]",""))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
        ids.forEach(e-> {
            if(e > ref.id){
                ref.id = e;
            }
        });
        return ref.id +1;
    }

     static List<Integer> getAllIds(String path){
         return   Arrays.stream(Objects.requireNonNull(getDirFiles(path)))
                .map(File::toString)
                .map(e-> e.replaceAll("[^0-9]",""))
                 .map(Integer::valueOf)
                .collect(Collectors.toList());
    }
}
