/*
 * Developed by HeyZeer0 on 3/8/19 6:56 PM.
 * Last Modification 3/8/19 6:56 PM.
 *
 * Copyright HeyZeer0 (c) 2019.
 * This project is over AGLP 3.0 License.
 */

package net.heyzeer0.aladdin.events;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.channel.category.CategoryCreateEvent;
import net.dv8tion.jda.core.events.channel.category.CategoryDeleteEvent;
import net.dv8tion.jda.core.events.channel.category.GenericCategoryEvent;
import net.dv8tion.jda.core.events.channel.category.update.CategoryUpdateNameEvent;
import net.dv8tion.jda.core.events.channel.category.update.GenericCategoryUpdateEvent;
import net.dv8tion.jda.core.events.channel.text.GenericTextChannelEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.text.update.GenericTextChannelUpdateEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.channel.voice.GenericVoiceChannelEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.voice.update.GenericVoiceChannelUpdateEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateBitrateEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateUserLimitEvent;
import net.dv8tion.jda.core.events.guild.GenericGuildEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.*;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.user.GenericUserEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.heyzeer0.aladdin.Main;
import net.heyzeer0.aladdin.commands.ChatClearCommand;
import net.heyzeer0.aladdin.enums.LogModules;
import net.heyzeer0.aladdin.utils.ImageUtils;
import net.heyzeer0.aladdin.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Optional;

public class LogEvents implements EventListener {

    private static final Map<String, Optional<CachedMessage>> messageCache = Utils.createCache(35000);

