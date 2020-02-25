package core.database;

import core.annotation.Database;
import core.annotation.Table;
import core.app.GameDBGenerator;
import core.database.FileSystem;
import core.database.Repository;
import core.database.ViewModel;


import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class InitUtil {
    private static String root;

    public static void Injector(ViewModel viewModel) {
        if(createDirs(viewModel)){
            GameDBGenerator.createDatabaseData();
        }
        initiateRepo(viewModel, root);
    }

    private static void initiateRepo(ViewModel viewModel, String rootPath) {
        Arrays.asList(viewModel.getClass().getDeclaredFields()).forEach(e->{
            if( e.getAnnotation(Table.class) != null){
                try {
                 Repository<?> repository   = (Repository) Class.forName(e.getType().getName()).getDeclaredConstructor(new Class[]{String.class}).newInstance(root + "/" + e.getAnnotation(Table.class).entity().getSimpleName() + "/");
                 e.setAccessible(true);
                e.set(viewModel,repository);
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private static boolean createDirs(ViewModel viewModel) {
        root =  viewModel.getClass().getAnnotation(Database.class).name();
        if(!FileSystem.exists(root)){
            FileSystem.createDir(root);
            Arrays.asList(viewModel.getClass().getDeclaredFields()).forEach(e->{
                if(e.getAnnotation(Table.class) != null){
                    String subDir =  e.getAnnotation(Table.class).entity().getSimpleName();
                    String path = root + "/" + subDir;
                    if(!FileSystem.exists(path )){
                        FileSystem.createDir(path);
                    }
                }

            });
            return true;
        }

     return false;
    }

}
