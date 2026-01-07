package com.gwill.tools.graphics.excelcombochartplotter.model;

import com.gwill.tools.graphics.excelcombochartplotter.i18n.I18nManager;

/**
 * Size unit enumeration
 */
public enum SizeUnit {

    PIXEL( "unit.pixels", 1.0 ),
    MM( "unit.mm", 3.7795275591 ),    // 1mm ≈ 3.78 pixels at 96 DPI
    CM( "unit.cm", 37.795275591 );    // 1cm ≈ 37.8 pixels at 96 DPI

    private final String i18nKey;
    private final double pixelsPerUnit;

    SizeUnit( String i18nKey, double pixelsPerUnit ) {
        this.i18nKey = i18nKey;
        this.pixelsPerUnit = pixelsPerUnit;
    }

    public String getDisplayName() {
        return I18nManager.getInstance().getString( i18nKey );
    }

    public double getPixelsPerUnit() {
        return pixelsPerUnit;
    }

    /**
     * Convert value in this unit to pixels
     */
    public int toPixels( double value ) {
        return (int) Math.round( value * pixelsPerUnit );
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    /**
     * Get i18n key for this unit
     */
    public String getI18nKey() {
        return i18nKey;
    }
}
