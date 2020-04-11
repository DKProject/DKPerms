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
import java.sql.SQLException;
import java.sql.Statement;

public class CreateQuery extends Query {

    public CreateQuery(String query){
        super( query);
        firstvalue = true;
    }

    public CreateQuery create(String value) {
        if(!firstvalue) query = query.substring(0,query.length()-1)+",";
        else firstvalue = false;
        query += value+")";
        return this;
    }

    public void execute(){
        try(Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        } catch(SQLException exception){
            exception.printStackTrace();
        }
    }
}
