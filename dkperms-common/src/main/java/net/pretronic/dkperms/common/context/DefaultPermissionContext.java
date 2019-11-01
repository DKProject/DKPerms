/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.context;

import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.context.PermissionContextAssignment;
import net.pretronic.dkperms.api.storage.DKPermsStorage;

public class DefaultPermissionContext implements PermissionContext {

    private final DKPermsStorage storage;
    private final int id;
    private String name;

    public DefaultPermissionContext(DKPermsStorage storage, int id, String name) {
        this.storage = storage;
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

    }

    @Override
    public void delete() {
        //this.storage
    }

    @Override
    public PermissionContextAssignment assign(Object valueDKPerms) {
        return null;
    }
}
