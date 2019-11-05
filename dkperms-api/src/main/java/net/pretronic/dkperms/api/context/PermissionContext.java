/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.context;

import java.util.concurrent.CompletableFuture;

/**
 * The PermissionContext is an addition to the scope and is used for setting condition for a permission.
 * A permission is only valid, when the placer has the same context value.
 */
public interface PermissionContext {

    int getId();

    String getName();

    void rename(String name);

    CompletableFuture<Void> renameAsync(String name);

    PermissionContextAssignment assign(Object value);

    void delete();

    CompletableFuture<Void> deleteAsync();

}
