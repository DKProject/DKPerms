/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 30.05.20, 20:38
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.permission.analyse;

import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;

public interface PermissionRequest {

    long getTime();

    PermissionObject getTarget();

    PermissionGraph getGraph();

    Graph<PermissionScope> getScopeRange();

    String getPermission();

    PermissionAction getResult();

    StackTraceElement[] getStackTrace();

    PermissionAnalyseResult<PermissionEntity> track();

}
