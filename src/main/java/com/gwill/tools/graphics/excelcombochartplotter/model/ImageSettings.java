package com.gwill.tools.graphics.excelcombochartplotter.model;

/**
 * 图片设置记录
 *
 * @param width          宽度值
 * @param widthUnit      宽度单位
 * @param height         高度值
 * @param heightUnit     高度单位
 * @param title          图表标题
 * @param showLegend     是否显示系列名称（图例）
 */
public record ImageSettings(
    double width,
    SizeUnit widthUnit,
    double height,
    SizeUnit heightUnit,
    String title,
    boolean showLegend
) {

    /**
     * 获取宽度的像素值
     */
    public int getWidthInPixels() {
        return widthUnit.toPixels( width );
    }

    /**
     * 获取高度的像素值
     */
    public int getHeightInPixels() {
        return heightUnit.toPixels( height );
    }

    /**
     * 创建默认设置
     */
    public static ImageSettings defaultSettings() {
        return new ImageSettings( 1024, SizeUnit.PIXEL, 768, SizeUnit.PIXEL, "", true );
    }
}
