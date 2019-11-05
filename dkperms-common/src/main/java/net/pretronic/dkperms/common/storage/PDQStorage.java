/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.storage;

import net.prematic.databasequery.api.Database;
import net.prematic.databasequery.api.DatabaseCollection;
import net.prematic.databasequery.api.DatabaseDriver;
import net.prematic.databasequery.api.ForeignKey;
import net.prematic.databasequery.api.datatype.DataType;
import net.prematic.databasequery.api.query.option.CreateOption;
import net.pretronic.dkperms.api.storage.ContextStorage;
import net.pretronic.dkperms.api.storage.DKPermsStorage;
import net.pretronic.dkperms.api.storage.ObjectStorage;
import net.pretronic.dkperms.api.storage.ScopeStorage;

public class PDQStorage implements DKPermsStorage {

    private final DatabaseDriver driver;
    private final Database database;

    private final PDQScopeStorage scopeStorage;
    private final PDQContextStorage contextStorage;
    private final PDQObjectStorage objectStorage;

    public PDQStorage(DatabaseDriver driver, Database database) {
        this.driver = driver;
        this.database = database;

        this.scopeStorage = new PDQScopeStorage();
        this.contextStorage = new PDQContextStorage();
        this.objectStorage = new PDQObjectStorage();
    }

    @Override
    public String getName() {
        return driver.getName();
    }

    @Override
    public ScopeStorage getScopeStorage() {
        return scopeStorage;
    }

    @Override
    public ContextStorage getContextStorage() {
        return contextStorage;
    }

    @Override
    public ObjectStorage getObjectStorage() {
        return objectStorage;
    }

    @Override
    public boolean connect() {
        driver.connect();
        createTables();
        return isConnected();
    }

    @Override
    public boolean isConnected() {
        return true; //driver.isConnected();
    }

    @Override
    public boolean disconnect() {
        driver.disconnect();
        return false;
    }



    private void createTables(){
        DatabaseCollection scope = database.createCollection("DKPerms_Scope")
                .attribute("Id", DataType.INTEGER, CreateOption.AUTO_INCREMENT,CreateOption.PRIMARY_KEY,CreateOption.NOT_NULL)
                .attribute("ParentId",DataType.INTEGER, ForeignKey.of(database.getName(),"DKPerms_Scope","Id", ForeignKey.Option.CASCADE))
                .attribute("Key",DataType.STRING,64,CreateOption.NOT_NULL)
                .attribute("Name",DataType.STRING,64,CreateOption.NOT_NULL)
                .create();

        DatabaseCollection context = database.createCollection("DKPerms_Context")
                .attribute("Id", DataType.INTEGER, CreateOption.AUTO_INCREMENT,CreateOption.PRIMARY_KEY,CreateOption.NOT_NULL)
                .attribute("Name",DataType.STRING,64,CreateOption.NOT_NULL,CreateOption.UNIQUE)
                .create();

        DatabaseCollection object_type = database.createCollection("DKPerms_Object_Type")
                .attribute("Id", DataType.INTEGER, CreateOption.AUTO_INCREMENT,CreateOption.PRIMARY_KEY,CreateOption.NOT_NULL)
                .attribute("Name",DataType.STRING,64,CreateOption.NOT_NULL,CreateOption.UNIQUE)
                .create();

        DatabaseCollection object = database.createCollection("DKPerms_Object")
                .attribute("Id", DataType.INTEGER, CreateOption.AUTO_INCREMENT,CreateOption.PRIMARY_KEY,CreateOption.NOT_NULL)
                .attribute("TypeId",DataType.INTEGER,ForeignKey.of(object_type,"Id"),CreateOption.NOT_NULL)
                .attribute("ScopeId",DataType.INTEGER,ForeignKey.of(scope,"Id"),CreateOption.NOT_NULL)
                .attribute("Name",DataType.STRING,64,CreateOption.NOT_NULL)
                .attribute("DisplayName",DataType.STRING,64,CreateOption.NOT_NULL)
                .attribute("Description",DataType.LONG_TEXT,CreateOption.NOT_NULL)
                .create();

        DatabaseCollection object_meta = database.createCollection("DKPerms_Object_Meta")
                .attribute("Id", DataType.INTEGER, CreateOption.AUTO_INCREMENT,CreateOption.PRIMARY_KEY,CreateOption.NOT_NULL)
                .attribute("ObjectId",DataType.INTEGER,ForeignKey.of(object,"Id"),CreateOption.NOT_NULL)
                .attribute("ScopeId",DataType.INTEGER,ForeignKey.of(scope,"Id"),CreateOption.NOT_NULL)
                .attribute("Key",DataType.STRING,CreateOption.NOT_NULL)
                .attribute("Value",DataType.STRING,CreateOption.NOT_NULL)
                .create();

        this.scopeStorage.setCollections(scope);
        this.contextStorage.setCollections(context);
        this.objectStorage.setCollections(object,object_type,object_meta);
    }
}
