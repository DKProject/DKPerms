/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.05.20, 15:35
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.object;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectTrack;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.List;
import java.util.ListIterator;

public class PriorityObjectTrack implements PermissionObjectTrack {

    private final PermissionScope scope;
    private final List<PermissionObject> groups;

    public PriorityObjectTrack(PermissionScope scope, List<PermissionObject> groups) {
        this.scope = scope;
        this.groups = groups;
    }

    @Override
    public int getId() {
        return -1;
    }

    @Override
    public String getName() {
        return "Priority";
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("A priority track can not be modified");
    }

    @Override
    public PermissionScope getScope() {
        return scope;
    }

    @Override
    public void setScope(PermissionScope scope) {
        throw new UnsupportedOperationException("A priority track can not be modified");
    }

    @Override
    public int getPosition(PermissionObject object) {
        throw new UnsupportedOperationException("A priority track can not be modified");
    }

    @Override
    public void addBefore(PermissionObject object) {
        throw new UnsupportedOperationException("A priority track can not be modified");
    }

    @Override
    public void addAfter(PermissionObject object) {
        throw new UnsupportedOperationException("A priority track can not be modified");
    }

    @Override
    public void addGroup(PermissionObject object, int position) {
        throw new UnsupportedOperationException("A priority track can not be modified");
    }

    @Override
    public void removeGroup(PermissionObject object) {
        throw new UnsupportedOperationException("A priority track can not be modified");
    }

    @Override
    public void clearGroups() {
        throw new UnsupportedOperationException("A priority track can not be modified");
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

}
