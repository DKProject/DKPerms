/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.04.20, 10:24
 * @website %web%
 *
 * %license%
 */

package ch.dkrieger.permissionsystem.lib.utils;

import java.io.File;

public class FileUtil {

    public static void deleteDirectory(String path){
        deleteDirectory(new File(path));
    }

    public static void deleteDirectory(File file){
        if(file.exists()){
            File[] files = file.listFiles();
            if(files == null) return;
            for(File entries : files){
                if(entries.isDirectory()) deleteDirectory(entries.getAbsolutePath());
                else entries.delete();
            }
        }
        file.delete();
    }

    public static void renameFile(String file, String newFile){
        renameFile(new File(file),new File(newFile));
    }

    public static void renameFile(File file,File newFile){
        try{
            if(file == null || !(file.exists())) return;
            if(newFile.exists()) newFile.delete();
            newFile.createNewFile();
            file.renameTo(newFile);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
