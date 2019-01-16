package net.heyzeer0.aladdin.profiles.custom.osu;

/**
 * Created by HeyZeer0 on 18/06/2018.
 * Copyright © HeyZeer0 - 2016
 */

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

    public String getBeatmapset_id() {
        return beatmapset_id;
    }

    public String getBeatmap_id() {
        return beatmap_id;
    }

    public String getApproved() {
        return approved;
    }

    public String getTotal_length() {
        return total_length;
    }

    public String getHit_length() {
        return hit_length;
    }

    public String getVersion() {
        return version;
    }

    public String getFile_md5() {
        return file_md5;
    }

    public String getDiff_size() {
        return diff_size;
    }

    public String getDiff_overall() {
        return diff_overall;
    }

    public String getDiff_approach() {
        return diff_approach;
    }

    public String getDiff_drain() {
        return diff_drain;
    }

    public String getMode() {
        return mode;
    }

    public String getApproved_date() {
        return approved_date;
    }

    public String getLast_update() {
        return last_update;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    public String getBpm() {
        return bpm;
    }

    public String getSource() {
        return source;
    }

    public String getTags() {
        return tags;
    }

    public String getGenre_id() {
        return genre_id;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public String getFavourite_count() {
        return favourite_count;
    }

    public String getPlaycount() {
        return playcount;
    }

    public String getPasscount() {
        return passcount;
    }

    public String getMax_combo() {
        return max_combo;
    }

    public String getDifficultyrating() {
        return difficultyrating;
    }
}
