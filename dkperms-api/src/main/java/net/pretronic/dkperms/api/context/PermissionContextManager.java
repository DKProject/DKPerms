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

public interface PermissionContextManager {

    PermissionContext getContext(int id);

    PermissionContext getContext(String name);

    PermissionContext getOrCreateContext(String name);

    CompletableFuture<PermissionContext> getContextAsync(int id);

    CompletableFuture<PermissionContext> getContextAsync(String name);

    CompletableFuture<PermissionContext> getOrCreateContextAsync(String name);


    boolean hasContext(String name);

    CompletableFuture<Boolean> hasContextAsync(String name);


    PermissionContext createContext(String name);

    CompletableFuture<PermissionContext> createContextAsync(String name);


    void deleteContext(int id);

    void deleteContext(String name);

    void deleteContext(PermissionContext context);

    CompletableFuture<Void> deleteContextAsync(int id);

    CompletableFuture<Void> deleteContextAsync(String name);

    CompletableFuture<Void> deleteContextAsync(PermissionContext context);

}
