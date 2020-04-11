/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 19.01.20, 13:33
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.graph.group;

import net.pretronic.dkperms.api.entity.PermissionGroupEntity;
import net.pretronic.dkperms.api.graph.Graph;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.permission.PermissionAction;

import java.util.ArrayList;
import java.util.List;

public class GroupGraph implements Graph<PermissionObject> {

    private final Graph<PermissionGroupEntity> inheritanceGraph;

    public GroupGraph(Graph<PermissionGroupEntity> inheritanceGraph) {
        this.inheritanceGraph = inheritanceGraph;
    }

    @Override
    public List<PermissionObject> traverse() {
        List<PermissionObject> blocked = new ArrayList<>();
        List<PermissionObject> result = new ArrayList<>();
        List<PermissionGroupEntity> inheritance = inheritanceGraph.traverse();

        for (PermissionGroupEntity entity : inheritance) {
            if(!blocked.contains(entity.getGroup())){
                if(entity.getAction() == PermissionAction.ALLOW){
                    if(!result.contains(entity.getGroup())) result.add(entity.getGroup());
                }else if(entity.getAction() == PermissionAction.REJECT){
                    result.remove(entity.getGroup());
                }else if(entity.getAction() == PermissionAction.ALLOW_ALWAYS){
                    if(!result.contains(entity.getGroup())) result.add(entity.getGroup());
                    blocked.add(entity.getGroup());
                }else if(entity.getAction() == PermissionAction.REJECT_ALWAYS){
                    result.remove(entity.getGroup());
                    blocked.add(entity.getGroup());
                }
            }
        }

        return result;
    }
}
