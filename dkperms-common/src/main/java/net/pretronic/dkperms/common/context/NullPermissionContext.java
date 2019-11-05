/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 02.11.19, 12:07
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.context;

import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.context.PermissionContextAssignment;

import java.util.concurrent.CompletableFuture;

public final class NullPermissionContext implements PermissionContext {

    private final int id;
    private final String name;

    NullPermissionContext(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void rename(String name) {
        throw new IllegalArgumentException("This context is null");
    }

    @Override
    public CompletableFuture<Void> renameAsync(String name) {
        throw new IllegalArgumentException("This context is null");
    }

    @Override
    public PermissionContextAssignment assign(Object value) {
        throw new IllegalArgumentException("This context is null");
    }

    @Override
    public void delete() {
        throw new IllegalArgumentException("This context is null");
    }

    @Override
    public CompletableFuture<Void> deleteAsync() {
        throw new IllegalArgumentException("This context is null");
    }
}
