/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.05.20, 15:03
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.migration.cloudnet;

import net.pretronic.dkperms.api.migration.PermissionMigration;
import org.mcnative.runtime.api.McNative;

public class CloudNetV2CPermsMigration implements PermissionMigration {

    @Override
    public String getDisplayName() {
        return "CloudNetV2 (CPerms)";
    }

    @Override
    public String getName() {
        return "CloudNetV2";
    }

    @Override
    public boolean isAvailable() {
        return McNative.getInstance().getPluginManager().getPlugin("CloudNetAPI") != null;
    }

    @Override
    public boolean migrate() throws Exception {
        return new CloudNetV2CPermsMigrationExecutor().migrate();
    }
}
