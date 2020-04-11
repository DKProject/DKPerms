/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.04.20, 10:24
 * @website %web%
 *
 * %license%
 */

package ch.dkrieger.permissionsystem.lib.storage.mysql.query;

import ch.dkrieger.permissionsystem.lib.PermissionSystem;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

public class Query {

    protected String query;
    protected boolean firstvalue;
    protected boolean and;
    protected boolean comma;
    protected List<Object> values;

    public Query(String query){
        this.query = query;
        this.firstvalue = true;
        this.comma = false;
        this.and = false;
        this.values = new LinkedList<>();
    }

    public Connection getConnection() {
        return PermissionSystem.getInstance().getMySQL().getConnection();
    }

    public String toString(){
        return this.query;
    }

    public List<Object> getValues(){
        return this.values;
    }
}
