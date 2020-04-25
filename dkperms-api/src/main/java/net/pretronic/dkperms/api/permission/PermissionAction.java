/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.api.permission;

public enum PermissionAction {

    ALLOW("+"),
    ALLOW_ALWAYS("++"),
    NEUTRAL(""),
    REJECT("-"),
    REJECT_ALWAYS("--");

    private final String symbol;

    PermissionAction(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public PermissionAction normalise(){
        if(this == ALLOW_ALWAYS) return ALLOW;
        else if(this == REJECT_ALWAYS) return REJECT;
        else return this;
    }

    public boolean has(){
        return this == ALLOW || this == ALLOW_ALWAYS;
    }

    public static PermissionAction of(int code){
        for (PermissionAction value : values()) if(value.ordinal() == code) return value;
        throw new IllegalArgumentException("Invalid permission action ("+code+")");
    }

    public static PermissionAction ofOrNull(int code){
        for (PermissionAction value : values()) if(value.ordinal() == code) return value;
        return null;
    }

    public static PermissionAction of(String nameOrSymbol){
        for (PermissionAction value : values()){
            if(value.name().equalsIgnoreCase(nameOrSymbol) || value.symbol.equalsIgnoreCase(nameOrSymbol)) return value;
        }
        throw new IllegalArgumentException("Invalid permission action ("+nameOrSymbol+")");
    }

    public static PermissionAction ofOrNull(String nameOrSymbol){
        for (PermissionAction value : values()){
            if(value.name().equalsIgnoreCase(nameOrSymbol) || value.symbol.equalsIgnoreCase(nameOrSymbol)) return value;
        }
        return null;
    }

}
