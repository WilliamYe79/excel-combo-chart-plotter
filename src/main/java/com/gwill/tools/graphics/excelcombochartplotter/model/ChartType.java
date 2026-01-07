package com.gwill.tools.graphics.excelcombochartplotter.model;

import com.gwill.tools.graphics.excelcombochartplotter.i18n.I18nManager;

/**
 * Chart type enumeration
 */
public enum ChartType {

    BAR( "charttype.bar" ),
    LINE( "charttype.line" );

    private final String i18nKey;

    ChartType( String i18nKey ) {
        this.i18nKey = i18nKey;
    }

    public String getDisplayName() {
        return I18nManager.getInstance().getString( i18nKey );
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    /**
     * Get i18n key for this chart type
     */
    public String getI18nKey() {
        return i18nKey;
    }
}
