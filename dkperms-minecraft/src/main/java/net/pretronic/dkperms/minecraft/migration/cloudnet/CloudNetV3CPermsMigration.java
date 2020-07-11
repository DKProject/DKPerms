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
import org.bukkit.Bukkit;

public class CloudNetV3CPermsMigration implements PermissionMigration {

    @Override
    public String getDisplayName() {
        return "CloudNetV3 (CPerms)";
    }

    @Override
    public String getName() {
        return "CloudNetV3";
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("CloudNet-Bridge") != null;
    }

    @Override
    public boolean migrate() throws Exception {
        return new CloudNetV3CPermsMigrationExecutor().migrate();
    }
}
