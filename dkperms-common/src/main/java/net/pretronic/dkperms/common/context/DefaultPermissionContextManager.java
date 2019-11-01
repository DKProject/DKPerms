/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.11.19, 21:09
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.context;

import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.context.PermissionContextManager;

import java.util.concurrent.CompletableFuture;

public class DefaultPermissionContextManager implements PermissionContextManager {

    @Override
    public PermissionContext getContext(int id) {
        return null;
    }

    @Override
    public PermissionContext getContext(String name) {
        return null;
    }

    @Override
    public PermissionContext getOrCreateContext(String name) {
        return null;
    }

    @Override
    public CompletableFuture<PermissionContext> getContextAsync(int id) {
        return null;
    }

    @Override
    public CompletableFuture<PermissionContext> getContextAsync(String name) {
        return null;
    }

    @Override
    public CompletableFuture<PermissionContext> getOrCreateContextAsync(String name) {
        return null;
    }

    @Override
    public PermissionContext createContext(String key) {
        return null;
    }

    @Override
    public CompletableFuture<PermissionContext> createContextAsync(String key) {
        return null;
    }

    @Override
    public boolean deleteContext(int id) {
        return false;
    }

    @Override
    public boolean deleteContext(String name) {
        return false;
    }

    @Override
    public boolean deleteContext(PermissionContext context) {
        return false;
    }

    @Override
    public CompletableFuture<Boolean> deleteContextAsync(int id) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> deleteContextAsync(String name) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> deleteContextAsync(PermissionContext context) {
        return null;
    }
}
