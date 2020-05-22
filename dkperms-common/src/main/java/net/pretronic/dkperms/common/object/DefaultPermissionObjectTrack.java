/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 10.05.20, 20:21
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.object;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObjectTrack;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.libraries.message.bml.variable.describer.VariableObjectToString;
import net.pretronic.libraries.utility.Validate;

import java.util.List;
import java.util.ListIterator;

public class DefaultPermissionObjectTrack implements PermissionObjectTrack, VariableObjectToString {

    private final int id;
    private String name;
    private PermissionScope scope;
    private final List<PermissionObject> groups;

    public DefaultPermissionObjectTrack(int id, String name, PermissionScope scope, List<PermissionObject> groups) {
        Validate.notNull(scope,name,groups);
        this.id = id;
        this.name = name;
        this.scope = scope;
        this.groups = groups;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        Validate.notNull(name);
        DKPerms.getInstance().getStorage().getTrackStorage().updateTrackName(id,name);
        this.name = name;
    }

    @Override
    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public void setScope(PermissionScope scope) {
        Validate.notNull(scope);
        DKPerms.getInstance().getStorage().getTrackStorage().updateTrackScope(id,scope.getId());
        this.scope = scope;
    }

    @Override
    public int getPosition(PermissionObject object) {
        Validate.notNull(object);
        return this.groups.indexOf(object);
    }

    @Override
    public void addBefore(PermissionObject object) {
        Validate.notNull(object);
        if(contains(object)) throw new IllegalArgumentException("This group is already contained in this track");
        DKPerms.getInstance().getStorage().getTrackStorage().shiftGroup(id,groups,0,1);
        DKPerms.getInstance().getStorage().getTrackStorage().addGroup(id,object.getId(),this.groups.size());
        this.groups.add(0,object);
    }

    @Override
    public void addAfter(PermissionObject object) {
        Validate.notNull(object);
        if(contains(object)) throw new IllegalArgumentException("This group is already contained in this track");
        DKPerms.getInstance().getStorage().getTrackStorage().addGroup(id,object.getId(),this.groups.size());
        this.groups.add(object);
    }

    @Override
    public void addGroup(PermissionObject object, int position) {
        Validate.notNull(object,position);
        if(contains(object)) throw new IllegalArgumentException("This group is already contained in this track");
        if(position >= groups.size()){
            addAfter(object);
        }else{
            DKPerms.getInstance().getStorage().getTrackStorage().shiftGroup(id,groups,position,1);
            DKPerms.getInstance().getStorage().getTrackStorage().addGroup(id,object.getId(),this.groups.size());
            this.groups.add(position,object);
        }
    }

    @Override
    public void removeGroup(PermissionObject object) {
        Validate.notNull(object);
        int index = getPosition(object);
        if(index >= 0){
            DKPerms.getInstance().getStorage().getTrackStorage().shiftGroup(id,groups,index,-1);
            DKPerms.getInstance().getStorage().getTrackStorage().removeGroup(id,object.getId());
            groups.remove(object);
        }
    }

    @Override
    public void clearGroups() {
        this.groups.clear();
    }

    @Override
    public PermissionObject getFirstGroup() {
        if(isEmpty()) return null;
        return groups.get(0);
    }

    @Override
    public PermissionObject getLastGroup() {
        if(isEmpty()) return null;
        return groups.get(groups.size()-1);
    }

    @Override
    public PermissionObject getNextGroup(PermissionObject object) {
        boolean next = false;
        for (PermissionObject group : this.groups) {
            if(next) return group;
            if(group.equals(object)) next = true;
        }
        return null;
    }

    @Override
    public PermissionObject getPreviousGroup(PermissionObject object) {
        ListIterator<PermissionObject> objects = this.groups.listIterator();

        while (objects.hasNext()){
            if(objects.next().equals(object)) return objects.previous();
        }

        return null;
    }

    @Override
    public int size() {
        return groups.size();
    }

    @Override
    public String toStringVariable() {
        return name;
    }
}
