/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 03.11.19, 19:07
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.storage;

import net.prematic.libraries.utility.map.Pair;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;

public interface ObjectStorage {

    Collection<PermissionObjectType> getObjectTypes();

    PermissionObjectType getObjectType(int id);

    PermissionObjectType getObjectType(String name);

    int createObjectType(String name);

    void updateObjectType(int typeId, String name);

    void deleteObjectType(int id);

    void deleteObjectType(String name);


    PermissionObject getObject(int id);

    PermissionObject getObject(String name, int scopeId);

    PermissionObject createObject(int scopeId,int objectType, String name);


    ObjectMetaEntry getMeta(int metaId);

    Collection<ObjectMetaEntry> getMetas(int objectId);

    Collection<ObjectMetaEntry> getMetas(int objectId, PermissionScope scopes);

    Collection<Pair<PermissionScope,Collection<ObjectMetaEntry>>> getMetas(int objectId, PermissionScope... scopes);

    Collection<Pair<PermissionScope,Collection<ObjectMetaEntry>>> getMetas(int objectId, Collection<PermissionScope> scopes);

    int insertMeta(int scopeId, int objectId, String key, String value);

    void updateMeta(int metaId, String value);

    void deleteMeta(int objectId, String key, int scopeId);

    void deleteMeta(int metaId);

    void clearMetas(int objectId);

    void clearMetas(int objectId, int scopeId);
}