    @Override
    public void onEvent(Event e) {
        if(Main.getDatabase() == null || !Main.getDatabase().isReady()) {
            return;
        }

        if(e instanceof GenericGuildMessageEvent) {
            if(!isModuleActive(((GenericGuildMessageEvent) e).getGuild(), LogModules.MESSAGE_MODULE)) {
                return;
            }
            if(e instanceof GuildMessageReceivedEvent) {
                GuildMessageReceivedEvent ev = (GuildMessageReceivedEvent)e;
                if(ev.getAuthor().isBot() || ev.getAuthor().isFake()) {
                    return;
                }
                messageCache.put(ev.getMessageId(), Optional.of(new CachedMessage(ev.getMessage().getContentDisplay(), ev.getAuthor().getName() + "#" + ev.getAuthor().getDiscriminator(), ev.getAuthor().getEffectiveAvatarUrl(), ev.getAuthor().getId())));
                return;
            }
            if(e instanceof GuildMessageDeleteEvent) {
                GuildMessageDeleteEvent ev = (GuildMessageDeleteEvent)e;

                if(System.currentTimeMillis() - ChatClearCommand.last_deletion.getOrDefault(ev.getGuild().getId(), 0L) < 2000) {
                   return;
                }

                try{
                    CachedMessage cache = messageCache.get(ev.getMessageId()).orElse(null);

                    if(cache != null && !cache.getMessage().isEmpty()) {
                        Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                                new EmbedBuilder().setAuthor(cache.getAuthor_name(), null, cache.getAuthor_url())
                                        .setColor(Color.RED)
                                        .setDescription("The user " + cache.getAuthor_name() + " deleted their message ```" + cache.getMessage() + "```")
                                        .setFooter("ID: " + cache.getAuthor_id() + " #" + ev.getChannel().getName(), null)); }
                }catch (Exception ignored) {}
            }
            if(e instanceof GuildMessageUpdateEvent) {
                GuildMessageUpdateEvent ev = (GuildMessageUpdateEvent)e;
                if(ev.getAuthor().isBot() || ev.getAuthor().isFake()) {
                    return;
                }

                try{
                    CachedMessage old_message = messageCache.get(ev.getMessageId()).orElse(null);

                    if(old_message != null && !old_message.getMessage().isEmpty()) {
                        Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                                new EmbedBuilder().setAuthor(ev.getAuthor().getName() + "#" + ev.getAuthor().getDiscriminator(), null, ev.getAuthor().getEffectiveAvatarUrl())
                                        .setColor(Color.YELLOW)
                                        .setDescription("The user " + ev.getAuthor().getAsMention() + " changed their message from ```" + old_message.getMessage() + "```to```" + ev.getMessage().getContentDisplay() + "```")
                                        .setFooter("ID: " + ev.getAuthor().getId() + " #" + ev.getChannel().getName(), null).setTimestamp(ev.getMessage().getEditedTime()));
                    }
                }catch (Exception ignored) {}
            }
            return;
        }
        if(e instanceof GenericUserEvent) {
            if(e instanceof UserUpdateAvatarEvent) {
                UserUpdateAvatarEvent ev = (UserUpdateAvatarEvent)e;
                if(ev.getUser().isBot() || ev.getUser().isFake()) return;;

                Utils.runAsync(() -> {
                    try{
                        BufferedImage before = null;
                        try{
                            before = ImageUtils.getImageFromUrl(ev.getOldAvatarUrl().replace("jpg", "png"));
                        }catch (Exception ex) {
                            try{
                                before = ImageUtils.getImageFromUrl(ev.getOldAvatarUrl());
                            }catch (Exception ex2) { }
                        }

                        if(before == null) {
                            return;
                        }

                        BufferedImage actual = null;
                        try{
                            actual = ImageUtils.getImageFromUrl(ev.getNewAvatarUrl());
                        }catch (Exception ex) { }

                        if(actual == null) {
                            return;
                        }

                        BufferedImage inputImage = ImageIO.read(new FileInputStream(new File(Main.getDataFolder(), "images" + File.separator + "update_avatar.png")));

                        Graphics g = inputImage.createGraphics();
                        g.drawImage(inputImage,0,0,null);
                        g.drawImage(before,11,8, null);
                        g.drawImage(actual,290,8, null);

                        g.dispose();
                        for(Guild g2 : Main.getMutualGuilds(ev.getUser())) {
                            if(!isModuleActive(g2, LogModules.MEMBER_MODULE)) {
                                continue;
                            }

                            Main.getDatabase().getGuildProfile(g2).sendLogMessage(g2, inputImage,
                                    new EmbedBuilder().setAuthor(ev.getUser().getName() + "#" + ev.getUser().getDiscriminator(), null, ev.getUser().getEffectiveAvatarUrl())
                                            .setColor(Color.GREEN)
                                            .setDescription(ev.getUser().getAsMention() + " Just changed their avatar")
                                            .setFooter("ID: " + ev.getUser().getId(), null));
                        }


                    }catch (Exception ex) { ex.printStackTrace(); }
                });

            }
        }
        if(e instanceof GenericGuildMemberEvent) {
            if(e instanceof GuildMemberJoinEvent) {
                if(!isModuleActive(((GenericGuildMemberEvent) e).getGuild(), LogModules.MEMBER_MODULE)) {
                    return;
                }

                GenericGuildMemberEvent ev = (GenericGuildMemberEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getUser().getName() + "#" + ev.getUser().getDiscriminator(), null, ev.getUser().getEffectiveAvatarUrl())
                                .setColor(Color.GREEN)
                                .setDescription(ev.getUser().getAsMention() + " Joined the guild")
                                .setFooter("ID: " + ev.getUser().getId(), null));
                return;

            }
            if(e instanceof GuildMemberLeaveEvent) {
                if(!isModuleActive(((GenericGuildMemberEvent) e).getGuild(), LogModules.MEMBER_MODULE)) {
                    return;
                }

                GuildMemberLeaveEvent ev = (GuildMemberLeaveEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getUser().getName() + "#" + ev.getUser().getDiscriminator(), null, ev.getUser().getEffectiveAvatarUrl())
                                .setColor(Color.RED)
                                .setDescription(ev.getUser().getName() + " Left the guild")
                                .setFooter("ID: " + ev.getUser().getId(), null));
                return;
            }
            if(e instanceof GuildMemberNickChangeEvent) {
                if(!isModuleActive(((GenericGuildMemberEvent) e).getGuild(), LogModules.MEMBER_MODULE)) {
                    return;
                }

                GuildMemberNickChangeEvent ev = (GuildMemberNickChangeEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getUser().getName() + "#" + ev.getUser().getDiscriminator(), null, ev.getUser().getEffectiveAvatarUrl())
                                .setColor(Color.YELLOW)
                                .setDescription(ev.getUser().getAsMention() + " Changed their nick from ```" + ev.getPrevNick() + "```to```" + ev.getNewNick() + "```")
                                .setFooter("ID: " + ev.getUser().getId(), null));
                return;
            }
            if(e instanceof GuildMemberRoleAddEvent) {
                if(!isModuleActive(((GenericGuildMemberEvent) e).getGuild(), LogModules.ROLE_MODULE)) {
                    return;
                }

                GuildMemberRoleAddEvent ev = (GuildMemberRoleAddEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getUser().getName() + "#" + ev.getUser().getDiscriminator(), null, ev.getUser().getEffectiveAvatarUrl())
                                .setColor(Color.GREEN)
                                .setDescription(ev.getUser().getAsMention() + " got the role " + ev.getRoles().get(0).getAsMention())
                                .setFooter("ID: " + ev.getUser().getId(), null));
                return;
            }
            if(e instanceof GuildMemberRoleRemoveEvent) {
                if(!isModuleActive(((GenericGuildMemberEvent) e).getGuild(), LogModules.ROLE_MODULE)) {
                    return;
                }

                GuildMemberRoleRemoveEvent ev = (GuildMemberRoleRemoveEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getUser().getName() + "#" + ev.getUser().getDiscriminator(), null, ev.getUser().getEffectiveAvatarUrl())
                                .setColor(Color.RED)
                                .setDescription(ev.getUser().getAsMention() + " lost the role " + ev.getRoles().get(0).getAsMention())
                                .setFooter("ID: " + ev.getUser().getId(), null));
            }
            return;
        }
        if(e instanceof GenericGuildVoiceEvent) {
            if(!isModuleActive(((GenericGuildVoiceEvent) e).getGuild(), LogModules.VOICE_MODULE)) {
                return;
            }

            if(((GenericGuildVoiceEvent) e).getMember().getUser().isBot() || ((GenericGuildVoiceEvent) e).getMember().getUser().isFake()) {
                return;
            }

            if(e instanceof GuildVoiceJoinEvent) {
                GuildVoiceJoinEvent ev = (GuildVoiceJoinEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getMember().getUser().getName() + "#" + ev.getMember().getUser().getDiscriminator(), null, ev.getMember().getUser().getEffectiveAvatarUrl())
                                .setColor(Color.GREEN)
                                .setDescription(ev.getMember().getUser().getAsMention() + " joined the voice channel " + ev.getChannelJoined().getName())
                                .setFooter("ID: " + ev.getMember().getUser().getId(), null));
                return;
            }
            if(e instanceof GuildVoiceLeaveEvent) {
                GuildVoiceLeaveEvent ev = (GuildVoiceLeaveEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getMember().getUser().getName() + "#" + ev.getMember().getUser().getDiscriminator(), null, ev.getMember().getUser().getEffectiveAvatarUrl())
                                .setColor(Color.RED)
                                .setDescription(ev.getMember().getUser().getAsMention() + " left the voice channel " + ev.getChannelLeft().getName())
                                .setFooter("ID: " + ev.getMember().getUser().getId(), null));
                return;
            }
            if(e instanceof GuildVoiceMoveEvent) {
                GuildVoiceMoveEvent ev = (GuildVoiceMoveEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getMember().getUser().getName() + "#" + ev.getMember().getUser().getDiscriminator(), null, ev.getMember().getUser().getEffectiveAvatarUrl())
                                .setColor(Color.YELLOW)
                                .setDescription(ev.getMember().getUser().getAsMention() + " was moved from ``" + ev.getChannelLeft().getName() + "`` to ``" + ev.getChannelJoined().getName() + "``")
                                .setFooter("ID: " + ev.getMember().getUser().getId(), null));
                return;
            }
        }
        if(e instanceof GenericTextChannelEvent) {
            if(!isModuleActive(((GenericTextChannelEvent) e).getGuild(), LogModules.ACTION_MODULE)) {
                return;
            }
            if(e instanceof TextChannelCreateEvent) {
                TextChannelCreateEvent ev = (TextChannelCreateEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getGuild().getName(), null, ev.getGuild().getIconUrl())
                                .setColor(Color.GREEN)
                                .setDescription("The channel " + ev.getChannel().getAsMention() + " just got created.")
                                .setFooter("ID: " + ev.getGuild().getId(), null));
                return;
            }
            if(e instanceof TextChannelDeleteEvent) {
                TextChannelDeleteEvent ev = (TextChannelDeleteEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getGuild().getName(), null, ev.getGuild().getIconUrl())
                                .setColor(Color.RED)
                                .setDescription("The channel " + ev.getChannel().getName() + " just got deleted.")
                                .setFooter("ID: " + ev.getGuild().getId(), null));
                return;
            }
        }
        if(e instanceof GenericTextChannelUpdateEvent) {
            if(!isModuleActive(((GenericTextChannelUpdateEvent) e).getGuild(), LogModules.ACTION_MODULE)) {
                return;
            }
            if(e instanceof TextChannelUpdateNameEvent) {
                TextChannelUpdateNameEvent ev = (TextChannelUpdateNameEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getGuild().getName(), null, ev.getGuild().getIconUrl())
                                .setColor(Color.YELLOW)
                                .setDescription("The channel " + ev.getChannel().getAsMention() + " was renamed from ``" + ev.getOldName() + "``.")
                                .setFooter("ID: " + ev.getGuild().getId(), null));
                return;
            }
        }
        if(e instanceof GenericCategoryEvent) {
            if (!isModuleActive(((GenericCategoryEvent) e).getGuild(), LogModules.ACTION_MODULE)) {
                return;
            }
            if(e instanceof CategoryCreateEvent) {
                CategoryCreateEvent ev = (CategoryCreateEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getGuild().getName(), null, ev.getGuild().getIconUrl())
                                .setColor(Color.GREEN)
                                .setDescription("The category ``" + ev.getCategory().getName() + "`` was created.")
                                .setFooter("ID: " + ev.getGuild().getId(), null));
                return;
            }
            if(e instanceof CategoryDeleteEvent) {
                CategoryDeleteEvent ev = (CategoryDeleteEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getGuild().getName(), null, ev.getGuild().getIconUrl())
                                .setColor(Color.RED)
                                .setDescription("The category ``" + ev.getCategory().getName() + "`` was deleted.")
                                .setFooter("ID: " + ev.getGuild().getId(), null));
                return;
            }
        }
        if(e instanceof GenericCategoryUpdateEvent) {
            if (!isModuleActive(((GenericCategoryUpdateEvent) e).getGuild(), LogModules.ACTION_MODULE)) {
                return;
            }
            if(e instanceof CategoryUpdateNameEvent) {
                CategoryUpdateNameEvent ev = (CategoryUpdateNameEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getGuild().getName(), null, ev.getGuild().getIconUrl())
                                .setColor(Color.YELLOW)
                                .setDescription("The category ``" + ev.getCategory().getName() + "`` was renamed from ``" + ev.getOldName() + "``.")
                                .setFooter("ID: " + ev.getGuild().getId(), null));
                return;
            }
        }
        if(e instanceof GenericVoiceChannelEvent) {
            if (!isModuleActive(((GenericVoiceChannelEvent) e).getGuild(), LogModules.ACTION_MODULE)) {
                return;
            }
            if(e instanceof VoiceChannelCreateEvent) {
                VoiceChannelCreateEvent ev = (VoiceChannelCreateEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getGuild().getName(), null, ev.getGuild().getIconUrl())
                                .setColor(Color.GREEN)
                                .setDescription("The voice channel ``" + ev.getChannel().getName() + "`` was created.")
                                .setFooter("ID: " + ev.getGuild().getId(), null));
                return;
            }
            if(e instanceof VoiceChannelDeleteEvent) {
                VoiceChannelDeleteEvent ev = (VoiceChannelDeleteEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getGuild().getName(), null, ev.getGuild().getIconUrl())
                                .setColor(Color.RED)
                                .setDescription("The voice channel ``" + ev.getChannel().getName() + "`` was deleted.")
                                .setFooter("ID: " + ev.getGuild().getId(), null));
                return;
            }
        }
        if(e instanceof GenericVoiceChannelUpdateEvent) {
            if (!isModuleActive(((GenericVoiceChannelUpdateEvent) e).getGuild(), LogModules.ACTION_MODULE)) {
                return;
            }
            if(e instanceof VoiceChannelUpdateNameEvent) {
                VoiceChannelUpdateNameEvent ev = (VoiceChannelUpdateNameEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getGuild().getName(), null, ev.getGuild().getIconUrl())
                                .setColor(Color.YELLOW)
                                .setDescription("The voice channel ``" + ev.getChannel().getName() + "`` was renamed from  ``" + ev.getOldName() + "``.")
                                .setFooter("ID: " + ev.getGuild().getId(), null));
                return;
            }
            if(e instanceof VoiceChannelUpdateBitrateEvent) {
                VoiceChannelUpdateBitrateEvent ev = (VoiceChannelUpdateBitrateEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getGuild().getName(), null, ev.getGuild().getIconUrl())
                                .setColor(Color.YELLOW)
                                .setDescription("The bitrate amount from ``" + ev.getChannel().getName() + "`` was changed from ``" + (ev.getOldBitrate()/1000) + "k`` to ``" + (ev.getChannel().getBitrate()/1000) + "k``.")
                                .setFooter("ID: " + ev.getGuild().getId(), null));
                return;
            }
            if(e instanceof VoiceChannelUpdateUserLimitEvent) {
                VoiceChannelUpdateUserLimitEvent ev = (VoiceChannelUpdateUserLimitEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getGuild().getName(), null, ev.getGuild().getIconUrl())
                                .setColor(Color.YELLOW)
                                .setDescription("The user amount of the channel ``" + ev.getChannel().getName() + "`` was changed from ``" + ev.getOldUserLimit() + "`` to ``" + ev.getChannel().getUserLimit() + "``")
                                .setFooter("ID: " + ev.getGuild().getId(), null));
                return;
            }
        }
        if(e instanceof GenericGuildEvent) {
            if (!isModuleActive(((GenericGuildEvent) e).getGuild(), LogModules.ACTION_MODULE)) {
                return;
            }
            if(e instanceof GuildBanEvent) {
                GuildBanEvent ev = (GuildBanEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getUser().getName() + "#" + ev.getUser().getDiscriminator(), null, ev.getUser().getEffectiveAvatarUrl())
                                .setColor(Color.RED)
                                .setDescription("The user ``" + ev.getUser().getName() + "`` was banned.")
                                .setFooter("ID: " + ev.getUser().getId(), null));
                return;
            }
            if(e instanceof GuildUnbanEvent) {
                GuildUnbanEvent ev = (GuildUnbanEvent)e;

                Main.getDatabase().getGuildProfile(ev.getGuild()).sendLogMessage(ev.getGuild(),
                        new EmbedBuilder().setAuthor(ev.getUser().getName() + "#" + ev.getUser().getDiscriminator(), null, ev.getUser().getEffectiveAvatarUrl())
                                .setColor(Color.GREEN)
                                .setDescription("The user ``" + ev.getUser().getName() + "`` was unbanned.")
                                .setFooter("ID: " + ev.getUser().getId(), null));
                return;
            }
        }
    }

    public static boolean isModuleActive(Guild g, LogModules module) {
        return Main.getDatabase().getGuildProfile(g).getGuild_log().getChannel_id() != null && Main.getDatabase().getGuildProfile(g).isLogModuleActive(module);
    }


    public class CachedMessage {

        String message;
        String author_name;
        String author_url;
        String author_id;

        public CachedMessage(String message, String author_name, String author_url, String author_id) {
            this.message = message; this.author_name = author_name; this.author_url = author_url; this.author_id = author_id;
        }

        public String getMessage() {
            return message;
        }

        public String getAuthor_name() {
            return author_name;
        }

        public String getAuthor_url() {
            return author_url;
        }

        public String getAuthor_id() {
            return author_id;
        }
    }

}
