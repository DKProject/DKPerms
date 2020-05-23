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

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.object.PermissionObjectType;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.document.annotations.DocumentIgnored;
import net.pretronic.libraries.document.annotations.DocumentKey;
import org.mcnative.common.McNative;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;

public class DKPermsConfig {

    @DocumentIgnored //@Todo create an implement simple mode
    public static String MODE = "SIMPLE";

    // Scope Configuration

    public static String SCOPE_NAMESPACE = "Minecraft";

    @DocumentKey("scope.current.dynamic")
    public static boolean SCOPE_CURRENT_INSTANCE_DYNAMIC = true;
    @DocumentKey("scope.current.instance")
    public static String SCOPE_CURRENT_INSTANCE_SCOPE_NAME = "\\ServerGroup@Server\\Server@Server-1";
    @DocumentKey("scope.current.group")
    public static String SCOPE_CURRENT_GROUP_SCOPE_NAME = "\\ServerGroup@Server";
    @DocumentKey("scope.serverGroupSplit")
    public static String SCOPE_SERVER_GROUP_SPLIT = "-";
    @DocumentKey("scope.worldKey")
    public static String SCOPE_WORLD_KEY = "world";

    @DocumentKey("mcnative.managementScope.group")
    public static String MCNATIVE_MANAGEMENT_SCOPE_GROUP_NAME = "{global}";
    @DocumentKey("mcnative.managementScope.permission")
    public static String MCNATIVE_MANAGEMENT_SCOPE_PERMISSION_NAME = "{currentGroup}";
    @DocumentKey("mcnative.managementScope.operator")
    public static String MCNATIVE_MANAGEMENT_SCOPE_OPERATOR_NAME = "{currentGroup}";

    //Object Configuration

    @DocumentKey("object.player.scope")
    public static String OBJECT_PLAYER_SCOPE_NAME = "\\\\namespace=minecraft";

    public static String OBJECT_GROUP_SCOPE_NAME = "\\\\namespace=minecraft";

    @DocumentKey("object.track.scope")
    public static String OBJECT_TRACK_SCOPE_NAME = "\\\\namespace=minecraft";

    //Security Configuration

    public static boolean SECURITY_COMMANDS_ENABLED = McNative.getInstance().getPlatform().isProxy() || !McNative.getInstance().isNetworkAvailable();
    public static boolean SECURITY_OPERATOR_ENABLED = false;
    public static boolean SECURITY_RESTRICTED_ENABLED = false;
    public static Collection<String> SECURITY_RESTRICTED_USERS = Arrays.asList("Dkrieger","cb7f0812-1fbb-4715-976e-a81e52be4b67");

    //public static boolean SYNCHRONISATION_ENABLED = false;
    //public static int SYNCHRONISATION_ENABLED = false;

    @DocumentKey("deleteTimedOutEntries.enabled")
    public static boolean DELETE_TIMED_OUT_ENTRIES_ENABLED = true;

    @DocumentKey("deleteTimedOutEntries.interval")
    public static String DELETE_TIMED_OUT_ENTRIES_INTERVAL = "30m";

    @DocumentKey("format.date.pattern")
    public static String FORMAT_DATE_PATTERN = "dd.MM.yyyy hh:mm";

    public static String FORMAT_DATE_ENDLESSLY = "-";

    public static CommandConfiguration COMMAND_PERMISSION = CommandConfiguration.newBuilder()
            .enabled(true)
            .name("permissions")
            .aliases("perms","permission","perm","dkperms")
            .permission("dkperms.admin")
            .create();

    public static CommandConfiguration COMMAND_RANK = CommandConfiguration.newBuilder()
            .enabled(true)
            .name("rank")
            .aliases("ranks")
            .permission("dkperms.rank")
            .create();

    public static CommandConfiguration COMMAND_TEAM = CommandConfiguration.newBuilder()
            .enabled(true)
            .name("team")
            .permission("dkperms.team")
            .create();

    //Loaded Configuration

    public static transient PermissionScope SCOPE_CURRENT_INSTANCE_SCOPE;
    public static transient PermissionScope SCOPE_CURRENT_GROUP_SCOPE;

    public static transient PermissionScope MCNATIVE_MANAGEMENT_SCOPE_GROUP;
    public static transient PermissionScope MCNATIVE_MANAGEMENT_SCOPE_PERMISSION;
    public static transient PermissionScope MCNATIVE_MANAGEMENT_SCOPE_OPERATOR;

    public static transient PermissionScope OBJECT_PLAYER_SCOPE;

    public static transient PermissionScope OBJECT_GROUP_SCOPE;
    public static transient PermissionScope OBJECT_TRACK_SCOPE;

    public static transient SimpleDateFormat FORMAT_DATE;


    public static void load(){
        OBJECT_PLAYER_SCOPE = DKPerms.getInstance().getScopeManager().get(DKPermsConfig.OBJECT_PLAYER_SCOPE_NAME);
        OBJECT_GROUP_SCOPE = DKPerms.getInstance().getScopeManager().get(DKPermsConfig.OBJECT_GROUP_SCOPE_NAME);
        OBJECT_TRACK_SCOPE = DKPerms.getInstance().getScopeManager().get(DKPermsConfig.OBJECT_TRACK_SCOPE_NAME);

        FORMAT_DATE = new SimpleDateFormat(FORMAT_DATE_PATTERN);
    }
}
