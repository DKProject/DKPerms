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

import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.api.text.components.MessageKeyComponent;

public final class Messages {

    public static final MessageKeyComponent SENDER_ONLY_CONSOLE = Text.ofMessageKey("dkperms.sender.only.console");
    public static final MessageKeyComponent INVALID_NUMBER = Text.ofMessageKey("dkperms.invalid.number");
    public static final MessageKeyComponent COMMAND_RESTRICTED = Text.ofMessageKey("dkperms.restricted");
    public static final MessageKeyComponent COMMAND_PERMS_HELP = Text.ofMessageKey("dkperms.command.perms.help");
    public static final MessageKeyComponent COMMAND_INVALID_SYNTAX = Text.ofMessageKey("dkperms.command.invalid.syntax");

    public static final MessageKeyComponent SCOPE_INVALID = Text.ofMessageKey("dkperms.scope.invalid");
    public static final MessageKeyComponent SCOPE_NOTFOUND = Text.ofMessageKey("dkperms.scope.notfound");


    public static final MessageKeyComponent GROUP_HELP = Text.ofMessageKey("dkperms.group.help");
    public static final MessageKeyComponent GROUP_NOTFOUND = Text.ofMessageKey("dkperms.group.notfound");

    public static final MessageKeyComponent GROUP_ALREADY_EXISTS = Text.ofMessageKey("dkperms.group.alreadyExists");
    public static final MessageKeyComponent GROUP_CREATED = Text.ofMessageKey("dkperms.group.created");
    public static final MessageKeyComponent GROUP_DELETE_CONFIRM = Text.ofMessageKey("dkperms.group.delete.confirm");
    public static final MessageKeyComponent GROUP_DELETED = Text.ofMessageKey("dkperms.group.deleted");
    public static final MessageKeyComponent GROUP_RENAMED = Text.ofMessageKey("dkperms.group.renamed");
    public static final MessageKeyComponent GROUP_COPY = Text.ofMessageKey("dkperms.group.copy");

    public static final MessageKeyComponent GROUP_LIST = Text.ofMessageKey("dkperms.group.list");
    public static final MessageKeyComponent GROUP_TREE_HEADER = Text.ofMessageKey("dkperms.group.tree.header");
    public static final MessageKeyComponent GROUP_TREE_SCOPE = Text.ofMessageKey("dkperms.group.tree.scope");
    public static final MessageKeyComponent GROUP_TREE_ENTRY = Text.ofMessageKey("dkperms.group.tree.entry");
    public static final MessageKeyComponent OBJECT_GROUP_INFO = Text.ofMessageKey("dkperms.group.info");

    public static final MessageKeyComponent GROUP_LIST_MEMBERS = Text.ofMessageKey("dkperms.group.listMembers");


    public static final MessageKeyComponent USER_HELP = Text.ofMessageKey("dkperms.user.help");
    public static final MessageKeyComponent USER_NOTFOUND = Text.ofMessageKey("dkperms.user.notfound");
    public static final MessageKeyComponent USER_INFO = Text.ofMessageKey("dkperms.user.info");

    public static final MessageKeyComponent OBJECT_META_HELP = Text.ofMessageKey("dkperms.object.meta.help");
    public static final MessageKeyComponent OBJECT_META_SET = Text.ofMessageKey("dkperms.object.meta.set");
    public static final MessageKeyComponent OBJECT_META_UNSET = Text.ofMessageKey("dkperms.object.meta.unset");
    public static final MessageKeyComponent OBJECT_META_CLEAR = Text.ofMessageKey("dkperms.object.meta.clear");
    public static final MessageKeyComponent OBJECT_META_SHOW = Text.ofMessageKey("dkperms.object.meta.show");
    public static final MessageKeyComponent OBJECT_META_LIST = Text.ofMessageKey("dkperms.object.meta.list");
    public static final MessageKeyComponent OBJECT_META_TREE_HEADER = Text.ofMessageKey("dkperms.object.meta.tree.header");
    public static final MessageKeyComponent OBJECT_META_TREE_SCOPE = Text.ofMessageKey("dkperms.object.meta.tree.scope");
    public static final MessageKeyComponent OBJECT_META_TREE_ENTRY = Text.ofMessageKey("dkperms.object.meta.tree.entry");
    public static final MessageKeyComponent OBJECT_META_CHECK = Text.ofMessageKey("dkperms.object.meta.check");


