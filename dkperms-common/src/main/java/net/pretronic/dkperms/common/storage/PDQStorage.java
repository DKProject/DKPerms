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
    private final PDQGroupStorage groupStorage;
    private final PDQPermissionStorage permissionStorage;

    public PDQStorage(Database database) {
        this.database = database;

        this.auditLogStorage = new PDQAuditLogStorage();
        this.scopeStorage = new PDQScopeStorage();
        this.objectStorage = new PDQObjectStorage();
        this.groupStorage = new PDQGroupStorage();
        this.permissionStorage = new PDQPermissionStorage();
        createTables();
    }

    @Override
    public String getName() {
        return database.getDriver().getName()+" ("+database.getDriver().getType()+")";
    }

    @Override
    public AuditLogStorage getAuditLogStorage() {
        return null;
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
    public GroupStorage getGroupStorage() {
        return groupStorage;
    }

    @Override
    public PermissionStorage getPermissionStorage() {
        return permissionStorage;
    }

    private void createTables(){
        DatabaseCollection scope = database.createCollection("DKPerms_Scope")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL,FieldOption.INDEX)
                .field("ParentId",DataType.INTEGER, ForeignKey.of(database.getName(),"DKPerms_Scope","Id", ForeignKey.Option.CASCADE),FieldOption.INDEX)
                .field("Key",DataType.STRING,64,FieldOption.NOT_NULL)
                .field("Name",DataType.STRING,64,FieldOption.NOT_NULL)
                .create();

        DatabaseCollection context = database.createCollection("DKPerms_Context")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL)
                .field("Name",DataType.STRING,64,FieldOption.NOT_NULL,FieldOption.UNIQUE,FieldOption.INDEX)
                .create();

        DatabaseCollection object_type = database.createCollection("DKPerms_Object_Type")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL)
                .field("Name",DataType.STRING,64,FieldOption.NOT_NULL,FieldOption.UNIQUE)
                .field("IsGroup",DataType.BOOLEAN,FieldOption.NOT_NULL)
                .create();

        DatabaseCollection object = database.createCollection("DKPerms_Object")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL,FieldOption.INDEX)
                .field("AssignmentId", DataType.UUID,FieldOption.INDEX)
                .field("Name",DataType.STRING,64,FieldOption.NOT_NULL,FieldOption.INDEX)
                .field("Priority",DataType.INTEGER,FieldOption.NOT_NULL)
                .field("TypeId",DataType.INTEGER,ForeignKey.of(object_type,"Id"),FieldOption.NOT_NULL)
                .field("ScopeId",DataType.INTEGER,ForeignKey.of(scope,"Id"),FieldOption.NOT_NULL)
                .field("Disabled",DataType.BOOLEAN,FieldOption.NOT_NULL)
                .field("DisplayName",DataType.STRING,64,FieldOption.NOT_NULL)
                .field("Description",DataType.LONG_TEXT,FieldOption.NOT_NULL)
                .create();

        DatabaseCollection object_meta = database.createCollection("DKPerms_Object_Meta")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL)
                .field("ObjectId",DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("ScopeId",DataType.INTEGER,ForeignKey.of(scope,"Id"),FieldOption.NOT_NULL)
                .field("Key",DataType.STRING,FieldOption.NOT_NULL)
                .field("Value",DataType.STRING,FieldOption.NOT_NULL)
                .field("Priority",DataType.INTEGER,FieldOption.NOT_NULL)
                .field("Timeout",DataType.LONG,FieldOption.NOT_NULL)
                .create();

        DatabaseCollection object_groups = database.createCollection("DKPerms_Object_Groups")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL)
                .field("ObjectId",DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("ScopeId",DataType.INTEGER,ForeignKey.of(scope,"Id"),FieldOption.NOT_NULL)
                .field("GroupId",DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("Action",DataType.INTEGER,FieldOption.NOT_NULL)
                .field("Timeout",DataType.LONG,FieldOption.NOT_NULL)
                .create();

        DatabaseCollection object_permissions = database.createCollection("DKPerms_Object_Permissions")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL)
                .field("ObjectId",DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("ScopeId",DataType.INTEGER,ForeignKey.of(scope,"Id"),FieldOption.NOT_NULL)
                .field("Permission",DataType.STRING,FieldOption.NOT_NULL)
                .field("Action",DataType.INTEGER,FieldOption.NOT_NULL)
                .field("Timeout",DataType.LONG,FieldOption.NOT_NULL)
                .create();

        DatabaseCollection auditLog = database.createCollection("DKPerms_AuditLog")
                .field("Id", DataType.INTEGER, FieldOption.AUTO_INCREMENT,FieldOption.PRIMARY_KEY,FieldOption.NOT_NULL)
                .field("ExecutorId", DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("Time",DataType.INTEGER,FieldOption.NOT_NULL)
                .field("Type",DataType.STRING,20,FieldOption.NOT_NULL)
                .field("Action",DataType.STRING,20,FieldOption.NOT_NULL)
                .field("OwnerId",DataType.INTEGER,ForeignKey.of(object,"Id"),FieldOption.NOT_NULL)
                .field("ScopeId",DataType.INTEGER,ForeignKey.of(scope,"Id"),FieldOption.NOT_NULL)
                .field("Field",DataType.STRING,32,FieldOption.NOT_NULL)
                .field("OldValue",DataType.STRING,500,FieldOption.NOT_NULL)
                .field("NewValue",DataType.STRING,500,FieldOption.NOT_NULL)
                .create();

        this.auditLogStorage.setCollections(auditLog);
        this.scopeStorage.setCollections(scope);
        this.objectStorage.setCollections(object,object_type,object_meta);
        this.groupStorage.setCollections(object_groups);
        this.permissionStorage.setCollections(object_permissions);
    }
}
