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

import net.pretronic.dkperms.api.context.PermissionContext;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.object.search.sort.SortColumn;
import net.pretronic.dkperms.api.object.search.sort.SortOrder;

public interface ObjectSearcher {

    ObjectSearcher inheritance();

    ObjectSearcher inScope(PermissionScope... scope);


    ObjectSearcher hasPermission(String permission);

    ObjectSearcher hasPermission(String permission,PermissionScope scope);

    ObjectSearcher hasPermissionNot(String permission);

    ObjectSearcher hasPermissionNot(String permission,PermissionScope scope);


    ObjectSearcher hasGroup(PermissionObject group);

    ObjectSearcher hasGroup(PermissionObject group,PermissionContext... contexts);

    ObjectSearcher hasGroup(PermissionObject group,PermissionScope scope);

    ObjectSearcher hasGroup(PermissionObject group, PermissionScope scope, PermissionContext... contexts);


    ObjectSearcher hasContext(PermissionContext context);

    ObjectSearcher hasContext(PermissionContext context, PermissionScope scope);

    ObjectSearcher hasContext(PermissionContext context, Object value);

    ObjectSearcher hasContext(PermissionContext context, Object value, PermissionScope scope);


    ObjectSearcher hasMeta(String key);

    ObjectSearcher hasMeta(String key,PermissionScope scope);

    ObjectSearcher hasMeta(String key, String value);

    ObjectSearcher hasMeta(String key, String value, PermissionScope scope);


    ObjectSearcher sortBy(SortColumn column,SortOrder order);

    ObjectSearcher sortB(String metaName,Object value,SortOrder order);


    ObjectSearchResult search();
}
