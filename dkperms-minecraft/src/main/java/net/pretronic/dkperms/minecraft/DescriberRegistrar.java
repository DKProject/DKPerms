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

import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.common.entity.DefaultPermissionEntity;
import net.pretronic.dkperms.common.entity.DefaultPermissionGroupEntity;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectSnapshot;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectType;
import net.pretronic.dkperms.common.object.meta.DefaultObjectMetaEntry;
import net.pretronic.dkperms.common.scope.DefaultPermissionScope;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriber;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriberRegistry;

public class DescriberRegistrar {

    public static void register(){
        registerObject();
        registerGroupEntity();
        registerSnapshot();

        VariableDescriberRegistry.registerDescriber(DefaultPermissionScope.class);
        VariableDescriberRegistry.registerDescriber(DefaultObjectMetaEntry.class);
        VariableDescriberRegistry.registerDescriber(DefaultPermissionEntity.class);
        VariableDescriberRegistry.registerDescriber(DefaultPermissionObjectType.class);
        VariableDescriberRegistry.registerDescriber(DKPermsPlayerDesign.class);
    }

    private static void registerObject(){
        VariableDescriber<DefaultPermissionObject> objectDescriber = VariableDescriberRegistry.registerDescriber(DefaultPermissionObject.class);
        objectDescriber.registerFunction("uniqueId", DefaultPermissionObject::getAssignmentId);
        objectDescriber.registerParameterFunction("property", (object, key) ->{
            ObjectMetaEntry entry =  object.getMeta().getHighest(key);
            /*
            if(result.length() == 2 && result.charAt(0) == '&'){
                result += "█";//Add for showing colors
            }
 */
            return entry != null ?entry.getValue() : "";
        });

        objectDescriber.registerParameterFunction("boolProperty", (object, key) ->{
            ObjectMetaEntry entry =  object.getMeta().getHighest(key);
            return entry != null ?entry.getBooleanValue() : "false";
        });

        objectDescriber.registerParameterFunction("numberProperty", (object, key) ->{
            ObjectMetaEntry entry =  object.getMeta().getHighest(key);
            return entry != null ?entry.getLongValue(): "0";
        });

        objectDescriber.registerFunction("globalGroups", PermissionObject::getGroups);
    }

    private static void registerSnapshot(){
        VariableDescriber<DefaultPermissionObjectSnapshot> snapshotDescriber = VariableDescriberRegistry.registerDescriber(DefaultPermissionObjectSnapshot.class);
        snapshotDescriber.registerParameterFunction("property", (snapshot, key) ->{
            ObjectMetaEntry entry = snapshot.getMeta(key);
            return entry != null ?entry.getValue() : "";
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
        VariableDescriber<DefaultPermissionGroupEntity> groupEntityDescriber = VariableDescriberRegistry.registerDescriber(DefaultPermissionGroupEntity.class);
        groupEntityDescriber.setForwardFunction(DefaultPermissionGroupEntity::getGroup);
    }

}
