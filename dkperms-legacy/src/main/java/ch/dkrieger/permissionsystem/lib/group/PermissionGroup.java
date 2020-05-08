/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.04.20, 10:24
 * @website %web%
 *
 * %license%
 */

package ch.dkrieger.permissionsystem.lib.group;

import ch.dkrieger.permissionsystem.lib.PermissionAdapter;
import ch.dkrieger.permissionsystem.lib.PermissionType;
import ch.dkrieger.permissionsystem.lib.player.PlayerDesign;

import java.util.UUID;

public class PermissionGroup extends PermissionAdapter{

    private String description;
    private boolean defaultGroup, team;
    private int priority, joinPower, tsGroupId;
    private PlayerDesign playerdesign;

    public PermissionGroup(String name, UUID uuid, String description, boolean defaultGroup, boolean team, int priority, int tsGroupId, int joinPower, PlayerDesign playerdesign) {
        super(name, uuid, PermissionType.GROUP);
        this.description = description;
        this.defaultGroup = defaultGroup;
        this.team = team;
        this.priority = priority;
        this.tsGroupId = tsGroupId;
        this.joinPower = joinPower;
        this.playerdesign = playerdesign;
    }

    public String getDescription() {
        return description;
    }

    public String getColor(){
        if(this.playerdesign.getColor().equalsIgnoreCase("-1")) return "";
        else return this.playerdesign.getColor();
    }

    public int getPriority() {
        return priority;
    }

    @Deprecated
    public int getJoinpower() {
        return joinPower;
    }

    public int getJoinPower() {
        return joinPower;
    }

    @Deprecated
    public int getTsgroupID() {
        return tsGroupId;
    }

    public int getTsGroupID() {
        return tsGroupId;
    }

    public PlayerDesign getPlayerDesign() {
        return playerdesign;
    }


    public boolean isDefault(){
        return this.defaultGroup;
    }

    public boolean isTeam(){
        return this.team;
    }
}
