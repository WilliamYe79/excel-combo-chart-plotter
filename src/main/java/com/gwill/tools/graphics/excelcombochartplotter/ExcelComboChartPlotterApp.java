package com.gwill.tools.graphics.excelcombochartplotter;

import com.gwill.tools.graphics.excelcombochartplotter.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Excel组合图表绘制器应用程序入口
 */
public class ExcelComboChartPlotterApp {

    public static void main( String[] args ) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch( Exception e ) {
            // 忽略，使用默认外观
        }

        // 在EDT中启动UI
        SwingUtilities.invokeLater( () -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible( true );
        } );
    }
}
