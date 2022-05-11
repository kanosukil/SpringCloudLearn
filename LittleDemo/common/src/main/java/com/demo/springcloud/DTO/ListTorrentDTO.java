package com.demo.springcloud.DTO;

import java.util.List;

public class ListTorrentDTO {
    private List<TorrentDTO> torrents;

    public ListTorrentDTO(List<TorrentDTO> torrents) {
        this.torrents = torrents;
    }

    public ListTorrentDTO() {
    }

    public List<TorrentDTO> getTorrents() {
        return torrents;
    }

    public void setTorrents(List<TorrentDTO> torrents) {
        this.torrents = torrents;
    }

    @Override
    public String toString() {
        return "ListTorrentDTO{" +
                "torrents=" + torrents +
                '}';
    }
}
