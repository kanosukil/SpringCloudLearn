package com.demo.springcloud.vo;

import com.demo.springcloud.DTO.TorrentDTO;

import java.util.List;

public class ResultVO {
    private Integer code;
    private List<TorrentDTO> torrentVOS;
    private String msg;

    public ResultVO() {
    }

    public ResultVO(Integer code, List<TorrentDTO> torrentVOS, String msg) {
        this.code = code;
        this.torrentVOS = torrentVOS;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<TorrentDTO> getTorrentVOS() {
        return torrentVOS;
    }

    public void setTorrentVOS(List<TorrentDTO> torrentVOS) {
        this.torrentVOS = torrentVOS;
    }

    @Override
    public String toString() {
        return "ResultDTO{" +
                "code=" + code +
                ", torrentVOS=" + torrentVOS +
                ", msg=" + msg +
                '}';
    }
}
