/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.backup;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface BackupManager {

    Backup exportData();

    boolean importData(Backup backup);

    CompletableFuture<Boolean> importDataAsync(Backup backup);

    Backup readBackup(File location);

    CompletableFuture<Backup> readBackupAsync(File location);
}
