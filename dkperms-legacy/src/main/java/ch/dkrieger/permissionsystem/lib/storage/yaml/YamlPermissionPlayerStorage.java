/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.04.20, 10:24
 * @website %web%
 *
 * %license%
 */

package ch.dkrieger.permissionsystem.lib.storage.yaml;

import ch.dkrieger.permissionsystem.lib.config.Config;
import ch.dkrieger.permissionsystem.lib.config.SimpleConfig;
import ch.dkrieger.permissionsystem.lib.player.PermissionPlayer;
import ch.dkrieger.permissionsystem.lib.player.PermissionPlayerStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class YamlPermissionPlayerStorage extends SimpleConfig implements PermissionPlayerStorage{

    public YamlPermissionPlayerStorage() {
        super(new File(Config.STORAGE_FOLDER,"players.yml"));
        loadConfig();
    }
    @Override
    public void onLoad() {}

    @Override
    public void registerDefaults() {
        addValue("nextid",1);
    }

    @Override
    public Collection<PermissionPlayer> getPlayers() throws Exception {
        Collection<PermissionPlayer> result = new ArrayList<>();
        for (String key: getKeys("players")) {
            result.add(new PermissionPlayer(getIntValue("players."+key+".id"),getStringValue("players."+key+".name"),UUID.fromString(key)));
        }
        return result;
    }

    @Override
    public PermissionPlayer getPermissionPlayer(UUID uuid) throws Exception {
        String name = getStringValue("players."+uuid+".name");
        if(name != null && name.length() > 0){
            return new PermissionPlayer(getIntValue("players."+uuid+".id"),getStringValue("players."+uuid+".name"),uuid);
        }
        return null;
    }

    @Override
    public PermissionPlayer getPermissionPlayer(String name) throws Exception {
        try{
            String id = getStringValue("nameplayers."+name.toLowerCase());
            if(id != null && id.length() > 0)
                return getPermissionPlayer(UUID.fromString(id));
        }catch (Exception ignored){}
        return null;
    }

    @Override
    public PermissionPlayer createPermissionPlayer(UUID uuid, String name) {
        int id = getIntValue("nextid");
        if(id == 0) id = 1;
        setValue("players."+uuid+".id",id);
        setValue("players."+uuid+".name",name);
        setValue("nameplayers."+name.toLowerCase(),""+uuid);
        setValue("nextid",id+1);
        save();
        return new PermissionPlayer(id,name,uuid);
    }

    @Override
    public void updateName(UUID uuid, String name) {
        setValue("nameplayers."+name.toLowerCase(),""+uuid);
        setValue("players."+uuid+".name",name);
        save();
    }
}
