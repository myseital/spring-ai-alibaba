package org.alibaba.cloud.ai.agent.nodes;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author myseital
 * @date 2026/4/22
 */
@Slf4j
public class ExecSqlAndCreateExcelNode implements NodeAction {

    private final JdbcTemplate jdbcTemplate;

    public ExecSqlAndCreateExcelNode(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String genSQL = state.value("genSQL", "");
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(genSQL);
        log.info("map=[{}]", JSONUtil.toJsonStr(maps));
        File file = generateExcel(maps);

        return Map.of("excelFile", file);
    }

    private File generateExcel(List<Map<String, Object>> rows) throws Exception {
        if (rows.size() == 0 || rows.isEmpty()) {
            throw new RuntimeException("SQL查询结果为空，无法生成Excel");
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("AI生成内容");
        Map<String, Object> firstRow = rows.get(0);
        List<String> columns = new ArrayList<>(firstRow.keySet());

        Row header = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            header.createCell(i).setCellValue(columns.get(i));
        }

        for (int i = 0; i < rows.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Map<String, Object> data = rows.get(i);
            for (int c = 0; c < columns.size(); c++) {
                Object value = data.get(c);
                row.createCell(c).setCellValue(value == null ? "" : value.toString());
            }
        }

        File file = File.createTempFile("report_", ".xlsx");

        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }

        return file;
    }
}
