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

import org.mcnative.common.text.Text;
import org.mcnative.common.text.components.MessageKeyComponent;

public final class Messages {

    public static final MessageKeyComponent COMMAND_PERMS_HELP = Text.ofMessageKey("dkperms.command.perms.help");

    public static final MessageKeyComponent USER_NOTFOUND = Text.ofMessageKey("dkperms.user.notfound");

    public static final MessageKeyComponent GROUP_NOTFOUND = Text.ofMessageKey("dkperms.group.notfound");

    public static final MessageKeyComponent SCOPE_INVALID = Text.ofMessageKey("dkperms.scope.invalid");
    public static final MessageKeyComponent SCOPE_NOTFOUND = Text.ofMessageKey("dkperms.scope.notfound");

    public static final MessageKeyComponent OBJECT_META_SET = Text.ofMessageKey("dkperms.object.meta.set");
    public static final MessageKeyComponent OBJECT_META_UNSET = Text.ofMessageKey("dkperms.object.meta.unset");
    public static final MessageKeyComponent OBJECT_META_CLEAR = Text.ofMessageKey("dkperms.object.meta.clear");
    public static final MessageKeyComponent OBJECT_META_SHOW = Text.ofMessageKey("dkperms.object.meta.show");
    public static final MessageKeyComponent OBJECT_META_LIST = Text.ofMessageKey("dkperms.object.meta.list");

    public static final MessageKeyComponent OBJECT_META_TREE_HEADER = Text.ofMessageKey("dkperms.object.meta.tree.header");
    public static final MessageKeyComponent OBJECT_META_TREE_ENTRY = Text.ofMessageKey("dkperms.object.meta.tree.entry");

    public static final MessageKeyComponent OBJECT_GROUP_CLEAR = Text.ofMessageKey("dkperms.object.group.clear");
    public static final MessageKeyComponent OBJECT_GROUP_ADD = Text.ofMessageKey("dkperms.object.group.add");
    public static final MessageKeyComponent OBJECT_GROUP_REMOVE = Text.ofMessageKey("dkperms.object.group.remove");

    public static final MessageKeyComponent OBJECT_USER_INFO = Text.ofMessageKey("dkperms.user.info");

    public static final MessageKeyComponent OBJECT_PERMISSION_SET = Text.ofMessageKey("dkperms.object.permission.set");
    public static final MessageKeyComponent OBJECT_PERMISSION_UNSET = Text.ofMessageKey("dkperms.object.permission.unset");
    public static final MessageKeyComponent OBJECT_PERMISSION_CLEAR = Text.ofMessageKey("dkperms.object.permission.clear");


    public static final MessageKeyComponent COMMAND_INVALID_SYNTAX = Text.ofMessageKey("dkperms.command.invalid.syntax");

}
