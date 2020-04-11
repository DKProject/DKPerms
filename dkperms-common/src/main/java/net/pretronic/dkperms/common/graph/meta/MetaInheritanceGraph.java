/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 19.01.20, 13:36
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.graph.meta;

import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.graph.ObjectMetaGraph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.util.*;

public class MetaInheritanceGraph implements ObjectMetaGraph {

    private final PermissionObject object;
    private final Graph<PermissionScope> scopes;
    private final Graph<PermissionObject> groups;

    public MetaInheritanceGraph(PermissionObject object, Graph<PermissionScope> scopes,Graph<PermissionObject> groups) {
        this.object = object;
        this.scopes = scopes;
        this.groups = groups;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectMetaEntry> traverse() {
        List<ObjectMetaEntry> result = new ArrayList<>();
        List<PermissionObject> groups  = this.groups.traverse();

        ScopeBasedDataList<ObjectMetaEntry>[] dataList = new ScopeBasedDataList[groups.size()+1];

        dataList[0] = object.getMeta().getEntries(scopes);
        int index = 1;
        for (PermissionObject group : groups) {
            dataList[index++] = group.getMeta().getEntries(scopes);
        }

        for (PermissionScope scope : scopes.traverse()) {
            for (ScopeBasedDataList<ObjectMetaEntry> data : dataList) {
                result.addAll(data.getData(scope));
            }
        }
        return result;
    }

    @Override
    public ObjectMetaEntry calculate(String key) {
        List<ObjectMetaEntry> entries = traverse();
        ObjectMetaEntry result = null;
        for (ObjectMetaEntry entry : entries) {
            if(entry.getKey().equalsIgnoreCase(key)){
                result = entry;
            }
        }
        return result;
    }

    @Override
    public boolean isSet(String key) {
        return calculate(key) != null;
    }

    @Override
    public boolean isSet(String key, Object value) {
        ObjectMetaEntry entry = calculate(key);
        return entry != null && entry.equalsValue(value);
    }
}
