# Excel 组合图表绘图仪

一个独立的 Java 应用程序，用于从 Excel 数据文件创建组合图表（柱形图 + 折线图）。

## 功能特性

- **Excel 数据导入**：加载带有列标题的 `.xlsx` 文件
- **灵活的图表配置**：
  - 选择任意列作为 X 轴数据
  - 添加多个 Y 轴数据系列
  - 为每个系列选择簇状柱形图或折线图
  - 支持次坐标轴
- **图片输出设置**：
  - 可配置宽度和高度（像素、毫米或厘米）
  - 自定义图表标题
  - 可选的图例显示
- **多语言支持**：美式英文和简体中文，支持实时切换
- **用户偏好持久化**：语言设置自动保存

## 截图

![主界面](docs/screenshots/main-interface.png)

## 系统要求

- **JDK 25** 或更高版本
- **Maven 3.9+**（用于构建）

## 依赖项

- JFreeChart 1.5.6
- Lombok 1.18.42
- [excel-io](https://github.com/user/excel-io)（自定义 Excel I/O 库）

## 构建

```bash
# 克隆仓库
git clone https://github.com/username/excel-combo-chart-plotter.git
cd excel-combo-chart-plotter

# 使用 Maven 构建
mvn clean package
```

可执行 JAR 文件将生成在：
```
target/excel-combo-chart-plotter-1.0.0-jar-with-dependencies.jar
```

## 运行

```bash
java -jar target/excel-combo-chart-plotter-1.0.0-jar-with-dependencies.jar
```

## 使用说明

1. **加载 Excel 数据**：点击"选择文件"选择 Excel 文件（.xlsx），然后点击"加载数据"
2. **配置 X 轴**：从下拉列表中选择 X 轴数据所在列
3. **添加 Y 轴系列**：
   - 点击"添加系列"添加新的数据系列
   - 为每个系列选择列名
   - 选择图表类型（簇状柱形图或折线图）
   - 可选启用次坐标轴
4. **配置图片设置**：设置宽度、高度、标题和图例可见性
5. **生成图表**：点击"生成图片"创建并保存图表

## 项目结构

```
excel-combo-chart-plotter/
├── src/main/java/com/gwill/tools/graphics/excelcombochartplotter/
│   ├── ExcelComboChartPlotterApp.java    # 应用程序入口
│   ├── i18n/
│   │   └── I18nManager.java              # 国际化管理器
│   ├── model/
│   │   ├── ChartConfiguration.java       # 图表配置模型
│   │   ├── ChartType.java                # 图表类型枚举
│   │   ├── ImageSettings.java            # 图片设置模型
│   │   ├── SizeUnit.java                 # 尺寸单位枚举
│   │   └── YAxisSeriesConfig.java        # Y轴系列配置
│   ├── service/
│   │   ├── ChartGenerationService.java   # 图表生成逻辑
│   │   └── ExcelDataService.java         # Excel 数据处理
│   └── ui/
│       ├── MainFrame.java                # 主应用程序窗口
│       ├── XAxisPanel.java               # X轴配置面板
│       ├── YAxisPanel.java               # Y轴配置面板
│       └── ImageSettingsPanel.java       # 图片设置面板
├── src/main/resources/
│   ├── messages_en_US.properties         # 英文翻译
│   └── messages_zh_CN.properties         # 中文翻译
└── pom.xml
```

## 许可证

本项目采用 MIT 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件。

## 贡献

欢迎贡献！请随时提交 Pull Request。

## 致谢

- [JFreeChart](https://www.jfree.org/jfreechart/) - Java 图表库
- [Apache POI](https://poi.apache.org/) - Excel 文件处理（通过 excel-io）
