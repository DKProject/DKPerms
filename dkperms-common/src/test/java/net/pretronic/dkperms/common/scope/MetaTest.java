/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 03.11.19, 11:42
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.scope;

import net.prematic.databasequery.api.DatabaseDriver;
import net.prematic.databasequery.sql.SqlDatabaseDriverConfig;
import net.prematic.databasequery.sql.mysql.MySqlDatabaseDriver;
import net.prematic.libraries.logging.SimplePrematicLogger;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectManager;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.PermissionScopeManager;
import net.pretronic.dkperms.common.DKPermsTest;
import net.pretronic.dkperms.common.context.DefaultPermissionContextManager;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectManager;
import net.pretronic.dkperms.common.storage.PDQStorage;
import org.slf4j.impl.StaticLoggerBinder;

public class MetaTest {


    public static void main(String[] args) throws Exception{
        DKPerms instance = new DKPermsTest();

        PermissionScopeManager scopeManager = instance.getScopeManager();
        PermissionObjectManager objectManager = instance.getObjectManager();

        PermissionScope worldServerScope = scopeManager.newBuilder()
                .of("Namespace","Minecraft")
                .of("Server","Server-1")
                .of("World","World-1").build();

        PermissionScope worldScope = scopeManager.newBuilder()
                .of("Namespace","Minecraft")
                .of("World","World-1").build();


        PermissionScope currentScope = scopeManager.newBuilder()
                .of("Namespace","Minecraft")
                .of("Server","Server-1")
                .of("World","World-1").build();


        PermissionObject object = objectManager.getObject(1);
        //if(object == null) object = objectManager.createObject(scopeManager.getRoot(),objectManager.getTypeOrCreate("Test"),"TestObject");

        //object.getMeta().set("Test","World-Server",worldServerScope);
        //object.getMeta().set("Test","World",worldScope);

        long start = System.currentTimeMillis();
        object.getMeta().get(currentScope,"Test").getValue();
        start = System.currentTimeMillis()-start;

        System.out.println(start+"ms");

        start = System.currentTimeMillis();
        object.getMeta().get(currentScope,"Test").getValue();
        start = System.currentTimeMillis()-start;

        System.out.println(start+"ms");


        Thread.sleep(2000);
    }


}
