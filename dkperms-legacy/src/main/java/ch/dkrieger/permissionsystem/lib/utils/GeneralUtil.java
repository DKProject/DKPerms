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

import java.util.Random;

public class GeneralUtil {

    public static final Random RANDOM = new Random();

    public static String getRandomString(final int size){
        char data = ' ';
        String dat = "";
        for(int i=0;i<=size;i++) {
            data = (char)(RANDOM.nextInt(25)+97);
            dat = data+dat;
        }
        return dat;
    }

    public static boolean hasTimeOut(Long timeout){
        if(timeout <= 0) return false;
        return System.currentTimeMillis() > timeout;
    }

    public interface AcceptAble<T> {
        boolean accept(T object);
    }

    public interface ForEach<T> {
        void forEach(T object);
    }
}
