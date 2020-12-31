/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 18.04.20, 17:04
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft;

import net.pretronic.dkperms.api.DKPermsFormatter;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.libraries.utility.duration.DurationProcessor;

public class MinecraftFormatter implements DKPermsFormatter {

    @Override
    public String formatDateTime(long time) {
        if(time <= 0) return DKPermsConfig.FORMAT_DATE_ENDLESSLY;
        return DKPermsConfig.FORMAT_DATE.format(time);
    }

    @Override
    public String formatRemainingDuration(long timeout) {
        if(timeout <= 0) return DKPermsConfig.FORMAT_DATE_ENDLESSLY;
        return DurationProcessor.getStandard().formatShort(timeout/1000);//@Todo make configurable
    }
}
