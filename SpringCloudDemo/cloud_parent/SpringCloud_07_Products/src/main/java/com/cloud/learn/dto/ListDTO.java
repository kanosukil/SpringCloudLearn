package com.cloud.learn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/03/12 - 15:39
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListDTO {
    private List<String> list;
}
