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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteQuery extends Query {

    public DeleteQuery(String query) {
        super( query);
        this.firstvalue = true;
    }

    public DeleteQuery where(String key, Object value) {
        if(and) query += " AND";
        else{
            query += " WHERE";
            and = true;
        }
        query += " "+key+"=";
        values.add(value);
        query += "?";
        return this;
    }

    public DeleteQuery whereLower(String key, Object value) {
        if(and) query += " AND";
        else{
            query += " WHERE";
            and = true;
        }
        query += " "+key+"<";
        values.add(value);
        query += "?";
        return this;
    }

    public DeleteQuery whereHigher(String key, Object value) {
        if(and) query += " AND";
        else{
            query += " WHERE";
            and = true;
        }
        query += " "+key+">";
        values.add(value);
        query += "?";
        return this;
    }

    public void execute() {
        try(Connection connection = getConnection()) {
            PreparedStatement pstatement = connection.prepareStatement(query);
            int i = 1;
            for (Object object : values) {
                pstatement.setString(i,object.toString());
                i++;
            }
            pstatement.executeUpdate();
            pstatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
