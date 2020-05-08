package ch.dkrieger.permissionsystem.lib;

import ch.dkrieger.permissionsystem.lib.config.Config;
import ch.dkrieger.permissionsystem.lib.entity.PermissionEntityStorage;
import ch.dkrieger.permissionsystem.lib.group.PermissionGroupStorage;
import ch.dkrieger.permissionsystem.lib.permission.PermissionStorage;
import ch.dkrieger.permissionsystem.lib.player.PermissionPlayerStorage;
import ch.dkrieger.permissionsystem.lib.storage.StorageType;
import ch.dkrieger.permissionsystem.lib.storage.mysql.*;
import ch.dkrieger.permissionsystem.lib.storage.mysql.table.TableManager;
import ch.dkrieger.permissionsystem.lib.storage.yaml.YamlPermissionEntityStorage;
import ch.dkrieger.permissionsystem.lib.storage.yaml.YamlPermissionGroupStorage;
import ch.dkrieger.permissionsystem.lib.storage.yaml.YamlPermissionPlayerStorage;
import ch.dkrieger.permissionsystem.lib.storage.yaml.YamlPermissionStorage;
import ch.dkrieger.permissionsystem.lib.utils.Messages;

/*
 *
 *  * Copyright (c) 2018 Davide Wietlisbach on 10.06.18 13:20
 *
 */

public class PermissionSystem {

    private static PermissionSystem INSTANCE;

    private Config config;

    private PermissionPlayerStorage playerStorage;
    private PermissionGroupStorage groupStorage;
    private PermissionStorage permissionStorage;
    private PermissionEntityStorage entityStorage;

    private MySQL mysql;

    public PermissionSystem() {
        INSTANCE = this;
        new Messages("DKPermsLegacy");

        systemBootstrap();
    }

    private void systemBootstrap(){
        this.config = new Config(false);
        this.config.loadConfig();
        setupStorage();
    }

    private void setupStorage(){
        if(Config.STORAGE_TYPE == StorageType.MYSQL){
            this.mysql = new MySQL(Messages.SYSTEM_NAME,Config.STORAGE_MYSQL_HOST,Config.STORAGE_MYSQL_PORT,Config.STORAGE_MYSQL_USER
                    ,Config.STORAGE_MYSQL_PASSWORD,Config.STORAGE_MYSQL_DATABASE);
            this.mysql.connect();
            if(this.mysql.isConnect()){
                new TableManager(this.mysql);

                this.playerStorage = new MySQLPermissionPlayerStorage();
                this.groupStorage= new MySQLPermissionGroupStorage();
                this.permissionStorage = new MySQLPermissionStorage();
                this.entityStorage = new MySQLPermissionEntityStorage();
                return;
            }
        }
        Config.STORAGE_TYPE = StorageType.YAML;
        this.playerStorage = new YamlPermissionPlayerStorage();
        this.groupStorage = new YamlPermissionGroupStorage((YamlPermissionPlayerStorage)this.playerStorage);
        this.permissionStorage = new YamlPermissionStorage((YamlPermissionGroupStorage)this.groupStorage,(YamlPermissionPlayerStorage)this.playerStorage);
        this.entityStorage = new YamlPermissionEntityStorage((YamlPermissionGroupStorage)this.groupStorage,(YamlPermissionPlayerStorage)this.playerStorage);
    }

    public PermissionPlayerStorage getPlayerStorage() {
        return playerStorage;
    }

    public PermissionGroupStorage getGroupStorage() {
        return groupStorage;
    }

    public PermissionStorage getPermissionStorage() {
        return permissionStorage;
    }

    public PermissionEntityStorage getEntityStorage() {
        return entityStorage;
    }

    public void disable(){
        if(this.mysql != null) this.mysql.disconnect();
    }

    public MySQL getMySQL() {
        return mysql;
    }

    public Config getConfig() {
        return config;
    }

    public static PermissionSystem getInstance() {
        return INSTANCE;
    }

}
