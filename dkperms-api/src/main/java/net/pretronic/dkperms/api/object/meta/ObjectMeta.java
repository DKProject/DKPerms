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

    ObjectMetaEntry get(String key);//From root

    ObjectMetaEntry get(PermissionScope scope, String key);

    ObjectMetaEntry getInheritance(PermissionScope scope, String key);//

    ObjectMeta getEffective(PermissionScope scope, String key);


    Collection<ObjectMetaEntry> getEntries();

    Collection<ObjectMetaEntry> getEntries(PermissionScope scope);

    Collection<ObjectMetaEntry> getInheritanceEntries(PermissionScope scope);

    Collection<ObjectMetaEntry> getEffectiveEntries(PermissionScope scope, String key);


    boolean contains(String key);

    boolean contains(PermissionScope scope,String key);

    boolean containsInheritance(PermissionScope scope, String key);

    boolean containsEffective(PermissionScope scope,String key);


    ObjectMetaEntry set(String key, Object value);//to root

    ObjectMetaEntry set(String key, Object value,PermissionScope scope);

    void unset(String key);//from root

    void unset(String key,PermissionScope scope);


    void clear();

    void clear(PermissionScope scope);

}
