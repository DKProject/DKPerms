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

import net.pretronic.dkperms.api.object.holder.PermissionHolderFactory;

import java.util.concurrent.CompletableFuture;

public interface PermissionObjectType {

    int getId();

    String getName();

    void rename(String name);

    CompletableFuture<Void> renameAsync(String name);

    boolean isGroup();


    PermissionHolderFactory getLocalHolderFactory();

    void setLocalHolderFactory(PermissionHolderFactory factory);


    default void checkIsGroup(){
        if(!isGroup()) throw new IllegalArgumentException("Object "+getName()+" is not a group object");
    }

}
