/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common.storage;

import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.query.ForeignKey;
import net.pretronic.dkperms.api.storage.*;

public class PDQStorage implements DKPermsStorage {

    private final Database database;

    private final PDQAuditLogStorage auditLogStorage;
    private final PDQScopeStorage scopeStorage;
    private final PDQObjectStorage objectStorage;
    private final PDQParentStorage parentStorage;
    private final PDQPermissionStorage permissionStorage;
    private final PDQTrackStorage trackStorage;

    public PDQStorage(Database database) {
        this.database = database;

        this.auditLogStorage = new PDQAuditLogStorage();
        this.scopeStorage = new PDQScopeStorage();
        this.objectStorage = new PDQObjectStorage();
        this.parentStorage = new PDQParentStorage();
        this.permissionStorage = new PDQPermissionStorage();
        this.trackStorage = new PDQTrackStorage();
        createTables();
    }

    @Override
    public String getName() {
        return database.getDriver().getName()+" ("+database.getDriver().getType()+")";
    }

    @Override
    public AuditLogStorage getAuditLogStorage() {
        return auditLogStorage;
    }

    @Override
    public ScopeStorage getScopeStorage() {
        return scopeStorage;
    }

    @Override
    public ObjectStorage getObjectStorage() {
        return objectStorage;
    }

    @Override
    public ParentStorage getParentStorage() {
        return parentStorage;
    }

    @Override
    public PermissionStorage getPermissionStorage() {
        return permissionStorage;
    }

    @Override
    public TrackStorage getTrackStorage() {
        return trackStorage;
    }

    private void createTables(){
        DatabaseCollection scope = database.createCollection("dkperms_scope")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL,FieldOption.INDEX)
                .field("ParentId",DataType.INTEGER, ForeignKey.of(database.getName(),"dkperms_scope","Id", ForeignKey.Option.CASCADE),FieldOption.INDEX)
                .field("Key",DataType.STRING,64,FieldOption.NOT_NULL)
                .field("Name",DataType.STRING,64,FieldOption.NOT_NULL)
                .create();

        DatabaseCollection object_type = database.createCollection("dkperms_object_type")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL)
                .field("Name",DataType.STRING,64,FieldOption.NOT_NULL,FieldOption.UNIQUE)
                .field("DisplayName",DataType.STRING,64,FieldOption.NOT_NULL)
                .field("IsParentAble",DataType.BOOLEAN,FieldOption.NOT_NULL)
                .create();

        DatabaseCollection object = database.createCollection("dkperms_object")//@Todo check inconsistent with displayName and description
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL,FieldOption.INDEX)
                .field("AssignmentId", DataType.UUID,FieldOption.INDEX)
                .field("Name",DataType.STRING,64,FieldOption.NOT_NULL,FieldOption.INDEX)
                .field("Priority",DataType.INTEGER,FieldOption.NOT_NULL)
                .field("TypeId",DataType.INTEGER,ForeignKey.of(object_type,"Id"),FieldOption.NOT_NULL)
                .field("ScopeId",DataType.INTEGER,ForeignKey.of(scope,"Id"),FieldOption.NOT_NULL)
                .field("Disabled",DataType.BOOLEAN,FieldOption.NOT_NULL)
                .field("DisplayName",DataType.STRING,64,FieldOption.NOT_NULL)
                .create();

        DatabaseCollection object_meta = database.createCollection("dkperms_object_meta")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL)
                .field("ObjectId",DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("ScopeId",DataType.INTEGER,ForeignKey.of(scope,"Id"),FieldOption.NOT_NULL)
                .field("Key",DataType.STRING,FieldOption.NOT_NULL)
                .field("Value",DataType.STRING,FieldOption.NOT_NULL)
                .field("Priority",DataType.INTEGER,FieldOption.NOT_NULL)
                .field("Timeout",DataType.LONG,FieldOption.NOT_NULL)
                .create();

        DatabaseCollection object_groups = database.createCollection("dkperms_object_parents")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL)
                .field("ObjectId",DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("ScopeId",DataType.INTEGER,ForeignKey.of(scope,"Id"),FieldOption.NOT_NULL)
                .field("ParentId",DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("Action",DataType.INTEGER,FieldOption.NOT_NULL)
                .field("Timeout",DataType.LONG,FieldOption.NOT_NULL)
                .create();

        DatabaseCollection object_permissions = database.createCollection("dkperms_object_permissions")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL)
                .field("ObjectId",DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("ScopeId",DataType.INTEGER,ForeignKey.of(scope,"Id"),FieldOption.NOT_NULL)
                .field("Permission",DataType.STRING,FieldOption.NOT_NULL)
                .field("Action",DataType.INTEGER,FieldOption.NOT_NULL)
                .field("Timeout",DataType.LONG,FieldOption.NOT_NULL)
                .create();

        DatabaseCollection auditLog = database.createCollection("dkperms_auditlog")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL)
                .field("Time",DataType.INTEGER,FieldOption.NOT_NULL)
                .field("ExecutorId", DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("Type",DataType.STRING,20,FieldOption.NOT_NULL)
                .field("Action",DataType.STRING,20,FieldOption.NOT_NULL)
                .field("OwnerId",DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("ObjectId",DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("ScopeId",DataType.INTEGER,ForeignKey.of(scope,"Id"),FieldOption.NOT_NULL)
                .field("Field",DataType.STRING,32,FieldOption.NOT_NULL)
                .field("OldValue",DataType.STRING,500,FieldOption.NOT_NULL)
                .field("NewValue",DataType.STRING,500,FieldOption.NOT_NULL)
                .field("Data",DataType.STRING,1024,FieldOption.NOT_NULL)
                .create();

        DatabaseCollection track = database.createCollection("dkperms_track")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL)
                .field("Name",DataType.STRING,FieldOption.NOT_NULL)
                .field("ScopeId",DataType.INTEGER,ForeignKey.of(scope,"Id"),FieldOption.NOT_NULL)
                .create();

        DatabaseCollection track_assignments = database.createCollection("dkperms_track_assignments")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL)
                .field("TrackId",DataType.INTEGER,ForeignKey.of(track,"Id"),FieldOption.NOT_NULL)
                .field("ObjectId",DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("Index",DataType.INTEGER,FieldOption.NOT_NULL)
                .create();

        this.auditLogStorage.setCollections(auditLog);
        this.scopeStorage.setCollections(scope);
        this.objectStorage.setCollections(object,object_type,object_meta,object_groups);
        this.parentStorage.setCollections(object_groups);
        this.permissionStorage.setCollections(object_permissions);
        this.trackStorage.setCollections(track,track_assignments);
    }
}
