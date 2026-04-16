import com.alibaba.cloud.ai.graph.GraphRepresentation;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author myseital
 * @date 2026/4/15
 */
public class PlantUmlTest {

    public static void main(String[] args) throws IOException {
        String uml = """
            @startuml
            title 人机协同流程
            User -> AI: 发起请求
            AI --> 系统: 生成结果
            系统 --> Admin: 发送邮件审核
            Admin --> 系统: 采纳/拒绝
            系统 --> DB: 执行最终操作
            @enduml
            """;

        // 转换并保存为 PNG
        convertToPng(uml, "F:\\projects\\java\\spring-ai-alibaba\\.temp\\temp.png");
        System.out.println("生成成功！");
    }

    public static void convertToPng(String plantUmlCode, String outputPath) throws IOException {
        // 关闭 PlantUML 安全检查（避免部分语法报错）
//        OptionFlags.SECRET_RECOMMENDED = true;

        // 读取 PlantUML 源码
        SourceStringReader reader = new SourceStringReader(plantUmlCode);

        try (ByteArrayOutputStream imageOutStream = new ByteArrayOutputStream();
             FileOutputStream fos = new FileOutputStream(outputPath)) {

            // 生成 PNG 图片流
            reader.outputImage(imageOutStream, new FileFormatOption(FileFormat.PNG));

            // 写入文件
            fos.write(imageOutStream.toByteArray());
            fos.flush();
        }
    }

    static java.awt.Image plantUML2PNG(String code) throws IOException {
        SourceStringReader reader = new SourceStringReader(code);

        try (ByteArrayOutputStream imageOutStream = new ByteArrayOutputStream()) {
            // 生成 PNG 图片流
            var description = reader.outputImage(imageOutStream, 0, new FileFormatOption(FileFormat.PNG));

            var imageInStream = new java.io.ByteArrayInputStream(imageOutStream.toByteArray());
            return javax.imageio.ImageIO.read(imageInStream);
        }
    }

    // 从 GraphRepresentation 生成图像
    static void displayDiagram(GraphRepresentation representation) throws IOException {
        java.awt.Image image = plantUML2PNG(representation.content());
//        display(image);
    }
}
