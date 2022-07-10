package com.moon.tinybilibili.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author Chanmoey
 * @date 2022年07月08日
 */
@Getter
@Setter
@AllArgsConstructor
public class PageResult<T> {

    private Integer total;

    private List<T> list;

}
