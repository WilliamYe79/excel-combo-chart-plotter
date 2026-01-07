# Excel Combo Chart Plotter

A standalone Java application for creating combo charts (bar + line) from Excel data files.

## Features

- **Excel Data Import**: Load data from `.xlsx` files with column headers
- **Flexible Chart Configuration**:
  - Select any column as X-axis data
  - Add multiple Y-axis data series
  - Choose between clustered bar chart or line chart for each series
  - Support for secondary Y-axis
- **Image Output Settings**:
  - Configurable width and height (pixels, mm, or cm)
  - Custom chart title
  - Optional legend display
- **Multi-language Support**: English (US) and Simplified Chinese with real-time switching
- **User Preference Persistence**: Language settings are automatically saved

## Screenshots

![Main Interface](docs/screenshots/main-interface.png)

## Requirements

- **JDK 25** or later
- **Maven 3.9+** (for building)

## Dependencies

- JFreeChart 1.5.6
- Lombok 1.18.42
- [excel-io](https://github.com/user/excel-io) (custom Excel I/O library)

## Building

```bash
# Clone the repository
git clone https://github.com/username/excel-combo-chart-plotter.git
cd excel-combo-chart-plotter

# Build with Maven
mvn clean package
```

The executable JAR will be generated at:
```
target/excel-combo-chart-plotter-1.0.0-jar-with-dependencies.jar
```

## Running

```bash
java -jar target/excel-combo-chart-plotter-1.0.0-jar-with-dependencies.jar
```

## Usage

1. **Load Excel Data**: Click "Select File" to choose an Excel file (.xlsx), then click "Load Data"
2. **Configure X-Axis**: Select a column for X-axis data from the dropdown
3. **Add Y-Axis Series**:
   - Click "Add Series" to add a new data series
   - Select the column name for each series
   - Choose chart type (Bar Chart or Line Chart)
   - Optionally enable secondary axis
4. **Configure Image Settings**: Set width, height, title, and legend visibility
5. **Generate Chart**: Click "Generate Image" to create and save the chart

## Project Structure

```
excel-combo-chart-plotter/
├── src/main/java/com/gwill/tools/graphics/excelcombochartplotter/
│   ├── ExcelComboChartPlotterApp.java    # Application entry point
│   ├── i18n/
│   │   └── I18nManager.java              # Internationalization manager
│   ├── model/
│   │   ├── ChartConfiguration.java       # Chart configuration model
│   │   ├── ChartType.java                # Chart type enum
│   │   ├── ImageSettings.java            # Image settings model
│   │   ├── SizeUnit.java                 # Size unit enum
│   │   └── YAxisSeriesConfig.java        # Y-axis series configuration
│   ├── service/
│   │   ├── ChartGenerationService.java   # Chart generation logic
│   │   └── ExcelDataService.java         # Excel data handling
│   └── ui/
│       ├── MainFrame.java                # Main application window
│       ├── XAxisPanel.java               # X-axis configuration panel
│       ├── YAxisPanel.java               # Y-axis configuration panel
│       └── ImageSettingsPanel.java       # Image settings panel
├── src/main/resources/
│   ├── messages_en_US.properties         # English translations
│   └── messages_zh_CN.properties         # Chinese translations
└── pom.xml
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Acknowledgments

- [JFreeChart](https://www.jfree.org/jfreechart/) - Java chart library
- [Apache POI](https://poi.apache.org/) - Excel file handling (via excel-io)
