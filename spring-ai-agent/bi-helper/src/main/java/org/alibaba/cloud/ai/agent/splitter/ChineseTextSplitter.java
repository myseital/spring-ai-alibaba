package org.alibaba.cloud.ai.agent.splitter;

import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author myseital
 * @date 2026/4/20
 */
public class ChineseTextSplitter extends TextSplitter {

    // 匹配中文常见分句符号（。！？；）
    private static final Pattern SENTENCE_SPLIT_PATTERN = Pattern.compile("(?<=[。！？；])");

    private final int chunkSize;
    private final int chunkOverlap;

    public ChineseTextSplitter(int chunkSize, int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    @Override
    protected List<String> splitText(String text) {
        List<String> segments = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return segments;
        }
        // 按句子切分
        String[] sentences = SENTENCE_SPLIT_PATTERN.split(text);
        StringBuilder currentChunk = new StringBuilder();
        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > chunkSize) {
                // 添加一个分块
                segments.add(currentChunk.toString().trim());

                // 处理重叠部分
                String chunk = currentChunk.toString();
                if (chunk.length() > chunkOverlap) {
                    currentChunk = new StringBuilder(chunk.substring(chunk.length() - chunkOverlap));
                } else {
                    currentChunk = new StringBuilder();
                }
            }
            currentChunk.append(sentence);
        }

        // 循环结束，追加最后剩余分块
        if (!currentChunk.isEmpty()) {
            segments.add(currentChunk.toString().trim());
        }

        return segments;
    }
}