    public static final MessageKeyComponent OBJECT_PERMISSION_HELP = Text.ofMessageKey("dkperms.object.permission.help");
    public static final MessageKeyComponent OBJECT_PERMISSION_SET = Text.ofMessageKey("dkperms.object.permission.set");
    public static final MessageKeyComponent OBJECT_PERMISSION_SET_FAILED = Text.ofMessageKey("dkperms.object.permission.set.failed");
    public static final MessageKeyComponent OBJECT_PERMISSION_UNSET = Text.ofMessageKey("dkperms.object.permission.unset");
    public static final MessageKeyComponent OBJECT_PERMISSION_CLEAR = Text.ofMessageKey("dkperms.object.permission.clear");
    public static final MessageKeyComponent OBJECT_PERMISSION_SHOW = Text.ofMessageKey("dkperms.object.permission.show");
    public static final MessageKeyComponent OBJECT_PERMISSION_LIST = Text.ofMessageKey("dkperms.object.permission.list");
    public static final MessageKeyComponent OBJECT_PERMISSION_TREE_HEADER = Text.ofMessageKey("dkperms.object.permission.tree.header");
    public static final MessageKeyComponent OBJECT_PERMISSION_TREE_SCOPE = Text.ofMessageKey("dkperms.object.permission.tree.scope");
    public static final MessageKeyComponent OBJECT_PERMISSION_TREE_ENTRY = Text.ofMessageKey("dkperms.object.permission.tree.entry");
    public static final MessageKeyComponent OBJECT_PERMISSION_NOT_SET = Text.ofMessageKey("dkperms.object.permission.notSet");
    public static final MessageKeyComponent OBJECT_PERMISSION_CHECK = Text.ofMessageKey("dkperms.object.permission.check");


    public static final MessageKeyComponent OBJECT_GROUP_HELP = Text.ofMessageKey("dkperms.object.group.help");
    public static final MessageKeyComponent OBJECT_GROUP_CLEAR = Text.ofMessageKey("dkperms.object.group.clear");
    public static final MessageKeyComponent OBJECT_GROUP_SET = Text.ofMessageKey("dkperms.object.group.set");
    public static final MessageKeyComponent OBJECT_GROUP_ADD = Text.ofMessageKey("dkperms.object.group.add");
    public static final MessageKeyComponent OBJECT_GROUP_REMOVE = Text.ofMessageKey("dkperms.object.group.remove");
    public static final MessageKeyComponent OBJECT_GROUP_SHOW = Text.ofMessageKey("dkperms.object.group.show");
    public static final MessageKeyComponent OBJECT_GROUP_LIST = Text.ofMessageKey("dkperms.object.group.list");
    public static final MessageKeyComponent OBJECT_GROUP_TREE_HEADER = Text.ofMessageKey("dkperms.object.group.tree.header");
    public static final MessageKeyComponent OBJECT_GROUP_TREE_SCOPE = Text.ofMessageKey("dkperms.object.group.tree.scope");
    public static final MessageKeyComponent OBJECT_GROUP_TREE_ENTRY = Text.ofMessageKey("dkperms.object.group.tree.entry");
    public static final MessageKeyComponent OBJECT_GROUP_NOT_SET = Text.ofMessageKey("dkperms.object.group.notSet");
    public static final MessageKeyComponent OBJECT_GROUP_PRIORITY_SET = Text.ofMessageKey("dkperms.object.group.priority.set");
    public static final MessageKeyComponent OBJECT_GROUP_CHECK = Text.ofMessageKey("dkperms.object.group.check");

