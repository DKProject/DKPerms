/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 09.11.19, 20:18
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.storage;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.QueryGroup;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectTrack;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.storage.TrackStorage;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectManager;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectTrack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PDQTrackStorage implements TrackStorage {

    private DatabaseCollection tracks;
    private DatabaseCollection track_assignments;

    @Override
    public Collection<PermissionObjectTrack> getTracks() {
        QueryResult result = tracks.find().execute();
        Collection<PermissionObjectTrack> tracks = new ArrayList<>();
        for (QueryResultEntry entry : result) {
            tracks.add(createTrack(entry));
        }
        return tracks;
    }

    @Override
    public Collection<PermissionObjectTrack> getTracks(PermissionScope scope) {
        QueryResult result = tracks.find().where("ScopeId",scope.getId()).execute();
        Collection<PermissionObjectTrack> tracks = new ArrayList<>();
        for (QueryResultEntry entry : result) {
            tracks.add(createTrack(entry));
        }
        return tracks;
    }

    @Override
    public PermissionObjectTrack getTrack(int id) {
        QueryResult result = tracks.find().where("Id",id).limit(1).execute();
        if(!result.isEmpty()) return createTrack(result.first());
        return null;
    }

    @Override
    public PermissionObjectTrack getTrack(String name) {
        QueryResult result = tracks.find().where("Name",name).limit(1).execute();
        if(!result.isEmpty()) return createTrack(result.first());
        return null;
    }

    private PermissionObjectTrack createTrack(QueryResultEntry entry){
        return new DefaultPermissionObjectTrack(entry.getInt("Id")
                ,entry.getString("Name")
                ,DKPerms.getInstance().getScopeManager().getScope(entry.getInt("ScopeId"))
                ,loadGroups(entry.getInt("Id")));
    }

    private List<PermissionObject> loadGroups(int trackId){
        List<PermissionObject> objects = new ArrayList<>();
        QueryResult result = this.track_assignments.find().where("TrackId",trackId).execute();
        for (QueryResultEntry entry : result) {
            int id = entry.getInt("Id");
            PermissionObject object = getObjectManager().getObjects().get("ByIdOnlyCached",id);
            if(object == null) {
                object = new DefaultPermissionObject(id
                        ,entry.getUniqueId("AssignmentId")
                        ,entry.getString("Name")
                        ,entry.getBoolean("Disabled")
                        ,entry.getInt("Priority")
                        ,DKPerms.getInstance().getObjectManager().getType(entry.getInt("TypeId"))
                        ,DKPerms.getInstance().getScopeManager().getScope(entry.getInt("ScopeId")));
                getObjectManager().getObjects().insert(object);
            }
            objects.add(object);
        }
        return objects;
    }

    private DefaultPermissionObjectManager getObjectManager(){
        return (DefaultPermissionObjectManager) DKPerms.getInstance().getObjectManager();
    }

    @Override
    public int createTrack(String name, int scopeId) {
        return tracks.insert()
                .set("Name",name)
                .set("ScopeId",scopeId)
                .executeAndGetGeneratedKeyAsInt("Id");
    }

    @Override
    public void updateTrackName(int id, String name) {
        this.tracks.update()
                .set("Name",name)
                .where("Id",id)
                .execute();
    }

    @Override
    public void updateTrackScope(int id, int scopeId) {
        this.tracks.update()
                .set("ScopeId",scopeId)
                .where("Id",id)
                .execute();
    }

    @Override
    public void deleteTrack(int id) {
        this.tracks.delete()
                .where("Id",id)
                .execute();
    }

    @Override
    public void addGroup(int trackId, int groupId, int position) {
        this.track_assignments.insert()
                .set("TrackId",trackId)
                .set("ObjectId",groupId)
                .set("Index",position)
                .execute();
    }

    @Override
    public void updateIndex(int trackId,int objectId, int index) {
        this.track_assignments.update()
                .set("Index",index)
                .where("TrackId",trackId)
                .where("ObjectId",objectId)
                .execute();
    }

    @Override
    public void removeGroup(int trackId, int groupId) {
        this.track_assignments.delete()
                .where("TrackId",trackId)
                .where("GroupId",groupId)
                .execute();
    }

    @Override
    public void removeGroup(int id) {
        this.track_assignments.delete()
                .where("Id",id)
                .execute();
    }

    @Override
    public void shiftGroup(int trackId, List<PermissionObject> groups, int start, int amount) {
        QueryGroup group = track_assignments.group();
        for (int i = 0; i < groups.size(); i++) {
            if(i >= start){
                group.add(track_assignments.update()
                        .set("Index",i+amount)
                        .where("TrackId",trackId)
                        .where("GroupId",groups.get(i).getId()));
            }
        }
        group.execute();
    }

    public void setCollections(DatabaseCollection tracks, DatabaseCollection track_assignments){
        this.tracks = tracks;
        this.track_assignments = track_assignments;
    }
}
