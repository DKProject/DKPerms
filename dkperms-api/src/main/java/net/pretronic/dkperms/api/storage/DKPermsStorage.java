/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.storage;

public interface DKPermsStorage{

    String getName();

    AuditLogStorage getAuditLogStorage();

    ScopeStorage getScopeStorage();

    ObjectStorage getObjectStorage();

    ParentStorage getParentStorage();

    PermissionStorage getPermissionStorage();

    TrackStorage getTrackStorage();


    default void deleteTimedOutEntries(){
        getParentStorage().deleteTimedOutParentReferences();
        getObjectStorage().deleteTimedOutMetaEntries();
        getPermissionStorage().deleteTimedOutPermissions();
    }


}
