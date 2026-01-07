package com.gwill.tools.graphics.excelcombochartplotter.service;

import com.gwill.tools.graphics.excelcombochartplotter.model.ChartConfiguration;
import com.gwill.tools.graphics.excelcombochartplotter.model.ChartType;
import com.gwill.tools.graphics.excelcombochartplotter.model.ImageSettings;
import com.gwill.tools.graphics.excelcombochartplotter.model.YAxisSeriesConfig;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 图表生成服务
 */
public class ChartGenerationService {

    // 预定义的系列颜色
    private static final Color[] SERIES_COLORS = {
        new Color( 79, 129, 189 ),   // 蓝色
        new Color( 192, 80, 77 ),    // 红色
        new Color( 155, 187, 89 ),   // 绿色
        new Color( 128, 100, 162 ),  // 紫色
        new Color( 75, 172, 198 ),   // 青色
        new Color( 247, 150, 70 ),   // 橙色
        new Color( 119, 119, 119 ),  // 灰色
        new Color( 193, 152, 89 )    // 棕色
    };

    private static final Font CHINESE_FONT = new Font( "PingFang SC", Font.PLAIN, 12 );
    private static final Font CHINESE_TITLE_FONT = new Font( "PingFang SC", Font.BOLD, 16 );

    private final ExcelDataService excelDataService;

    public ChartGenerationService( ExcelDataService excelDataService ) {
        this.excelDataService = excelDataService;
    }

    /**
     * 生成图表并保存为图片
     *
     * @param configuration 图表配置
     * @param outputPath    输出文件路径
     * @throws IOException 如果保存失败
     */
    public void generateChart( ChartConfiguration configuration, String outputPath ) throws IOException {
        JFreeChart chart = createChart( configuration );

        ImageSettings settings = configuration.imageSettings();
        int width = settings.getWidthInPixels();
        int height = settings.getHeightInPixels();

        File outputFile = new File( outputPath );
        ChartUtils.saveChartAsPNG( outputFile, chart, width, height );
    }

