package com.cloud.learn.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/03/12 - 15:37
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListVO {
    private List<String> list;
}
