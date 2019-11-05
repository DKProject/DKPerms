/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 02.11.19, 13:24
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.storage;

import net.pretronic.dkperms.api.context.PermissionContext;

public interface ContextStorage {

    PermissionContext getContext(int id);

    PermissionContext getContext(String name);

    int createContext(String name);

    void updateContextName(int contextId, String name);

    void deleteContext(int id);

    void deleteContext(String name);

}
