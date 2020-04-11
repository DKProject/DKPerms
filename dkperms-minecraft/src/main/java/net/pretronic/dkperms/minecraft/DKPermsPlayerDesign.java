/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 18.01.20, 22:29
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft;

import net.pretronic.dkperms.api.graph.ObjectMetaGraph;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import org.mcnative.common.player.PlayerDesign;

public class DKPermsPlayerDesign implements PlayerDesign {
    
    private String color;
    private String prefix;
    private String suffix;
    private String display;
    private int priority;

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public String getChat() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void update(ObjectMetaGraph meta){
        ObjectMetaEntry entry = meta.calculate("color");
        this.color = entry != null?entry.getValue():"";

        entry = meta.calculate("prefix");
        this.prefix = entry != null?entry.getValue():"";

        entry = meta.calculate("suffix");
        this.suffix = entry != null?entry.getValue():"";

        entry = meta.calculate("display");
        this.display = entry != null?entry.getValue():"";

        entry = meta.calculate("priority");
        this.priority = entry != null?entry.getIntValue():Integer.MAX_VALUE;
    }

}
