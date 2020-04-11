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

public class CustomQuery extends Query{

    public CustomQuery() {
        super(null);
    }

    public void execute(){
        throw new UnsupportedOperationException("Not allowed in custom query");
    }

    public void execute(String query) {
        this.query = query;
        try(Connection connection = getConnection()) {
            PreparedStatement pstatement = connection.prepareStatement(query);
            int i = 1;
            for (Object object : values) {
                pstatement.setString(i, object.toString());
                i++;
            }
            pstatement.executeUpdate();
            pstatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeSave(String query) throws SQLException{
        this.query = query;
        try(Connection connection = getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            int i = 1;
            for (Object object : values) {
                statement.setString(i, object.toString());
                i++;
            }
            statement.executeUpdate();
            statement.close();
        }
    }

    public void executeWithOutError(String query){
        try{ executeSave(query);
        }catch (Exception ignored){}
    }

    public void close() throws SQLException{
        getConnection().close();
    }
}
