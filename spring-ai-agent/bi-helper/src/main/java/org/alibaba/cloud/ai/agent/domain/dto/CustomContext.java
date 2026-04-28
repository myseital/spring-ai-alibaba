package org.alibaba.cloud.ai.agent.domain.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Stack;

/**
 *
 * @author myseital
 * @date 2026/4/29
 */
@Data
@Component
public class CustomContext {

    private Stack<String> msg = new Stack<>();

}