    /**
     * 创建JFreeChart图表
     */
    private JFreeChart createChart( ChartConfiguration configuration ) {
        ImageSettings settings = configuration.imageSettings();
        List<String> xAxisData = excelDataService.getStringColumnData( configuration.xAxisColumn() );
        List<YAxisSeriesConfig> yAxisSeries = configuration.yAxisSeries();

        // 创建X轴
        CategoryAxis domainAxis = new CategoryAxis( configuration.xAxisColumn() );
        domainAxis.setLabelFont( CHINESE_FONT );
        domainAxis.setTickLabelFont( CHINESE_FONT );
        domainAxis.setCategoryLabelPositions( CategoryLabelPositions.UP_45 );

        // 创建主Y轴
        NumberAxis primaryRangeAxis = new NumberAxis();
        primaryRangeAxis.setLabelFont( CHINESE_FONT );
        primaryRangeAxis.setTickLabelFont( CHINESE_FONT );
        primaryRangeAxis.setAutoRangeIncludesZero( true );

        // 创建CategoryPlot
        CategoryPlot plot = new CategoryPlot();
        plot.setDomainAxis( domainAxis );
        plot.setRangeAxis( 0, primaryRangeAxis );
        plot.setOrientation( PlotOrientation.VERTICAL );
        plot.setBackgroundPaint( Color.WHITE );
        plot.setRangeGridlinePaint( Color.LIGHT_GRAY );

        int datasetIndex = 0;
        int colorIndex = 0;

        // 分离主轴的柱形图和折线图系列
        DefaultCategoryDataset primaryBarDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset primaryLineDataset = new DefaultCategoryDataset();

        for( YAxisSeriesConfig series : configuration.getPrimaryAxisSeries() ) {
            List<Number> yData = excelDataService.getNumericColumnData( series.columnName() );
            DefaultCategoryDataset targetDataset = series.chartType() == ChartType.BAR ? primaryBarDataset : primaryLineDataset;

            for( int i = 0; i < xAxisData.size() && i < yData.size(); i++ ) {
                targetDataset.addValue( yData.get( i ), series.columnName(), xAxisData.get( i ) );
            }
        }

//        System.out.println( "Bar dataset row count: " + primaryBarDataset.getRowCount() );
//        System.out.println( "Bar dataset row keys: " + primaryBarDataset.getRowKeys() );

        // 添加主轴柱形图数据集（必须先添加柱形图，使其在底层）
        if( primaryBarDataset.getRowCount() > 0 ) {
            plot.setDataset( datasetIndex, primaryBarDataset );
            plot.mapDatasetToRangeAxis( datasetIndex, 0 );

            BarRenderer barRenderer = new BarRenderer();
            barRenderer.setBarPainter( new StandardBarPainter() );
            barRenderer.setDrawBarOutline( false );
            barRenderer.setItemMargin( 0.0 );  // 同一分类内柱形之间零间距
            barRenderer.setShadowVisible( false );
            barRenderer.setDefaultToolTipGenerator( new StandardCategoryToolTipGenerator() );

            // 为每个系列设置颜色
            for( int i = 0; i < primaryBarDataset.getRowCount(); i++ ) {
                barRenderer.setSeriesPaint( i, SERIES_COLORS[colorIndex % SERIES_COLORS.length] );
                colorIndex++;
            }

            plot.setRenderer( datasetIndex, barRenderer );
            datasetIndex++;
        }

        // 添加主轴折线图数据集
        if( primaryLineDataset.getRowCount() > 0 ) {
            plot.setDataset( datasetIndex, primaryLineDataset );
            plot.mapDatasetToRangeAxis( datasetIndex, 0 );

            LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
            lineRenderer.setDefaultToolTipGenerator( new StandardCategoryToolTipGenerator() );

            for( int i = 0; i < primaryLineDataset.getRowCount(); i++ ) {
                lineRenderer.setSeriesPaint( i, SERIES_COLORS[colorIndex % SERIES_COLORS.length] );
                lineRenderer.setSeriesStroke( i, new BasicStroke( 2.0f ) );
                lineRenderer.setSeriesShapesVisible( i, true );
                colorIndex++;
            }

            plot.setRenderer( datasetIndex, lineRenderer );
            datasetIndex++;
        }

        // 处理次坐标轴数据
        List<YAxisSeriesConfig> secondaryAxisSeries = configuration.getSecondaryAxisSeries();
        if( !secondaryAxisSeries.isEmpty() ) {
            // 创建次Y轴
            NumberAxis secondaryRangeAxis = new NumberAxis();
            secondaryRangeAxis.setLabelFont( CHINESE_FONT );
            secondaryRangeAxis.setTickLabelFont( CHINESE_FONT );
            secondaryRangeAxis.setAutoRangeIncludesZero( true );
            plot.setRangeAxis( 1, secondaryRangeAxis );

            // 分离次轴的柱形图和折线图系列
            DefaultCategoryDataset secondaryBarDataset = new DefaultCategoryDataset();
            DefaultCategoryDataset secondaryLineDataset = new DefaultCategoryDataset();

            for( YAxisSeriesConfig series : secondaryAxisSeries ) {
                List<Number> yData = excelDataService.getNumericColumnData( series.columnName() );
                DefaultCategoryDataset targetDataset = series.chartType() == ChartType.BAR ? secondaryBarDataset : secondaryLineDataset;

                for( int i = 0; i < xAxisData.size() && i < yData.size(); i++ ) {
                    targetDataset.addValue( yData.get( i ), series.columnName(), xAxisData.get( i ) );
                }
            }

            // 添加次轴柱形图
            if( secondaryBarDataset.getRowCount() > 0 ) {
                plot.setDataset( datasetIndex, secondaryBarDataset );
                plot.mapDatasetToRangeAxis( datasetIndex, 1 );

                BarRenderer barRenderer = new BarRenderer();
                barRenderer.setBarPainter( new StandardBarPainter() );
                barRenderer.setDrawBarOutline( false );
                barRenderer.setItemMargin( 0.0 );
                barRenderer.setShadowVisible( false );
                barRenderer.setDefaultToolTipGenerator( new StandardCategoryToolTipGenerator() );

                for( int i = 0; i < secondaryBarDataset.getRowCount(); i++ ) {
                    barRenderer.setSeriesPaint( i, SERIES_COLORS[colorIndex % SERIES_COLORS.length] );
                    colorIndex++;
                }

                plot.setRenderer( datasetIndex, barRenderer );
                datasetIndex++;
            }

            // 添加次轴折线图
            if( secondaryLineDataset.getRowCount() > 0 ) {
                plot.setDataset( datasetIndex, secondaryLineDataset );
                plot.mapDatasetToRangeAxis( datasetIndex, 1 );

                LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
                lineRenderer.setDefaultToolTipGenerator( new StandardCategoryToolTipGenerator() );

                for( int i = 0; i < secondaryLineDataset.getRowCount(); i++ ) {
                    lineRenderer.setSeriesPaint( i, SERIES_COLORS[colorIndex % SERIES_COLORS.length] );
                    lineRenderer.setSeriesStroke( i, new BasicStroke( 2.0f ) );
                    lineRenderer.setSeriesShapesVisible( i, true );
                    colorIndex++;
                }

                plot.setRenderer( datasetIndex, lineRenderer );
            }
        }

        // 设置渲染顺序，让折线图在柱形图之上
        plot.setDatasetRenderingOrder( DatasetRenderingOrder.FORWARD );

        // 创建图表（不自动创建图例）
        JFreeChart chart = new JFreeChart( null, null, plot, false );
        chart.setBackgroundPaint( Color.WHITE );

        // 设置标题
        if( settings.title() != null && !settings.title().isBlank() ) {
            TextTitle title = new TextTitle( settings.title(), CHINESE_TITLE_FONT );
            chart.setTitle( title );
        }

        // 设置图例
        if( settings.showLegend() ) {
            LegendTitle legend = new LegendTitle( plot );
            legend.setItemFont( CHINESE_FONT );
            chart.addLegend( legend );
        }

        return chart;
    }
}
