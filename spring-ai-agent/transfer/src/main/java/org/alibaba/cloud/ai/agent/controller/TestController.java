package org.alibaba.cloud.ai.agent.controller;

import org.alibaba.cloud.ai.common.domain.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author myseital
 * @date 2026/4/16
 */
@RestController
public class TestController {

    @Value("${count}")
    private Integer count;

    @GetMapping("/testNacos")
    public R testNacos() {
        return R.ok(count);
    }
}
