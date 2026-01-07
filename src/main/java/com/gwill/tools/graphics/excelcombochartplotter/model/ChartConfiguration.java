package com.gwill.tools.graphics.excelcombochartplotter.model;

import java.util.List;

/**
 * 完整的图表配置记录
 *
 * @param xAxisColumn     X轴数据所在列的列名
 * @param yAxisSeries     Y轴系列配置列表
 * @param imageSettings   图片设置
 */
public record ChartConfiguration(
    String xAxisColumn,
    List<YAxisSeriesConfig> yAxisSeries,
    ImageSettings imageSettings
) {

    /**
     * 检查配置是否有效
     */
    public boolean isValid() {
        return xAxisColumn != null
            && !xAxisColumn.isBlank()
            && yAxisSeries != null
            && !yAxisSeries.isEmpty();
    }

    /**
     * 获取使用主坐标轴的系列
     */
    public List<YAxisSeriesConfig> getPrimaryAxisSeries() {
        return yAxisSeries.stream()
            .filter( s -> !s.useSecondaryAxis() )
            .toList();
    }

    /**
     * 获取使用次坐标轴的系列
     */
    public List<YAxisSeriesConfig> getSecondaryAxisSeries() {
        return yAxisSeries.stream()
            .filter( YAxisSeriesConfig::useSecondaryAxis )
            .toList();
    }
}
