/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 03.11.19, 19:18
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.storage;

import net.prematic.databasequery.api.DatabaseCollection;
import net.prematic.databasequery.api.query.result.QueryResult;
import net.prematic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.storage.ContextStorage;
import net.pretronic.dkperms.common.context.DefaultPermissionContext;

import java.util.function.Function;

public class PDQContextStorage implements ContextStorage {

    private DatabaseCollection context;

    @Override
    public PermissionContext getContext(int id) {
        QueryResult result = this.context.find().where("Id",id).execute();
        if(!result.isEmpty()) return result.first().to((Function<QueryResultEntry, PermissionContext>) entry
                -> new DefaultPermissionContext(entry.getInt("id"),entry.getString("Name")));
        return null;
    }

    @Override
    public PermissionContext getContext(String name) {
        QueryResult result = this.context.find().where("Name",name).execute();
        if(!result.isEmpty()) return result.first().to((Function<QueryResultEntry, PermissionContext>) entry
                -> new DefaultPermissionContext(entry.getInt("id"),entry.getString("Name")));
        return null;
    }

    @Override
    public int createContext(String name) {
        return this.context.insert()
                .set("Name",name)
                .executeAndGetGeneratedKeys("id").first().getInt("id");
    }

    @Override
    public void updateContextName(int contextId, String name) {
        this.context.update()
                .set("Name",name)
                .where("Id",contextId)
                .execute();
    }

    @Override
    public void deleteContext(int contextId) {
        this.context.delete()
                .where("Id",contextId)
                .execute();
    }

    @Override
    public void deleteContext(String name) {
        this.context.delete()
                .where("Name",name)
                .execute();
    }

    public void setCollections(DatabaseCollection context){
        this.context = context;
    }

}
