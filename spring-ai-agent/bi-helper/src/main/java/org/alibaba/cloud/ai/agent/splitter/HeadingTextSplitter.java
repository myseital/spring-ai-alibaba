package org.alibaba.cloud.ai.agent.splitter;

import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author myseital
 * @date 2026/4/20
 */
public class HeadingTextSplitter extends TextSplitter {

    //匹配Word 文档的所有数字编号标题:1、1.1、2.3.4等
    private static final Pattern HEADING_PATTERN = Pattern.compile("^\\d+(?:\\.\\d+)*\\s+.*$", Pattern.MULTILINE);

    @Override
    protected List<String> splitText(String text) {
        List<String> blocks = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return blocks;
        }
        Matcher matcher = HEADING_PATTERN.matcher(text);
        List<Integer> starts = new ArrayList<>();
        //记录每个标题的起始素引
        while (matcher.find()) {
            starts.add(matcher.start());
        }
        // 没有任何标题:整个文本作为一个块返回
        if (starts.isEmpty()) {
            blocks.add(text.trim());
            return blocks;
        }
        //如果第一条标题不是从文本开头开始，则把开头到第一标题之间的前言也作为一个块
        int firstStart = starts.get(0);
        if (firstStart > 0) {
            String preamble = text.substring(0, firstStart).trim();
            if (!preamble.isEmpty()) {
                blocks.add(preamble);
            }
        }
        //按标题起点切分，标题包含在每个块中
        for (int i = 0; i < starts.size(); i++) {
            int start = starts.get(i);
            int end = (i + 1 < starts.size()) ? starts.get(i + 1) : text.length();
            String block = text.substring(start, end).trim();
            if (!block.isEmpty()) {
                blocks.add(block);
            }
        }

        return blocks;
    }
}
