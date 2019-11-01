/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.object.meta;

import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;

public interface ObjectMeta extends Iterable<ObjectMetaEntry> {

    ObjectMetaEntry get(String key);

    ObjectMetaEntry get(PermissionScope scope, String key);

    ObjectMetaEntry getInheritance(PermissionScope scope, String key);


    Collection<ObjectMetaEntry> getEntries();

    Collection<ObjectMetaEntry> getEntries(PermissionScope scope);

    Collection<ObjectMetaEntry> getInheritanceEntries(PermissionScope scope);


    void set(String key, Object value);

    void set(String key, Object value,PermissionScope scope);

    void set(ObjectMetaEntry entry);


    boolean contains(String key);

    boolean contains(String key,PermissionScope scope);

    boolean containsInheritance(String key,PermissionScope scope);


    boolean clear();

    boolean clear(PermissionScope scope);

}
