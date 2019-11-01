/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object.search;

import net.pretronic.dkperms.api.object.PermissionObject;

import java.util.Collection;

public interface ObjectSearchResult extends Iterable<PermissionObject> {

    int getCount();

    PermissionObject getFirst();

    PermissionObject getLast();

    PermissionObject get(int index);

    PermissionObject get(int fromIndex, int toIndex);

    Collection<PermissionObject> getAll();

}
