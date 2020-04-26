/*
 * (C) Copyright 2019 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 28.10.19, 19:40
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft;

import net.pretronic.dkperms.api.DKPerms;
import net.pretronic.dkperms.api.minecraft.player.PermissionPlayer;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.holder.PermissionHolderFactory;
import net.pretronic.dkperms.api.object.holder.PermissionObjectHolder;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.dkperms.api.scope.PermissionScope;
import net.pretronic.dkperms.api.scope.PermissionScopeManager;
import net.pretronic.dkperms.common.DefaultDKPerms;
import net.pretronic.dkperms.common.DefaultMigrationAssistant;
import net.pretronic.dkperms.common.entity.DefaultPermissionEntity;
import net.pretronic.dkperms.common.entity.DefaultPermissionGroupEntity;
import net.pretronic.dkperms.common.logging.DefaultAuditLog;
import net.pretronic.dkperms.common.object.DefaultPermissionObject;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectManager;
import net.pretronic.dkperms.common.object.DefaultPermissionObjectType;
import net.pretronic.dkperms.common.object.meta.DefaultObjectMetaEntry;
import net.pretronic.dkperms.common.scope.DefaultPermissionScope;
import net.pretronic.dkperms.common.scope.DefaultPermissionScopeManager;
import net.pretronic.dkperms.common.storage.PDQStorage;
import net.pretronic.dkperms.minecraft.commands.permission.PermissionCommand;
import net.pretronic.dkperms.minecraft.config.DKPermsConfig;
import net.pretronic.dkperms.minecraft.migration.DKPermsLegacyMigration;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriber;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriberRegistry;
import net.pretronic.libraries.plugin.description.PluginVersion;
import net.pretronic.libraries.plugin.lifecycle.Lifecycle;
import net.pretronic.libraries.plugin.lifecycle.LifecycleState;
import net.pretronic.libraries.plugin.service.ServicePriority;
import net.pretronic.libraries.synchronisation.UnconnectedSynchronisationCaller;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.concurrent.AsyncExecutor;
import net.pretronic.libraries.utility.io.FileUtil;
import net.pretronic.libraries.utility.io.IORuntimeException;
import org.mcnative.common.McNative;
import org.mcnative.common.event.player.login.MinecraftPlayerLoginEvent;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.plugin.MinecraftPlugin;
import org.mcnative.common.serviceprovider.permission.PermissionProvider;

import java.io.File;
import java.util.function.Function;

public class DKPermsPlugin extends MinecraftPlugin {

    @Lifecycle(state = LifecycleState.BOOTSTRAP)
    public void onBootstrap(LifecycleState state){
        getLogger().info("DKPerms is starting, please wait..");
        copyLegacyConfig();

        getConfiguration().load(DKPermsConfig.class);

        PluginVersion version = getDescription().getVersion();

        DefaultPermissionScopeManager scopeManager = new DefaultPermissionScopeManager();
        DefaultPermissionObjectManager objectManager = new DefaultPermissionObjectManager();

        DKPerms dkPerms = new DefaultDKPerms(version.getName()
                ,version.getBuild(),getLogger()
                ,new DefaultMigrationAssistant()
                ,new PDQStorage(getDatabaseOrCreate())
                ,new DefaultAuditLog()
                ,scopeManager
                ,objectManager
                ,new AsyncExecutor(GeneralUtil.getDefaultExecutorService())
                ,new MinecraftFormatter());

        DKPerms.setInstance(dkPerms);

        if(getRuntime().isNetworkAvailable()){
            getRuntime().getNetwork().getMessenger().registerSynchronizingChannel("dkperms_objects"
                    ,this,Integer.class,objectManager.getObjects());
            getRuntime().getNetwork().getMessenger().registerSynchronizingChannel("dkperms_scopes",
                    this,Integer.class,scopeManager.getScopeSynchronizer());
            DefaultPermissionObject.SYNCHRONISATION_CALLER = objectManager.getObjects().getCaller();
        }else{
            objectManager.getObjects().initUnconnected();
            scopeManager.getScopeSynchronizer().init(new UnconnectedSynchronisationCaller<>(true));
        }

        scopeManager.initialise(dkPerms);
        objectManager.initialise(dkPerms);

        objectManager.getTypeOrCreate(DKPermsConfig.OBJECT_PLAYER_TYPE_NAME,false).setLocalHolderFactory(new UserFactory());
        objectManager.getTypeOrCreate(DKPermsConfig.OBJECT_GROUP_TYPE_NAME,true).setLocalHolderFactory(new GroupFactory());

        DKPermsConfig.load();

        findCurrentInstanceScope(scopeManager);

        getRuntime().getRegistry().registerService(this,PermissionProvider.class,new DKPermsPermissionProvider(),ServicePriority.HIGHEST);
        getRuntime().getPlayerManager().registerPlayerAdapter(PermissionPlayer.class,new PlayerAdapter());

        getRuntime().getLocal().getEventBus().subscribe(this, MinecraftPlayerLoginEvent.class
                ,event -> event.getPlayer().getPermissionHandler());

        registerCommands();
        registerDescribers();

        dkPerms.getMigrationAssistant().registerMigration(new DKPermsLegacyMigration());

        getLogger().info("DKPerms successfully started");
    }

    @Lifecycle(state = LifecycleState.SHUTDOWN)
    public void onShutdown(LifecycleState state){
        getLogger().info("DKPerms is shutting down, please wait..");
        DKPerms.setInstance(null);
        getLogger().info("DKPerms successfully shutted down");
    }

    private void copyLegacyConfig(){
        File configLocation = new File("plugins/DKPerms/config.yml");

        if(configLocation.exists()) {
            Document oldConfig = DocumentFileType.YAML.getReader().read(configLocation);

            if(oldConfig.contains("storage.mysql")) {
                getLogger().info("DKPerms Legacy detected");

                File legacyConfigLocation = new File("plugins/DKPerms/legacy-config.yml");
                try{
                    FileUtil.copyFile(configLocation, legacyConfigLocation);
                    boolean success = configLocation.delete();
                    if(success) {
                        getLogger().info("DKPerms Legacy config successfully copied to legacy-config.yml");
                        return;
                    }
                }catch (IORuntimeException e){
                    e.printStackTrace();
                }
                getLogger().error("DKPerms Legacy config could not be copied to legacy-config.yml");
            }
        }
    }

    private void findCurrentInstanceScope(PermissionScopeManager scopeManager){
        if(DKPermsConfig.SCOPE_CURRENT_INSTANCE_DYNAMIC){
            String serverName = McNative.getInstance().getLocal().getName();

            String group = serverName.split(DKPermsConfig.SCOPE_SERVER_GROUP_SPLIT)[0];

            PermissionScope groupScope = scopeManager.getNamespace(DKPermsConfig.SCOPE_NAMESPACE)
                    .getChild("serverGroup",group);
            PermissionScope scope = groupScope.getChild("server",getName());
            scopeManager.setCurrentInstanceScope(scope);
            DKPermsConfig.SCOPE_CURRENT_INSTANCE_SCOPE = scope;
            DKPermsConfig.SCOPE_CURRENT_GROUP_SCOPE = groupScope;
        }else{
            String pathInstance = DKPermsConfig.SCOPE_NAMESPACE+"\\"+DKPermsConfig.SCOPE_CURRENT_INSTANCE_SCOPE_NAME;
            String pathGroup = DKPermsConfig.SCOPE_NAMESPACE+"\\"+DKPermsConfig.SCOPE_CURRENT_GROUP_SCOPE_NAME;
            scopeManager.setCurrentInstanceScope(scopeManager.get(pathInstance));
            DKPermsConfig.SCOPE_CURRENT_GROUP_SCOPE = scopeManager.get(pathGroup);
        }

        DKPermsConfig.MCNATIVE_MANAGEMENT_SCOPE_GROUP = setupManagementScope(scopeManager,DKPermsConfig.MCNATIVE_MANAGEMENT_SCOPE_GROUP_NAME);
        DKPermsConfig.MCNATIVE_MANAGEMENT_SCOPE_PERMISSION = setupManagementScope(scopeManager,DKPermsConfig.MCNATIVE_MANAGEMENT_SCOPE_PERMISSION_NAME);
        DKPermsConfig.MCNATIVE_MANAGEMENT_SCOPE_OPERATOR = setupManagementScope(scopeManager,DKPermsConfig.MCNATIVE_MANAGEMENT_SCOPE_OPERATOR_NAME);
    }

    private PermissionScope setupManagementScope(PermissionScopeManager scopeManager,String path){
        if(path.equalsIgnoreCase("{global}")){
            return scopeManager.getNamespace(DKPermsConfig.SCOPE_NAMESPACE);
        }else  if(path.equalsIgnoreCase("{currentGroup}")){
            return DKPermsConfig.SCOPE_CURRENT_GROUP_SCOPE;
        }else  if(path.equalsIgnoreCase("{currentInstance}")){
            return DKPermsConfig.SCOPE_CURRENT_INSTANCE_SCOPE;
        }else{
            return scopeManager.get(path);
        }
    }

    private void registerCommands(){
       // if(!DKPermsConfig.SECURITY_COMMANDS_ENABLED) return;@Todo add after fix
        getRuntime().getLocal().getCommandManager().registerCommand(new PermissionCommand(this,DKPermsConfig.COMMAND_PERMISSION));
    }

    private void registerDescribers(){
        VariableDescriber<DefaultPermissionObject> objectDescriber = VariableDescriberRegistry.registerDescriber(DefaultPermissionObject.class);
        objectDescriber.registerFunction("uniqueId", DefaultPermissionObject::getAssignmentId);
        objectDescriber.registerParameterFunction("property", (object, key) ->{
           ObjectMetaEntry entry =  object.getMeta().get(key);
           return entry != null ?entry.getValue() : "";
        });

        objectDescriber.registerParameterFunction("boolProperty", (object, key) ->{
            ObjectMetaEntry entry =  object.getMeta().get(key);
            return entry != null ?entry.getBooleanValue() : "false";
        });

        objectDescriber.registerParameterFunction("numberProperty", (object, key) ->{
            ObjectMetaEntry entry =  object.getMeta().get(key);
            return entry != null ?entry.getLongValue(): "0";
        });

        objectDescriber.registerFunction("globalGroups", PermissionObject::getGroups);

        VariableDescriber<DefaultPermissionGroupEntity> groupEntityDescriber = VariableDescriberRegistry.registerDescriber(DefaultPermissionGroupEntity.class);
        groupEntityDescriber.setForwardFunction(DefaultPermissionGroupEntity::getGroup);

        VariableDescriberRegistry.registerDescriber(DefaultPermissionScope.class);
        VariableDescriberRegistry.registerDescriber(DefaultObjectMetaEntry.class);
        VariableDescriberRegistry.registerDescriber(DefaultPermissionEntity.class);
        VariableDescriberRegistry.registerDescriber(DefaultPermissionObjectType.class);
        VariableDescriberRegistry.registerDescriber(DKPermsPlayerDesign.class);
    }

    //@Tod update and optimize
    private static class PlayerAdapter implements Function<MinecraftPlayer, PermissionPlayer> {

        private PlayerAdapter(){}

        @Override
        public PermissionPlayer apply(MinecraftPlayer player) {
            PermissionObject object = DKPerms.getInstance().getObjectManager().getObjectByAssignment(player.getUniqueId());
            if(object == null){
                object = DKPerms.getInstance().getObjectManager().createObject(
                        DKPermsConfig.OBJECT_PLAYER_SCOPE
                        ,DKPermsConfig.OBJECT_PLAYER_TYPE
                        ,player.getName(),player.getUniqueId());
            }else if(!object.getName().equals(player.getName())){
                object.setName(null,player.getName());
            }
            return object.getHolder(PermissionPlayer.class);
        }
    }

    private static class UserFactory implements PermissionHolderFactory {

        @Override
        public PermissionObjectHolder create(PermissionObject object) {
            return new DKPermsPermissionPlayer(object);
        }
    }

    private static class GroupFactory implements PermissionHolderFactory {

        @Override
        public PermissionObjectHolder create(PermissionObject object) {
            return new DKPermsPermissionGroup(object);
        }
    }

}
