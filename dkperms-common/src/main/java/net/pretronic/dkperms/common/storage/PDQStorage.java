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
import net.prematic.databasequery.api.query.result.QueryResult;
import net.prematic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.storage.DKPermsStorage;
import net.pretronic.dkperms.common.context.DefaultPermissionContext;
import net.pretronic.dkperms.common.scope.DefaultPermissionScope;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PDQStorage implements DKPermsStorage {

    private final DatabaseDriver driver;
    private final Database database;

    private DatabaseCollection scope;
    private DatabaseCollection context;

    public PDQStorage(DatabaseDriver driver, Database database) {
        this.driver = driver;
        this.database = database;
    }

    @Override
    public String getName() {
        return driver.getName();
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

    @Override
    public List<PermissionScope> loadScopes(PermissionScope parent) {
        List<PermissionScope> scopes = new ArrayList<>();
        this.scope.find().where("ParentId",(parent!=null?parent.getId():null)).execute().loadIn(scopes, entry
                -> new DefaultPermissionScope(PDQStorage.this,entry.getInt("Id")
                ,entry.getString("Key"),entry.getString("Name"),parent));
        return scopes;
    }

    @Override
    public int insertScope(PermissionScope scope) {
        return this.scope.insert()
                .set("Key",scope.getKey())
                .set("Name",scope.getName())
                .set("Parent",scope.getParent())
                .executeAndGetGeneratedKeys("id").first().getInt("id");
    }

    @Override
    public void updateScopeName(PermissionScope scope) {
        this.scope.update()
                .set("Key",scope.getKey())
                .set("Name",scope.getName())
                .where("Id",scope.getId())
                .execute();
    }

    @Override
    public void updateScopeParent(PermissionScope scope, PermissionScope newParent) {
        this.scope.update()
                .set("Parent",newParent.getId())
                .where("id",scope.getId())
                .execute();
    }

    @Override
    public void deleteScope(PermissionScope scope) {
        this.scope.delete().where("Id",scope.getId()).execute();
    }

    @Override
    public PermissionContext getContext(int id) {
        QueryResult result = this.context.find().where("Id",id).execute();
        if(!result.isEmpty()) return result.first().to((Function<QueryResultEntry, PermissionContext>) entry
                -> new DefaultPermissionContext(this,entry.getInt("id"),entry.getString("Name")));
        return null;
    }

    @Override
    public PermissionContext getContext(String name) {
        QueryResult result = this.context.find().where("Name",name).execute();
        if(!result.isEmpty()) return result.first().to((Function<QueryResultEntry, PermissionContext>) entry
                -> new DefaultPermissionContext(this,entry.getInt("id"),entry.getString("Name")));
        return null;
    }

    @Override
    public int createContext(String name) {
        this.context.insert()
                .set("Name",name)
                .executeAndGetGeneratedKeys("id");
        return 0;
    }

    private void createTables(){
        this.scope = database.createCollection("DKPerms_Scope")
                .attribute("Id", DataType.INTEGER, CreateOption.AUTO_INCREMENT,CreateOption.PRIMARY_KEY,CreateOption.NOT_NULL)
                .attribute("ParentId",DataType.INTEGER, ForeignKey.of(database.getName(),"DKPerms_Scope","Id", ForeignKey.Option.CASCADE))
                .attribute("Key",DataType.STRING,128,CreateOption.NOT_NULL)
                .attribute("Name",DataType.STRING,128,CreateOption.NOT_NULL)
                .create();

        this.context = database.createCollection("DKPerms_Context")
                .attribute("Id", DataType.INTEGER, CreateOption.AUTO_INCREMENT,CreateOption.PRIMARY_KEY,CreateOption.NOT_NULL)
                .attribute("Name",DataType.STRING,CreateOption.NOT_NULL,CreateOption.UNIQUE)
                .create();
    }
}
