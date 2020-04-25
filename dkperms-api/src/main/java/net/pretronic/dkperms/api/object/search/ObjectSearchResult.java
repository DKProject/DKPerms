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
import net.pretronic.dkperms.api.object.holder.PermissionObjectHolder;

import java.util.Collection;

public interface ObjectSearchResult extends Iterable<PermissionObject> {

    ObjectSearchQuery getQuery();

    int getCount();

    PermissionObject getFirst();

    PermissionObject getFirstOrNull();

    PermissionObject getLast();

    PermissionObject getLastOrNull();

    PermissionObject get(int index);

    Collection<PermissionObject> get(int fromIndex, int toIndex);


    Collection<PermissionObject> getAll();


    Collection<PermissionObjectHolder> getAllHolders();

    <T extends PermissionObjectHolder> Collection<T> getAllHolders(Class<T> holderClass);

}
