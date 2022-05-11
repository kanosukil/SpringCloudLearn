package com.cloud.learn.openfeignclient;

import org.springframework.stereotype.Component;

/**
 * @author VHBin
 * @date 2022/03/14 - 18:33
 */

@Component
public class HystrixClientFallBack implements HystrixClient {
    @Override
    public String demo(Integer id) {
        return "Wrong! The Server of ID:" + id + " is unavailable.";
    }
}
