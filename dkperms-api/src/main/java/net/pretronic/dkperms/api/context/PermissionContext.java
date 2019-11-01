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

public interface PermissionContext {

    int getId();

    String getName();

    void rename(String name);

    PermissionContextAssignment assign(Object value);

    void delete();

}
