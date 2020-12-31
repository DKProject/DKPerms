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

import net.pretronic.libraries.utility.annonations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The PermissionScope is used to group permissions, objects and metas in different validity areas.
 *
 * <p>The DKPerms Scope technology is based on a tree structure, every scope has everything
 * from the parent scope and so on. Scopes are custom defined and registered, a permission
 * is only valid, when the Scope of this permission is the same then the current scope or a parent scope.</p>
 *
 */
public interface PermissionScope {

    String NAMESPACE = "Namespace";

    int getId();

    int getLevel();

    String getKey();

    String getName();

    String getPath();


    @Nullable
    PermissionScope getParent();

    PermissionScope getChild(String key, String name);

    CompletableFuture<PermissionScope> getChildAsync(String key, String name);

    List<PermissionScope> getChildren();

    CompletableFuture<List<PermissionScope>> getChildrenAsync();

    boolean contains(PermissionScope innerScope);

    boolean areChildrenLoaded();

    boolean isSaved();

    void insert();

    //void delete(); Deletion of scopes is dangerous

    //CompletableFuture<Void> deleteAsync();

    void rename(String key, String name);

    CompletableFuture<Void> renameAsync(String key, String name);

    void move(PermissionScope parent);

    CompletableFuture<Void> moveAsync(PermissionScope parent);

}
