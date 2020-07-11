/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 30.05.20, 20:50
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.permission;

import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyseResult;
import net.pretronic.dkperms.api.permission.analyse.PermissionRequest;
import net.pretronic.dkperms.api.scope.PermissionScope;

public class DefaultPermissionRequest implements PermissionRequest {

    private final long time;
    private final PermissionObject target;
    private final PermissionGraph graph;
    private final Graph<PermissionScope> scopeRange;
    private final String permission;
    private final PermissionAction action;
    private final StackTraceElement[] stackTrace;

    public DefaultPermissionRequest(PermissionObject target, PermissionGraph graph, Graph<PermissionScope> scopeRange
            , String permission, PermissionAction action, StackTraceElement[] stackTrace) {
        this.time = System.currentTimeMillis();
        this.target = target;
        this.graph = graph;
        this.scopeRange = scopeRange;
        this.permission = permission;
        this.action = action;
        this.stackTrace = stackTrace;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public PermissionObject getTarget() {
        return target;
    }

    @Override
    public PermissionGraph getGraph() {
        return graph;
    }

    @Override
    public Graph<PermissionScope> getScopeRange() {
        return scopeRange;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public PermissionAction getResult() {
        return action;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    @Override
    public PermissionAnalyseResult<PermissionEntity> track() {
        return graph.analyze(permission);
    }
}
