/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 19.04.20, 13:31
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkperms.minecraft.commands.permission;

import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.plugin.MinecraftPlugin;
import org.mcnative.common.text.Text;
import org.mcnative.common.text.format.TextColor;

public class InfoCommand extends BasicCommand {

    public InfoCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("info","i","information","version","v"));
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {//&8» &9DKPerms &8|&f
        sender.sendMessage(Text.newBuilder()
                .color(TextColor.DARK_GRAY).text("» ")
                .color(TextColor.BLUE).text("DKPerms")
                .color(TextColor.DARK_GRAY).text(" | ")
                .color(TextColor.GRAY).text("PermissionSystem v")
                .color(TextColor.RED).text(((MinecraftPlugin)getOwner()).getDescription().getVersion().getName())
                .color(TextColor.GRAY).text(" by ")
                .color(TextColor.RED).text(((MinecraftPlugin)getOwner()).getDescription().getAuthor())
                .build());
    }
}
