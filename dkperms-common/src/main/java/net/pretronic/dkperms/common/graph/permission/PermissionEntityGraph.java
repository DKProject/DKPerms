/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 19.01.20, 13:36
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.graph.permission;

import net.pretronic.dkperms.api.entity.PermissionEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.PermissionGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;
import net.pretronic.dkperms.common.calculator.PermissionCalculator;

import java.util.ArrayList;
import java.util.List;
//@Todo cache
public class PermissionEntityGraph implements PermissionGraph {

    private final PermissionObject object;
    private final Graph<PermissionScope> scopes;
    private final Graph<PermissionObject> groups;

    public PermissionEntityGraph(PermissionObject object, Graph<PermissionScope> scopes, Graph<PermissionObject> groups) {
        this.object = object;//@Todo add in direct graph
        this.scopes = scopes;
        this.groups = groups;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PermissionEntity> traverse() {
        List<PermissionEntity> result = new ArrayList<>();
        List<PermissionObject> groups  = this.groups.traverse();

        ScopeBasedDataList<PermissionEntity>[] dataList = new ScopeBasedDataList[groups.size()+1];

        dataList[0] = object.getPermissions(scopes);
        int index = 1;
        for (PermissionObject group : groups) {
            dataList[index++] = group.getPermissions(scopes);
        }

        for (PermissionScope scope : scopes.traverse()) {
            for (ScopeBasedDataList<PermissionEntity> data : dataList) {
                result.addAll(data.getData(scope));
            }
        }
        return result;
    }

    @Override
    public boolean containsPermission(String permission) {
        return PermissionCalculator.containsPermission(this,permission);
    }

    @Override
    public PermissionAction calculatePermission(String permission) {
        return PermissionCalculator.calculate(traverse(),permission);
    }

    @Override
    public List<String> toStringList() {
        return PermissionCalculator.toStringList(this);
    }
}
