/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object;

import net.pretronic.dkperms.api.logging.Loggable;

import java.util.concurrent.CompletableFuture;

public interface PermissionObjectType extends Loggable {

    PermissionObjectType SERVICE_ACCOUNT = new BuiltInPermissionObjectType(1,"BUILTIN/SERVICE_ACCOUNT","service",false);

    PermissionObjectType USER_ACCOUNT = new BuiltInPermissionObjectType(2,"BUILTIN/USER_ACCOUNT","user",false);

    PermissionObjectType GROUP = new BuiltInPermissionObjectType(3,"BUILTIN/GROUP","group",true);


    int getId();

    String getName();

    String getDisplayName();

    void rename(PermissionObject executor,String name, String displayName);

    CompletableFuture<Void> renameAsync(PermissionObject executor,String name, String displayName);

    boolean isParentAble();

    boolean hasLocalHolderFactory();

    PermissionHolderFactory getLocalHolderFactory();

    void setLocalHolderFactory(PermissionHolderFactory factory);


    default void checkParentAble(){
        if(!isParentAble()) throw new IllegalArgumentException("Object "+getName()+" is not a group object");
    }

}
