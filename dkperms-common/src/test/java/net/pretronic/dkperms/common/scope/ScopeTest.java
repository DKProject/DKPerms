/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.scope;

import net.prematic.databasequery.api.DatabaseDriver;
import net.prematic.databasequery.sql.SqlDatabaseDriverConfig;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseDriver;
import net.prematic.libraries.logging.PrematicLoggerFactory;
import net.prematic.libraries.logging.SimplePrematicLogger;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.PermissionScopeManager;
import net.pretronic.dkperms.common.DKPermsTest;
import net.pretronic.dkperms.common.storage.PDQStorage;
import org.slf4j.impl.StaticLoggerBinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;

public class ScopeTest {

    public static void main(String[] args) throws Exception{
        new DKPermsTest();


       /*
       scopeManager.newBuilder()
                .of("Namespace","Test")
                .of("Server","Server-1")
                .of("World","World-1")
                .of("Region","Region-1")
                .of("Block","Block-1").build().insert();

        scopeManager.newBuilder()
                .of("Namespace","Minecraft")
                .of("Server","Server-1")
                .of("World","World-1").build().insert();


        scopeManager.newBuilder()
                .of("Namespace","Minecraft")
                .of("Server","Server-1")
                .of("World","World-2").build().insert();

        scopeManager.newBuilder()
                .of("Namespace","Minecraft")
                .of("World","World-1").build().insert();

        scopeManager.newBuilder()
                .of("Namespace","Cloud")
                .of("ServiceGroup","Service-1")
                .of("ServiceX","Hallo").build().insert();

        scopeManager.newBuilder()
                .of("Namespace","XY")
                .of("sfsdfdf","Service-1")
                .of("ServiceX","Hallo").build().insert();
        scopeManager.newBuilder()
                .of("Namespace","XY")
                .of("dddd","Service-1")
                .of("ServiceX","Hallo").build().insert();
        */
        PermissionScope current = DKPerms.getInstance().getScopeManager().newBuilder()
                .of("Namespace","Minecraft")
                .of("ServerGroup","Server")
                .of("Server","Server-1")
                .of("World","World-1")
                .of("Region","Region-1").build();

        long start = System.currentTimeMillis();
        Collection<PermissionScope> result =  DKPerms.getInstance().getScopeManager().getValidScopes(current);
        start = System.currentTimeMillis()-start;
        System.out.println("Loaded scopes in "+start+"ms");

        start = System.currentTimeMillis();
        Collection<PermissionScope> result2 =  DKPerms.getInstance().getScopeManager().getValidScopes(current);
        start = System.currentTimeMillis()-start;
        System.out.println("Loaded scopes in "+start+"ms");

        ArrayList<PermissionScope> resultX = new ArrayList<>(result);
        resultX.sort(new Comparator<PermissionScope>() {
            @Override
            public int compare(PermissionScope o1, PermissionScope o2) {
                return o1.getLevel() > o2.getLevel()?0:-1;
            }
        });

        resultX.forEach(scope -> System.out.println(scope.getKey()+"#"+scope.getName()+" | "+scope.getPath()));

        Thread.sleep(2000);

        /*

        - SCOPE xv
            - test -> S5
        - SCOPE bd
            -test  -> S3
            SCOPE dsfsd
             -test -> S7

         */
    }

}
