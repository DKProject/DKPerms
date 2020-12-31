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

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.api.player.PlayerDesign;

public class DKPermsPlayerDesign implements PlayerDesign {

    private final PermissionObject object;

    public DKPermsPlayerDesign(PermissionObject object) {
        this.object = object;
    }

    @Override
    public String getColor() {
        ObjectMetaEntry entry = object.getCurrentSnapshot().getMeta("color");
        return entry != null?entry.getValue():"";
    }

    @Override
    public String getPrefix() {
        ObjectMetaEntry entry = object.getCurrentSnapshot().getMeta("prefix");
        return entry != null?entry.getValue():"";
    }

    @Override
    public String getSuffix() {
        ObjectMetaEntry entry = object.getCurrentSnapshot().getMeta("suffix");
        return entry != null?entry.getValue():"";
    }

    @Override
    public String getChat() {
        ObjectMetaEntry entry = object.getCurrentSnapshot().getMeta("chat");
        return entry != null?entry.getValue():"";
    }

    @Override
    public void appendAdditionalVariables(VariableSet variables) {
        variables.addDescribed("object",object);
    }

    @Override
    public int getPriority() {
        ObjectMetaEntry entry = object.getCurrentSnapshot().getMeta("priority");
        if(entry == null){
            PermissionObject group = object.getCurrentSnapshot().getHighestGroup();
            return group != null ? group.getPriority() : 0;
        }
        return entry.getIntValue();
    }

}

