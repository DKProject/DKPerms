package ch.dkrieger.permissionsystem.lib.config;

/*
 *
 *  * Copyright (c) 2018 Davide Wietlisbach on 10.06.18 13:39
 *
 */

import ch.dkrieger.permissionsystem.lib.utils.Messages;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.reflect.TypeReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SimpleConfig {

    private final File file;
    private Document config;

    public SimpleConfig(File file) {
        this.file = file;

        try{
            file.getParentFile().mkdirs();
            if(!file.exists()) this.file.createNewFile();
        }catch (Exception exception){
            System.out.println(Messages.SYSTEM_PREFIX+"Could not create config file.");
            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
        }
    }
    public File getFile() {
        return file;
    }

    public void reloadConfig(){
        loadConfig();
    }

    public void loadConfig(){
        load();
        registerDefaults();
        save();
        onLoad();
    }

    public void save(){ }

    public void load(){
        if(file == null) return;
        try{
            if(file.exists()) file.createNewFile();
            this.config = DocumentFileType.YAML.getReader().read(this.file);
        }catch (Exception exception){
            System.out.println(Messages.SYSTEM_PREFIX+"Could not load config file.");
            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
        }
    }

    public void setValue(String path, Object value){
        this.config.set(path,value);
    }

    public void addValue(String path, Object value){
        if(!this.config.contains(path))this.config.set(path,value);
    }

    public String getStringValue(String path){
        return this.config.getString(path);
    }

    public int getIntValue(String path){
        return this.config.getInt(path);
    }

    public double getDoubleValue(String path){
        return this.config.getDouble(path);
    }

    public long getLongValue(String path){
        return this.config.getLong(path);
    }

    public boolean getBooleanValue(String path){
        return this.config.getBoolean(path);
    }

    public List<String> getStringListValue(String path){
        return this.config.getObject(path,new TypeReference<List<String>>(){}.getType());
    }

    public List<Integer> getIntListValue(String path){
        return this.config.getObject(path,new TypeReference<List<Integer>>(){}.getType());
    }

    public List<Double> getDoubleListValue(String path){
        return this.config.getObject(path,new TypeReference<List<Double>>(){}.getType());
    }

    public List<Long> getLongListValue(String path){
        return this.config.getObject(path,new TypeReference<List<Long>>(){}.getType());
    }

    public List<Boolean> getBooleanListValue(String path){
        return this.config.getObject(path,new TypeReference<List<Boolean>>(){}.getType());
    }
    public Collection<String> getKeys(String path){
        Document document = this.config.getDocument(path);
        return document != null ? document.keys() : new ArrayList<String>();
    }

    public boolean contains(String path){
        return this.config.contains(path);
    }

    public abstract void onLoad();

    public abstract void registerDefaults();
}
