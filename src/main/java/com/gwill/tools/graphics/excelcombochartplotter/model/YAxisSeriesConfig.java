package com.gwill.tools.graphics.excelcombochartplotter.model;

/**
 * Y轴系列配置记录
 *
 * @param columnName      系列所在列的列名
 * @param chartType       图表类型（簇状柱形图或折线图）
 * @param useSecondaryAxis 是否使用次坐标轴
 */
public record YAxisSeriesConfig(
    String columnName,
    ChartType chartType,
    boolean useSecondaryAxis
) {

    /**
     * 创建使用主坐标轴的默认配置
     */
    public static YAxisSeriesConfig withPrimaryAxis( String columnName, ChartType chartType ) {
        return new YAxisSeriesConfig( columnName, chartType, false );
    }

    /**
     * 创建使用次坐标轴的配置
     */
    public static YAxisSeriesConfig withSecondaryAxis( String columnName, ChartType chartType ) {
        return new YAxisSeriesConfig( columnName, chartType, true );
    }
}
