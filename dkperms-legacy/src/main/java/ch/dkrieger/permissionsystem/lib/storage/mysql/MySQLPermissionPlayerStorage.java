/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.04.20, 10:24
 * @website %web%
 *
 * %license%
 */

package ch.dkrieger.permissionsystem.lib.storage.mysql;

import ch.dkrieger.permissionsystem.lib.player.PermissionPlayer;
import ch.dkrieger.permissionsystem.lib.player.PermissionPlayerStorage;
import ch.dkrieger.permissionsystem.lib.storage.mysql.query.QueryBuilder;
import ch.dkrieger.permissionsystem.lib.storage.mysql.query.SelectQuery;
import ch.dkrieger.permissionsystem.lib.storage.mysql.table.TableManager;
import ch.dkrieger.permissionsystem.lib.utils.GeneralUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MySQLPermissionPlayerStorage implements PermissionPlayerStorage{

    @Override
    public Collection<PermissionPlayer> getPlayers() throws Exception{
        Collection<PermissionPlayer> data = new ArrayList<>();
        SelectQuery query = TableManager.getInstance().getPlayerTable().select();
        ResultSet result = query.execute();
        try {
            while (result.next()){
                data.add(getPermissionPlayer(result));
            }
        }finally{
            result.close();
            query.close();
        }
        return data;
    }

    @Override
    public PermissionPlayer getPermissionPlayer(UUID uuid) throws Exception{
        SelectQuery query = TableManager.getInstance().getPlayerTable().select().where("uuid",uuid);
        ResultSet result = query.execute();
        if(result == null) return null;
        try {
            if(!result.next()) return null;
            result.first();
            return getPermissionPlayer(result);
        }finally{
            result.close();
            query.close();
        }
    }
    @Override
    public PermissionPlayer getPermissionPlayer(String name) throws Exception{
        SelectQuery query = TableManager.getInstance().getPlayerTable().select().where("name",name);
        ResultSet result = query.execute();
        if(result == null) return null;
        try {
            if(!result.next()) return null;
            result.first();
            return getPermissionPlayer(result);
        }finally{
            result.close();
            query.close();
        }
    }
    private PermissionPlayer getPermissionPlayer(ResultSet result) throws SQLException{
        return new PermissionPlayer(result.getInt("id"),result.getString("name"),UUID.fromString(result.getString("uuid")));
    }
    @Override
    public PermissionPlayer createPermissionPlayer(UUID uuid, String name) {
        checkName(name);
        int id = TableManager.getInstance().getPlayerTable().insert().insert("name").insert("uuid").value(name).value(uuid).executeAndGetKeyInInt();
        return new PermissionPlayer(id,name,uuid);
    }
    public void updateName(UUID uuid, String name){
        checkName(name);
        TableManager.getInstance().getPlayerTable().update().set("name",name).where("uuid",uuid).execute();
    }
    private void checkName(String name){
        try{
            QueryBuilder builder = new QueryBuilder();
            SelectQuery query = TableManager.getInstance().getPlayerTable().select().where("name",name);
            ResultSet result = query.execute();
            if(result == null) return;
            try {
                while(result.next()){
                    builder.append(TableManager.getInstance().getPlayerTable().update().set("name", GeneralUtil.getRandomString(16))
                            .where("id",result.getInt("id")));
                }
            }finally{
                result.close();
                query.close();
            }
            builder.buildAndExecute();
        }catch (Exception ignored){}
    }
}
