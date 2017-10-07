package net.heyzeer0.aladdin.commands;

import net.dv8tion.jda.core.entities.User;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.configs.MainConfig;
import net.heyzeer0.aladdin.enums.CommandResultEnum;
import net.heyzeer0.aladdin.enums.CommandType;
import net.heyzeer0.aladdin.interfaces.Command;
import net.heyzeer0.aladdin.interfaces.CommandExecutor;
import net.heyzeer0.aladdin.profiles.commands.ArgumentProfile;
import net.heyzeer0.aladdin.profiles.commands.CommandResult;
import net.heyzeer0.aladdin.profiles.commands.MessageEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.stream.Stream;

/**
 * Created by HeyZeer0 on 22/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class AdminCommand implements CommandExecutor {

    @Command(command = "admin", description = "Comandos sobre o bot", type = CommandType.BOT_ADMIN, isAllowedToDefault = false,
            usage = "")
    public CommandResult onCommand(ArgumentProfile args, MessageEvent e) {
        if(args.get(0).equalsIgnoreCase("update")) {
            e.sendMessage("Irei checar se possuo uma atualização, caso sim irei automaticamente atualizar, aguarde alguns segundos.");
            if(!checkUpdate(e)) {
                e.sendMessage(":x: Desculpe, eu não possuo nenhuma atualização.");
            }
            return new CommandResult(CommandResultEnum.SUCCESS);
        }
        if(args.get(0).equalsIgnoreCase("key")) {
            for(User u : e.getMessage().getMentionedUsers()) {
                Main.getDatabase().getUserProfile(u).addKeys(2);
            }
            return new CommandResult(CommandResultEnum.SUCCESS);
        }

        return new CommandResult(CommandResultEnum.SUCCESS);
    }

    public static boolean checkUpdate(MessageEvent event) {
        File old = new File("AladdinBOT-1.0.jar");
        File newer = new File(MainConfig.update_dir);

        if(newer.exists() && old.exists()) {
            if(old.lastModified() > newer.lastModified()) {
                return false;
            }
            event.sendMessage(":white_check_mark: **Processo concluido. Aguarde uns instantes estou atualizando.**");
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            try{
                                copyFile(newer, old);
                            }catch (Exception e) {e.printStackTrace();}
                        }
                    },
                    6000);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Stream.of(Main.getShards()).forEach(sc -> sc.getJDA().shutdown());
                            System.exit(0);
                        }
                    },
                    8000);
            return true;
        }
        return false;
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }

}
