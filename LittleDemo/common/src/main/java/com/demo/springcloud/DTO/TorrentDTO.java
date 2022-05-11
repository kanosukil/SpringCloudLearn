package com.demo.springcloud.DTO;

public class TorrentDTO {
    private String title;
    private String size;
    private String torrent;

    public TorrentDTO(String title, String size, String torrent) {
        this.title = title;
        this.size = size;
        this.torrent = torrent;
    }

    public TorrentDTO() {
    }

    @Override
    public String toString() {
        return "TorrentVO{" +
                "title='" + title + '\'' +
                ", size='" + size + '\'' +
                ", torrent='" + torrent + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTorrent() {
        return torrent;
    }

    public void setTorrent(String torrent) {
        this.torrent = torrent;
    }
}
