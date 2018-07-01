package net.heyzeer0.aladdin.events.listeners;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.Region;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateOwnerEvent;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.database.entities.GuildProfile;
import net.heyzeer0.aladdin.enums.Lang;

import java.awt.*;

/**
 * Created by HeyZeer0 on 22/09/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class GuildListener {

    public static void onGuildJoin(GuildJoinEvent e) {
        GuildProfile pf = Main.getDatabase().getGuildProfile(e.getGuild());
        if(e.getGuild().getRegion() != Region.BRAZIL) {
            pf.updateLang(Lang.EN_US);
        }

        if(e.getGuild().getSelfMember().hasPermission(e.getGuild().getDefaultChannel(), Permission.MESSAGE_WRITE)) {
            e.getGuild().getDefaultChannel().sendMessage(pf.getSelectedLanguage().getLangProfile().get("guild.join.message")).queue();
        }



        Main.getLogger().embed(":house: Guild Join Event", "Guilda ``" + e.getGuild().getName() + "``\nUsers ``" + e.getGuild().getMembers().size() + "``\nDono ``" + e.getGuild().getOwner().getUser().getName() + "#" + e.getGuild().getOwner().getUser().getDiscriminator() + "``", Color.GREEN);
    }

    public static void onGuildLeave(GuildLeaveEvent e) {
        Main.getDatabase().getGuildProfile(e.getGuild()).deleteAsync();
        Main.getLogger().embed(":house: Guild Leave Event", "Guilda ``" + e.getGuild().getName() + "``\nUsers ``" + e.getGuild().getMembers().size() + "``", Color.GREEN);
    }

    public static void onMemberLeave(GuildMemberLeaveEvent e) {
        Main.getDatabase().getGuildProfile(e.getGuild()).cleanUserData(e.getMember().getUser());
    }

    public static void onOwnerUpdate(GuildUpdateOwnerEvent e) {
        Main.getDatabase().getGuildProfile(e.getGuild()).updateGuildOwner(e.getGuild().getOwner().getUser().getId());
    }

}