    public static final MessageKeyComponent TRACK_HELP = Text.ofMessageKey("dkperms.track.help");
    public static final MessageKeyComponent TRACK_ALREADY_EXISTS = Text.ofMessageKey("dkperms.track.alreadyExists");
    public static final MessageKeyComponent TRACK_ALREADY_CONTAINS = Text.ofMessageKey("dkperms.track.alreadyContains");
    public static final MessageKeyComponent TRACK_NOT_FOUND = Text.ofMessageKey("dkperms.track.notFound");
    public static final MessageKeyComponent TRACK_LIST = Text.ofMessageKey("dkperms.track.list");
    public static final MessageKeyComponent TRACK_INFO = Text.ofMessageKey("dkperms.track.info");
    public static final MessageKeyComponent TRACK_CREATED = Text.ofMessageKey("dkperms.track.created");
    public static final MessageKeyComponent TRACK_RENAMED = Text.ofMessageKey("dkperms.track.renamed");
    public static final MessageKeyComponent TRACK_DELETED = Text.ofMessageKey("dkperms.track.deleted");
    public static final MessageKeyComponent TRACK_CLEAR = Text.ofMessageKey("dkperms.track.clear");
    public static final MessageKeyComponent TRACK_ADD = Text.ofMessageKey("dkperms.track.add");
    public static final MessageKeyComponent TRACK_INSERT = Text.ofMessageKey("dkperms.track.insert");
    public static final MessageKeyComponent TRACK_REMOVE = Text.ofMessageKey("dkperms.track.remove");

    public static final MessageKeyComponent RANK_HELP = Text.ofMessageKey("dkperms.rank.help");
    public static final MessageKeyComponent RANK_LIST = Text.ofMessageKey("dkperms.rank.list");
    public static final MessageKeyComponent RANK_PROMOTED = Text.ofMessageKey("dkperms.rank.promoted");
    public static final MessageKeyComponent RANK_DEMOTED = Text.ofMessageKey("dkperms.rank.demoted");
    public static final MessageKeyComponent RANK_CHANGE_NO_PERMISSION_FOR_RANK = Text.ofMessageKey("dkperms.rank.noPermissionForRank");
    public static final MessageKeyComponent RANK_CHANGE_NO_PERMISSION_FOR_PLAYER = Text.ofMessageKey("dkperms.rank.noPermissionForPlayer");

    public static final MessageKeyComponent SYNCHRONIZED = Text.ofMessageKey("dkperms.synchronized");

    public static final MessageKeyComponent ANALYSE_ENABLED = Text.ofMessageKey("dkperms.analyse.enabled");
    public static final MessageKeyComponent ANALYSE_DISABLED = Text.ofMessageKey("dkperms.analyse.disabled");
    public static final MessageKeyComponent ANALYSE_JOINED = Text.ofMessageKey("dkperms.analyse.joined");
    public static final MessageKeyComponent ANALYSE_LEAVED = Text.ofMessageKey("dkperms.analyse.leaved");
    public static final MessageKeyComponent ANALYSE_HELP = Text.ofMessageKey("dkperms.analyse.help");

    public static final MessageKeyComponent TEAM = Text.ofMessageKey("dkperms.team");

    public static final MessageKeyComponent SERVER_JOIN_NOT_PERMITTED = Text.ofMessageKey("dkperms.server.notPermitted");
    public static final MessageKeyComponent SERVER_JOIN_FULL = Text.ofMessageKey("dkperms.server.join.full");
    public static final MessageKeyComponent SERVER_JOIN_FULL_KICK = Text.ofMessageKey("dkperms.server.join.full.kick");
    public static final MessageKeyComponent SERVER_JOIN_FULL_NO_KICK = Text.ofMessageKey("dkperms.server.join.full.noKick");

    public static final MessageKeyComponent LOG_LIST = Text.ofMessageKey("dkperms.log.list");
}
