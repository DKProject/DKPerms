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


    PermissionContext createContext(String key);

    CompletableFuture<PermissionContext> createContextAsync(String key);


    boolean deleteContext(int id);

    boolean deleteContext(String name);

    boolean deleteContext(PermissionContext context);

    CompletableFuture<Boolean> deleteContextAsync(int id);

    CompletableFuture<Boolean> deleteContextAsync(String name);

    CompletableFuture<Boolean> deleteContextAsync(PermissionContext context);

}
