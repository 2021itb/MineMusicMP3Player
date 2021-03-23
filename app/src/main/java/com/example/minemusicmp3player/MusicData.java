package com.example.minemusicmp3player;

import java.util.Objects;

public class MusicData {
    private String id;
    private String artist;
    private String title;
    private String albumArt;
    private String duration;    //이 다섯가지는 컨텐트 프로바이더에서 제공되는 것이고
    private int playCount;
    private int liked;  //이 두개는 내가 만들어준 것

    public MusicData() {
    }   //디폴트 생성자

    public MusicData(String id, String artist, String title, String albumArt, String duration, int playCount, int liked) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.albumArt = albumArt;
        this.duration = duration;
        this.playCount = playCount;
        this.liked = liked;
    }   //매개변수 있는 생성자  전체 파라미터

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    //musicData 7가지 멤버가 들어갔는데, id 값이 같으면 true, 다르면 false를 리턴하는 함수
    @Override
    public boolean equals(Object object) {

        boolean equal = false;

        if (object instanceof MusicData) {
            MusicData data = (MusicData) object;
            equal = (this.id).equals(data.getId()); //MusicData.getID()
        }

        return equal;
    }

}
