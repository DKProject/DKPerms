/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 19.01.20, 13:34
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.graph.group;

import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.scope.data.ScopeBasedData;
import net.pretronic.dkperms.api.scope.data.ScopeBasedDataList;

import java.util.*;

//@Todo implement caching and optimize with entries
public final class GroupEntityGraph implements Graph<PermissionGroupEntity> {

    private final PermissionObject object;
    private final Graph<PermissionScope> scopes;
    private final boolean subGroups;

    private final List<PermissionGroupEntity> entities;
    private final Map<PermissionObject,Integer> objectPriority;

    public GroupEntityGraph(PermissionObject object, Graph<PermissionScope> scopes, boolean subGroups) {
        this.object = object;
        this.scopes = scopes;
        this.subGroups = subGroups;

        this.entities = new ArrayList<>();
        this.objectPriority = new HashMap<>();
    }

    @Override
    public List<PermissionGroupEntity> traverse() {
        this.objectPriority.put(object,0);
        findNextGroups(object,1);

        List<PermissionGroupEntity> entities = this.entities;
        entities.sort((o1, o2) -> {
            int result = Integer.compare(o1.getScope().getLevel(),o2.getScope().getLevel());
            if(result == 0){
                result = Integer.compare(objectPriority.get(o2.getGroup()),objectPriority.get(o1.getGroup()));
            }
            return result;
        });
        return entities;
    }

    private void findNextGroups(PermissionObject object, int level){
        ScopeBasedDataList<PermissionGroupEntity> dataList = object.getGroups(scopes);
        for (ScopeBasedData<PermissionGroupEntity> groupData : dataList) {
            for (PermissionGroupEntity group : groupData.getData()) {
                if(!entities.contains(group)){
                    entities.add(group);
                    objectPriority.put(group.getGroup(),level);
                    if(subGroups) findNextGroups(group.getGroup(),level+1);
                }
            }
        }
    }
}
