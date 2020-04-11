/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.permission.analyse;

import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;
import java.util.function.Consumer;

public interface PermissionAnalyse {

    int getId();

    void setIncludedPermissions(String... permissions);

    void setExcludedPermissions(String... permissions);

    PermissionScope getScope();

    void setScope(PermissionScope scope);

    Collection<PermissionRequest> getRequests();

    void setRequestListener(Consumer<PermissionRequest> request);

    boolean stop();

}
