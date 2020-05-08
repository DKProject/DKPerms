/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 05.05.20, 20:56
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.integration;

import net.pretronic.dkperms.api.minecraft.player.PermissionPlayer;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.serviceprovider.placeholder.PlaceholderHook;

public class DKPermsPlaceholders implements PlaceholderHook {

    @Override
    public Object onRequest(MinecraftPlayer player, String parameter) {
        PermissionObject object = player.getAs(PermissionPlayer.class).getObject();
        if(parameter.equalsIgnoreCase("name")){
            return player.getName();
        }else if(parameter.equalsIgnoreCase("uniqueId")){
            return player.getUniqueId();
        }else if(parameter.equalsIgnoreCase("id")){
            return object.getId();
        }else if(parameter.equalsIgnoreCase("scope")){
            return object.getScope().getPath();
        }else if(parameter.equalsIgnoreCase("rank")){
            return object.getCurrentSnapshot().getHighestGroup();
        }else if(parameter.equalsIgnoreCase("ranks")){
            StringBuilder builder = new StringBuilder();
            for (PermissionObject group : object.getCurrentSnapshot().getEffectedGroupGraph()) {
                builder.append(group.getName()).append(", ");
            }
            builder.setLength(builder.length()-1);
            return builder.toString();
        }else if(parameter.equalsIgnoreCase("color")){
            return player.getDesign().getColor();
        }else if(parameter.equalsIgnoreCase("prefix")){
            return player.getDesign().getPrefix();
        }else if(parameter.equalsIgnoreCase("suffix")){
            return player.getDesign().getSuffix();
        }else if(parameter.equalsIgnoreCase("display") || parameter.equalsIgnoreCase("chat")){
            return player.getDesign().getChat();
        }else if(parameter.equalsIgnoreCase("priority")){
            return player.getDesign().getPriority();
        }else if(parameter.equalsIgnoreCase("team")){
            ObjectMetaEntry result = object.getCurrentSnapshot().getMeta("team");
            return result != null && result.getBooleanValue();
        }else if(parameter.startsWith("property_") || parameter.startsWith("meta_")){
            String key = parameter.substring(parameter.lastIndexOf("_")+1);
            ObjectMetaEntry result = object.getCurrentSnapshot().getMeta(key);
            return result != null ? result.getValue() : "";
        }else if(parameter.startsWith("boolProperty_") || parameter.startsWith("boolMeta_")){
            String key = parameter.substring(parameter.lastIndexOf("_")+1);
            ObjectMetaEntry result = object.getCurrentSnapshot().getMeta(key);
            return result != null && result.getBooleanValue();
        }else if(parameter.startsWith("numberProperty_") || parameter.startsWith("intMeta_")){
            String key = parameter.substring(parameter.lastIndexOf("_")+1);
            ObjectMetaEntry result = object.getCurrentSnapshot().getMeta(key);
            return result != null ? result.getLongValue() : 0;
        }
        return null;
    }
}
