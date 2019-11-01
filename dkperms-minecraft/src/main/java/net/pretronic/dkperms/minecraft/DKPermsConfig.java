/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft;

import java.util.Arrays;
import java.util.Collection;

public class DKPermsConfig {

    public static String MODE = "SIMPLE";


    public static String SCOPE_NAMESPACE = "Minecraft";

    public static String SCOPE_CREATE_GROUP = "Root";

    public static String SCOPE_CREATE_USER = "Root";

    public static String SCOPE_CURRENT_INSTANCE_SCOPE = "Proxy";
    public static String SCOPE_CURRENT_INSTANCE_Name = "Proxy-1";

    public static String SCOPE_CURRENT_GROUP_SCOPE = "ServerGroup";
    public static String SCOPE_CURRENT_GROUP_Name = "Proxy";


    public static boolean SECURITY_OPERATOR_ENABLED = false;

    public static boolean SECURITY_COMMANDS_ENABLED = true;

    public static boolean SECURITY_RESTRICTED_ENABLED = false;

    public static Collection<String> SECURITY_RESTRICTED_USERS = Arrays.asList("Dkrieger","cb7f0812-1fbb-4715-976e-a81e52be4b67","1");


    public static boolean COMMAND_PERMISSION_ENABLED = true;
    public static String COMMAND_PERMISSION_PERMISSION = "dkperms.admin";
    public static String COMMAND_PERMISSION_NAME = "permissions";
    public static Collection<String> COMMAND_PERMISSION_ALIASES = Arrays.asList("perms","permission");
}
