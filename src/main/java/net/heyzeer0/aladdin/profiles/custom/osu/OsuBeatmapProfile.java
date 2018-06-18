package net.heyzeer0.aladdin.profiles.custom.osu;

import lombok.Getter;

/**
 * Created by HeyZeer0 on 18/06/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Getter
public class OsuBeatmapProfile {

    String beatmapset_id;
    String beatmap_id;
    String approved;
    String total_length;
    String hit_length;
    String version;
    String file_md5;
    String diff_size;
    String diff_overall;
    String diff_approach;
    String diff_drain;
    String mode;
    String approved_date;
    String last_update;
    String artist;
    String title;
    String creator;
    String bpm;
    String source;
    String tags;
    String genre_id;
    String language_id;
    String favourite_count;
    String playcount;
    String passcount;
    String max_combo;
    String difficultyrating;

    public OsuBeatmapProfile(String beatmapset_id, String beatmap_id, String approved, String total_length, String hit_length, String version, String file_md5, String diff_size, String diff_overall, String diff_approach, String diff_drain, String mode, String approved_date, String last_update, String artist, String title, String creator, String bpm, String source, String tags, String genre_id, String language_id, String favourite_count, String playcount, String passcount, String max_combo, String difficultyrating) {
        this.beatmapset_id = beatmapset_id;
        this.beatmap_id = beatmap_id;
        this.approved = approved;
        this.total_length = total_length;
        this.hit_length = hit_length;
        this.version = version;
        this.file_md5 = file_md5;
        this.diff_size = diff_size;
        this.diff_overall = diff_overall;
        this.diff_approach = diff_approach;
        this.diff_drain = diff_drain;
        this.mode = mode;
        this.approved_date = approved_date;
        this.last_update = last_update;
        this.artist = artist;
        this.title = title;
        this.creator = creator;
        this.bpm = bpm;
        this.source = source;
        this.tags = tags;
        this.genre_id = genre_id;
        this.language_id = language_id;
        this.favourite_count = favourite_count;
        this.playcount = playcount;
        this.passcount = passcount;
        this.max_combo = max_combo;
        this.difficultyrating = difficultyrating;
    }

}
