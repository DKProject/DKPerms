/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 05.05.20, 20:33
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft;

import net.pretronic.dkperms.api.object.BuiltInPermissionObjectType;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.permission.analyse.PermissionAnalyseResult;
import net.pretronic.dkperms.common.entity.DefaultPermissionEntity;
import net.pretronic.dkperms.common.entity.DefaultPermissionParentEntity;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectSnapshot;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectType;
import net.pretronic.dkperms.common.object.meta.DefaultObjectMetaEntry;
import net.pretronic.dkperms.common.scope.DefaultPermissionScope;
import net.pretronic.dkperms.minecraft.commands.TeamCommand;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriber;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriberRegistry;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.serviceprovider.message.ColoredString;

public class DescriberRegistrar {

    public static void register(){
        registerObject();
        registerGroupEntity();
        registerSnapshot();

        VariableDescriberRegistry.registerDescriber(DefaultPermissionParentEntity.class)
                .setForwardFunction(DefaultPermissionParentEntity::getParent);

        VariableDescriberRegistry.registerDescriber(DefaultPermissionScope.class);
        VariableDescriberRegistry.registerDescriber(DefaultObjectMetaEntry.class);
        VariableDescriberRegistry.registerDescriber(DefaultPermissionEntity.class);
        VariableDescriberRegistry.registerDescriber(DefaultPermissionObjectType.class);
        VariableDescriberRegistry.registerDescriber(TeamCommand.TeamTree.class);
        VariableDescriberRegistry.registerDescriber(PermissionAnalyseResult.class);
        VariableDescriberRegistry.registerDescriber(BuiltInPermissionObjectType.class);
        VariableDescriberRegistry.registerDescriber(DKPermsPlayerDesign.class);

        VariableDescriber<DKPermsPlayerDesign> designDescriber = VariableDescriberRegistry.registerDescriber(DKPermsPlayerDesign.class);
        ColoredString.makeDescriberColored(designDescriber);
    }

    private static void registerObject(){
        VariableDescriber<DefaultPermissionObject> objectDescriber = VariableDescriberRegistry.registerDescriber(DefaultPermissionObject.class);
        objectDescriber.registerFunction("uniqueId", DefaultPermissionObject::getAssignmentId);
        objectDescriber.registerParameterFunction("property", (object, key) ->{
            ObjectMetaEntry entry =  object.getMeta().getHighest(key);
            return entry != null ? new ColoredString(entry.getValue()) : "";
        });

        objectDescriber.registerParameterFunction("boolProperty", (object, key) ->{
            ObjectMetaEntry entry =  object.getMeta().getHighest(key);
            return entry != null ?entry.getBooleanValue() : "false";
        });

        objectDescriber.registerParameterFunction("numberProperty", (object, key) ->{
            ObjectMetaEntry entry =  object.getMeta().getHighest(key);
            return entry != null ?entry.getLongValue(): "0";
        });

        objectDescriber.registerFunction("globalGroups", PermissionObject::getParents);

        objectDescriber.registerFunction("displayName", object -> {
            if(object.getType().equals(PermissionObjectType.USER_ACCOUNT)){
                MinecraftPlayer player = object.getHolder(MinecraftPlayer.class);
                if(player != null) return new ColoredString(player.getDisplayName());
                return object.getName();
            }else {
                ObjectMetaEntry color = object.getMeta().getHighest("color");
                return new ColoredString((color != null ? color.getValue() : "")+object.getName());
            }
        });
    }

    private static void registerSnapshot(){
        VariableDescriber<DefaultPermissionObjectSnapshot> snapshotDescriber = VariableDescriberRegistry.registerDescriber(DefaultPermissionObjectSnapshot.class);
        snapshotDescriber.registerParameterFunction("property", (snapshot, key) ->{
            ObjectMetaEntry entry = snapshot.getMeta(key);
            return entry != null ? new ColoredString(entry.getValue()) : "";
        });

        snapshotDescriber.registerParameterFunction("boolProperty", (snapshot, key) ->{
            ObjectMetaEntry entry = snapshot.getMeta(key);
            return entry != null ?entry.getBooleanValue() : "false";
        });

        snapshotDescriber.registerParameterFunction("numberProperty", (snapshot, key) ->{
            ObjectMetaEntry entry = snapshot.getMeta(key);
            return entry != null ?entry.getLongValue(): "0";
        });

        snapshotDescriber.registerParameterFunction("groups", (snapshot, key) -> snapshot.getEffectedGroupGraph().traverse());
    }

    private static void registerGroupEntity(){
        VariableDescriber<DefaultPermissionParentEntity> groupEntityDescriber = VariableDescriberRegistry.registerDescriber(DefaultPermissionParentEntity.class);
        groupEntityDescriber.setForwardFunction(DefaultPermissionParentEntity::getParent);
    }

}
