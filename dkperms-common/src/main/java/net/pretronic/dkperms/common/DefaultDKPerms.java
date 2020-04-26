/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 07.12.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.common;

import net.pretronic.dkperms.api.DKPermsFormatter;
import net.pretronic.dkperms.api.logging.AuditLog;
import net.pretronic.dkperms.api.migration.MigrationAssistant;
import net.pretronic.dkperms.api.storage.DKPermsStorage;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.utility.concurrent.AsyncExecutor;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObjectManager;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyse;
import net.pretronic.dkperms.api.scope.PermissionScopeManager;

import java.text.SimpleDateFormat;

public class DefaultDKPerms extends DKPerms {

    private final String version;
    private final int build;
    private final PretronicLogger logger;
    private final DKPermsStorage storage;
    private final MigrationAssistant migrationAssistant;
    private final AuditLog auditLog;
    private final PermissionScopeManager scopeManager;
    private final PermissionObjectManager objectManager;
    private final AsyncExecutor executor;
    private final DKPermsFormatter formatter;

    public DefaultDKPerms(String version, int build, PretronicLogger logger,MigrationAssistant migrationAssistant,DKPermsStorage storage,AuditLog auditLog
            , PermissionScopeManager scopeManager,PermissionObjectManager objectManager, AsyncExecutor executor,DKPermsFormatter formatter) {
        this.version = version;
        this.build = build;
        this.logger = logger;
        this.storage = storage;
        this.auditLog = auditLog;
        this.migrationAssistant = migrationAssistant;
        this.scopeManager = scopeManager;
        this.objectManager = objectManager;
        this.executor = executor;
        this.formatter = formatter;
    }



    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public int getBuild() {
        return build;
    }

    @Override
    public PretronicLogger getLogger() {
        return logger;
    }

    @Override
    public DKPermsStorage getStorage() {
        return storage;
    }

    @Override
    public MigrationAssistant getMigrationAssistant() {
        return migrationAssistant;
    }

    @Override
    public AuditLog getAuditLog() {
        return auditLog;
    }

    @Override
    public PermissionScopeManager getScopeManager() {
        return scopeManager;
    }

    @Override
    public PermissionObjectManager getObjectManager() {
        return objectManager;
    }

    @Override
    public AsyncExecutor getExecutor() {
        return executor;
    }

    @Override
    public DKPermsFormatter getFormatter() {
        return formatter;
    }

    @Override
    public PermissionAnalyse startAnalyse() {
        return null;
    }
}
