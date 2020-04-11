/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 07.12.19, 18:15
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.config;

import net.pretronic.libraries.document.annotations.DocumentKey;
import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.scope.PermissionScope;

import java.util.Arrays;
import java.util.Collection;

public class DKPermsConfig {

    public static String MODE = "SIMPLE";

    // Scope Configuration

    public static String SCOPE_NAMESPACE = "Minecraft";

    public static boolean SCOPE_CURRENT_INSTANCE_DYNAMIC = true;
    public static String SCOPE_CURRENT_INSTANCE_SCOPE = "\\ServerGroup@Server\\Server@Server-1";

    //Object Configuration

    @DocumentKey("object.player.type")
    public static String OBJECT_PLAYER_TYPE_NAME = "user";
    @DocumentKey("object.player.scope")
    public static String OBJECT_PLAYER_SCOPE_NAME = "\\\\namespace=minecraft";

    @DocumentKey("object.group.type")
    public static String OBJECT_GROUP_TYPE_NAME = "group";
    @DocumentKey("object.group.scope")
    public static String OBJECT_GROUP_SCOPE_NAME = "\\\\namespace=minecraft";


    //Security Configuration

    public static boolean SECURITY_OPERATOR_ENABLED = false;
    public static boolean SECURITY_COMMANDS_ENABLED = true;
    public static boolean SECURITY_RESTRICTED_ENABLED = false;
    public static Collection<String> SECURITY_RESTRICTED_USERS = Arrays.asList("Dkrieger","cb7f0812-1fbb-4715-976e-a81e52be4b67","1");

    //public static boolean SYNCHRONISATION_ENABLED = false;
    //public static int SYNCHRONISATION_ENABLED = false;


    //Command Configuration
    public static boolean COMMAND_PERMISSION_ENABLED = true;
    public static String COMMAND_PERMISSION_PERMISSION = "dkperms.admin";
    public static String COMMAND_PERMISSION_NAME = "permissions";
    public static Collection<String> COMMAND_PERMISSION_ALIASES = Arrays.asList("perms","permission");



    //Loaded Configuration

    public static transient PermissionObjectType OBJECT_PLAYER_TYPE;
    public static transient PermissionScope OBJECT_PLAYER_SCOPE;

    public static transient PermissionObjectType OBJECT_GROUP_TYPE;
    public static transient PermissionScope OBJECT_GROUP_SCOPE;


    public static boolean load(){//@Todo error messages
        OBJECT_PLAYER_TYPE = DKPerms.getInstance().getObjectManager().getType(DKPermsConfig.OBJECT_PLAYER_TYPE_NAME);
        OBJECT_PLAYER_SCOPE = DKPerms.getInstance().getScopeManager().get(DKPermsConfig.OBJECT_PLAYER_SCOPE_NAME);

        OBJECT_GROUP_TYPE = DKPerms.getInstance().getObjectManager().getType(DKPermsConfig.OBJECT_GROUP_TYPE_NAME);
        OBJECT_GROUP_SCOPE = DKPerms.getInstance().getScopeManager().get(DKPermsConfig.OBJECT_GROUP_SCOPE_NAME);
        return true;
    }
}
