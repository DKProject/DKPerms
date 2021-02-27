/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 05.11.19, 18:36
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.storage;

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectTrack;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Collection;
import java.util.List;

public interface TrackStorage {

    Collection<PermissionObjectTrack> getTracks();

    Collection<PermissionObjectTrack> getTracks(PermissionScope scope);


    PermissionObjectTrack getTrack(int id);

    PermissionObjectTrack getTrack(String name);


    int createTrack(String name,int scopeId);

    void updateTrackName(int id, String name);

    void updateTrackScope(int id, int scopeId);

    void clearTrack(int id);

    void deleteTrack(int id);


    void addGroup(int trackId, int groupId, int index);

    void updateIndex(int trackId,int objectId, int index);

    void removeGroup(int trackId, int groupId);

    void removeGroup(int id);

    void shiftGroup(int trackId, List<PermissionObject> groups, int start, int amount);

}
