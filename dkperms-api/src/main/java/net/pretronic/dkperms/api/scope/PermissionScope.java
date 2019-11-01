/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.scope;

import net.prematic.libraries.utility.annonations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PermissionScope {

    String NAMESPACE = "Namespace";

    int getId();

    int getLevel();

    String getKey();

    String getName();


    @Nullable
    PermissionScope getParent();

    PermissionScope getChild(String key, String name);

    CompletableFuture<PermissionScope> getChildAsync(String key, String name);

    List<PermissionScope> getChildren();

    CompletableFuture<List<PermissionScope>> getChildrenAsync();


    boolean isSaved();

    void insert();

    void delete();

    void rename(String key, String name);

    void move(PermissionScope parent);

}
