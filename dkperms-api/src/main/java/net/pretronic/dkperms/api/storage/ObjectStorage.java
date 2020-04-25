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

import net.pretronic.dkperms.api.object.search.ObjectSearchQuery;
import net.pretronic.dkperms.api.object.search.ObjectSearchResult;
import net.pretronic.libraries.utility.map.Pair;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.meta.ObjectMeta;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface ObjectStorage {

    Collection<PermissionObjectType> getObjectTypes();

    PermissionObjectType getObjectType(int typeId);

    PermissionObjectType getObjectType(String name);

    int createObjectType(String name, boolean group);

    void updateObjectType(int typeId, String name);

    void deleteObjectType(int typeId);

    void deleteObjectType(String name);


    PermissionObject getObject(int id);

    PermissionObject getObjectByAssignment(UUID id);

    PermissionObject getObject(String name, PermissionScope scope, PermissionObjectType type);


    ObjectSearchQuery createSearchQuery();


    Collection<PermissionObject> getObjects(String name, PermissionObjectType type,Collection<PermissionObject> skipp);

    PermissionObject createObject(PermissionScope scope,PermissionObjectType type, String name, UUID assignmentId);

    void updateObjectName(int objectId, String name);

    void updateObjectType(int objectId, PermissionObjectType type);

    void updateObjectScope(int objectId, PermissionScope scope);

    void updateObjectPriority(int objectId, int priority);

    void updateObjectDisabled(int objectId, boolean disabled);

    void deleteObject(int objectId);




    Collection<ObjectMetaEntry> getMetaEntries(PermissionObject object, PermissionScope scopes);

    ScopeBasedDataList<ObjectMetaEntry> getMetaEntries(PermissionObject object, Collection<PermissionScope> scopes);

    ScopeBasedDataList<ObjectMetaEntry> getAllMetaEntries(PermissionObject object, Collection<PermissionScope> skipped);


    int insertMeta(int objectId,int scopeId, String key, String value);

    void updateMeta(int metaId, String value);

    void deleteMetaEntry(int objectId, String key, int scopeId);

    void deleteMetaEntry(int entryId);

    void clearMeta(int objectId);

    void clearMeta(int objectId, int scopeId);
}
