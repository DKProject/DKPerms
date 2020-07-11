/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 04.05.20, 18:00
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object.meta;

import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ObjectMetaGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ObjectMeta extends Iterable<ObjectMetaEntry> {

    List<ObjectMetaEntry> get(String key);//From root

    List<ObjectMetaEntry> get(String key, PermissionScope scope);

    default ObjectMetaEntry getHighest(String key){
        List<ObjectMetaEntry> result =  get(key);
        if(result.isEmpty()) return null;
        return result.get(0);
    }

    default ObjectMetaEntry getHighest(String key, PermissionScope scope){
        List<ObjectMetaEntry> result =  get(key,scope);
        if(result.isEmpty()) return null;
        return result.get(0);
    }


    default ObjectMetaEntry getLowest(String key){
        List<ObjectMetaEntry> result =  get(key);
        if(result.isEmpty()) return null;
        return result.get(result.size()-1);
    }

    default ObjectMetaEntry getLowest(String key, PermissionScope scope){
        List<ObjectMetaEntry> result =  get(key,scope);
        if(result.isEmpty()) return null;
        return result.get(result.size()-1);
    }


    ScopeBasedDataList<ObjectMetaEntry> getEntries();

    ScopeBasedDataList<ObjectMetaEntry> getEntries(Graph<PermissionScope> scopes);

    Collection<ObjectMetaEntry> getEntries(PermissionScope scope);


    ObjectMetaGraph newGraph(Graph<PermissionScope> scopes);

    ObjectMetaGraph newInheritanceGraph(Graph<PermissionScope> scopes);


    boolean contains(String key);

    boolean contains(String key,PermissionScope scope);


    boolean isSet(String key, Object value);

    boolean isSet(PermissionScope scope,String key, Object value);


    ObjectMetaEntry set(PermissionObject executor, String key, Object value,int priority, PermissionScope scope, long timeout);


    default ObjectMetaEntry set(PermissionObject executor, String key, Object value,int priority, PermissionScope scope, long duration, TimeUnit unit){
        return set(executor, key,value,priority,scope,duration>0?unit.toMillis(duration)+System.currentTimeMillis():duration);
    }

    default ObjectMetaEntry set(PermissionObject executor, String key, Object value,int priority, PermissionScope scope,Duration duration){
        return set(executor, key,value,priority,scope,duration.getSeconds(),TimeUnit.SECONDS);
    }


    ObjectMetaEntry add(PermissionObject executor, String key, Object value,int priority, PermissionScope scope, long timeout);


    default ObjectMetaEntry add(PermissionObject executor, String key, Object value,int priority, PermissionScope scope, long duration, TimeUnit unit){
        return add(executor, key,value,priority,scope,duration>0?unit.toMillis(duration)+System.currentTimeMillis():duration);
    }

    default ObjectMetaEntry add(PermissionObject executor, String key, Object value,int priority, PermissionScope scope,Duration duration){
        return add(executor, key,value,priority,scope,duration.getSeconds(),TimeUnit.SECONDS);
    }



    void unset(PermissionObject executor,String key,PermissionScope scope);

    void remove(PermissionObject executor, ObjectMetaEntry entry,PermissionScope scope);


    void clear(PermissionObject executor);

    void clear(PermissionObject executor,PermissionScope scope);

}
