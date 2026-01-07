package com.gwill.tools.graphics.excelcombochartplotter.ui;

import com.gwill.tools.graphics.excelcombochartplotter.i18n.I18nManager;
import com.gwill.tools.graphics.excelcombochartplotter.i18n.I18nManager.LocaleChangeListener;
import com.gwill.tools.graphics.excelcombochartplotter.model.ChartConfiguration;
import com.gwill.tools.graphics.excelcombochartplotter.model.ImageSettings;
import com.gwill.tools.graphics.excelcombochartplotter.model.YAxisSeriesConfig;
import com.gwill.tools.graphics.excelcombochartplotter.service.ChartGenerationService;
import com.gwill.tools.graphics.excelcombochartplotter.service.ExcelDataService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Application main window
 */
public class MainFrame extends JFrame implements LocaleChangeListener {

    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 700;

    private final ExcelDataService excelDataService;
    private final ChartGenerationService chartGenerationService;
    private final I18nManager i18n;

    // UI components that need i18n updates
    private JLabel excelFileLabel;
    private JLabel outputFileLabel;
    private JButton selectExcelButton;
    private JButton loadDataButton;
    private JButton browseOutputButton;
    private JButton generateButton;
    private JLabel languageLabel;
    private JComboBox<String> languageComboBox;
    private JPanel actionPanel;

    // Other UI components
    private JTextField excelFileField;
    private JTextField outputFileField;
    private XAxisPanel xAxisPanel;
    private YAxisPanel yAxisPanel;
    private ImageSettingsPanel imageSettingsPanel;

    // Column name tracking
    private List<String> allColumnNames = new ArrayList<>();
    private Set<String> unselectedColumnNames = new LinkedHashSet<>();

    public MainFrame() {
        this.excelDataService = new ExcelDataService();
        this.chartGenerationService = new ChartGenerationService( excelDataService );
        this.i18n = I18nManager.getInstance();

        initializeUI();
        setupColumnExclusionListeners();

        // Register for locale changes
        i18n.addLocaleChangeListener( this );
    }

    private void initializeUI() {
        setTitle( i18n.getString( "app.title" ) );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setSize( DEFAULT_WIDTH, DEFAULT_HEIGHT );
        setMinimumSize( new Dimension( 700, 600 ) );
        setLocationRelativeTo( null );

        JPanel mainPanel = new JPanel( new BorderLayout( 10, 10 ) );
        mainPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );

        // Top: file selection area
        JPanel filePanel = createFilePanel();
        mainPanel.add( filePanel, BorderLayout.NORTH );

        // Center: configuration area
        JPanel configPanel = createConfigPanel();
        mainPanel.add( configPanel, BorderLayout.CENTER );

        // Bottom: action area
        actionPanel = createActionPanel();
        mainPanel.add( actionPanel, BorderLayout.SOUTH );

        setContentPane( mainPanel );
    }

    /**
     * Setup X-axis and Y-axis column mutual exclusion listeners
     */
    private void setupColumnExclusionListeners() {
        // When X-axis selection changes, update unselectedColumnNames
        xAxisPanel.setOnSelectionChangeListener( ( oldSelection, newSelection ) -> {
            if( oldSelection != null && !oldSelection.isBlank() ) {
                if( !yAxisPanel.getSelectedColumns().contains( oldSelection ) ) {
                    unselectedColumnNames.add( oldSelection );
                }
            }
            if( newSelection != null && !newSelection.isBlank() ) {
                unselectedColumnNames.remove( newSelection );
            }
        } );

        // When Y-axis selection changes, update unselectedColumnNames and sync X-axis ComboBox
        yAxisPanel.setOnSelectionChangeListener( ( oldSelection, newSelection ) -> {
            if( oldSelection != null && !oldSelection.isBlank() ) {
                String xAxisSelected = xAxisPanel.getSelectedColumn();
                if( !oldSelection.equals( xAxisSelected ) ) {
                    unselectedColumnNames.add( oldSelection );
                }
            }
            if( newSelection != null && !newSelection.isBlank() ) {
                unselectedColumnNames.remove( newSelection );
            }
            xAxisPanel.updateAvailableColumns( new ArrayList<>( unselectedColumnNames ) );
        } );

        // Provide method for Y-axis panel to get unselected column names
        yAxisPanel.setUnselectedColumnsSupplier( () -> new ArrayList<>( unselectedColumnNames ) );
    }

    private JPanel createFilePanel() {
        JPanel panel = new JPanel( new GridBagLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 0, 0, 10, 0 ) );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets( 5, 5, 5, 5 );
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Excel file selection row
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        excelFileLabel = new JLabel( i18n.getString( "file.excel.label" ) );
        panel.add( excelFileLabel, gbc );

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        excelFileField = new JTextField();
        excelFileField.setEditable( false );
        panel.add( excelFileField, gbc );

        gbc.gridx = 2;
        gbc.weightx = 0;
        selectExcelButton = new JButton( i18n.getString( "file.select" ) );
        selectExcelButton.addActionListener( e -> selectExcelFile() );
        panel.add( selectExcelButton, gbc );

        gbc.gridx = 3;
        loadDataButton = new JButton( i18n.getString( "file.load" ) );
        loadDataButton.addActionListener( e -> loadExcelData() );
        panel.add( loadDataButton, gbc );

        // Output file selection row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        outputFileLabel = new JLabel( i18n.getString( "file.output.label" ) );
        panel.add( outputFileLabel, gbc );

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        outputFileField = new JTextField();
        panel.add( outputFileField, gbc );

        gbc.gridx = 2;
        gbc.weightx = 0;
        browseOutputButton = new JButton( i18n.getString( "file.browse" ) );
        browseOutputButton.addActionListener( e -> selectOutputFile() );
        panel.add( browseOutputButton, gbc );

        return panel;
    }

    private JPanel createConfigPanel() {
        JPanel panel = new JPanel( new BorderLayout( 10, 10 ) );

        // X-axis data panel
        xAxisPanel = new XAxisPanel();
        panel.add( xAxisPanel, BorderLayout.NORTH );

        // Y-axis data panel
        yAxisPanel = new YAxisPanel();
        panel.add( yAxisPanel, BorderLayout.CENTER );

        // Image settings panel
        imageSettingsPanel = new ImageSettingsPanel();
        panel.add( imageSettingsPanel, BorderLayout.SOUTH );

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel( new FlowLayout( FlowLayout.CENTER, 20, 5 ) );
        panel.setBorder( BorderFactory.createTitledBorder( i18n.getString( "action.title" ) ) );

        // Language selector
        languageLabel = new JLabel( i18n.getString( "language.label" ) );
        panel.add( languageLabel );

        languageComboBox = new JComboBox<>();
        updateLanguageComboBox();
        languageComboBox.addActionListener( e -> {
            if( languageComboBox.getSelectedIndex() >= 0 ) {
                Locale selectedLocale = I18nManager.SUPPORTED_LOCALES[languageComboBox.getSelectedIndex()];
                i18n.setLocale( selectedLocale );
            }
        } );
        panel.add( languageComboBox );

        // Generate button
        generateButton = new JButton( i18n.getString( "action.generate" ) );
        generateButton.setPreferredSize( new Dimension( 120, 35 ) );
        generateButton.addActionListener( e -> generateChart() );
        panel.add( generateButton );

        return panel;
    }

    /**
     * Update language ComboBox items
     */
    private void updateLanguageComboBox() {
        languageComboBox.removeAllItems();
        Locale currentLocale = i18n.getCurrentLocale();
        int selectedIndex = 0;

        for( int i = 0; i < I18nManager.SUPPORTED_LOCALES.length; i++ ) {
            Locale locale = I18nManager.SUPPORTED_LOCALES[i];
            languageComboBox.addItem( i18n.getLocaleDisplayName( locale ) );
            if( locale.equals( currentLocale ) ) {
                selectedIndex = i;
            }
        }

        languageComboBox.setSelectedIndex( selectedIndex );
    }

    @Override
    public void onLocaleChanged( Locale newLocale ) {
        // Update window title
        setTitle( i18n.getString( "app.title" ) );

        // Update file panel labels and buttons
        excelFileLabel.setText( i18n.getString( "file.excel.label" ) );
        outputFileLabel.setText( i18n.getString( "file.output.label" ) );
        selectExcelButton.setText( i18n.getString( "file.select" ) );
        loadDataButton.setText( i18n.getString( "file.load" ) );
        browseOutputButton.setText( i18n.getString( "file.browse" ) );

        // Update action panel
        ( (TitledBorder) actionPanel.getBorder() ).setTitle( i18n.getString( "action.title" ) );
        languageLabel.setText( i18n.getString( "language.label" ) );
        generateButton.setText( i18n.getString( "action.generate" ) );

        // Update language ComboBox without triggering action
        languageComboBox.removeActionListener( languageComboBox.getActionListeners()[0] );
        updateLanguageComboBox();
        languageComboBox.addActionListener( e -> {
            if( languageComboBox.getSelectedIndex() >= 0 ) {
                Locale selectedLocale = I18nManager.SUPPORTED_LOCALES[languageComboBox.getSelectedIndex()];
                i18n.setLocale( selectedLocale );
            }
        } );

        // Repaint
        actionPanel.repaint();
        repaint();
    }

    private void selectExcelFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter( new FileNameExtensionFilter( "Excel (*.xlsx)", "xlsx" ) );

        if( fileChooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
            File selectedFile = fileChooser.getSelectedFile();
            excelFileField.setText( selectedFile.getAbsolutePath() );

            // Auto-set output file path
            String outputPath = selectedFile.getAbsolutePath().replace( ".xlsx", "_chart.png" );
            outputFileField.setText( outputPath );
        }
    }

    private void selectOutputFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter( new FileNameExtensionFilter( "PNG (*.png)", "png" ) );

        if( fileChooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            if( !path.toLowerCase().endsWith( ".png" ) ) {
                path += ".png";
            }
            outputFileField.setText( path );
        }
    }

    private void loadExcelData() {
        String filePath = excelFileField.getText();
        if( filePath.isBlank() ) {
            JOptionPane.showMessageDialog( this,
                i18n.getString( "msg.select.excel.first" ),
                i18n.getString( "dialog.warning" ),
                JOptionPane.WARNING_MESSAGE );
            return;
        }

        try {
            excelDataService.loadData( filePath );
            List<String> columnNames = excelDataService.getColumnNames();

            if( columnNames.size() < 2 ) {
                JOptionPane.showMessageDialog( this,
                    i18n.getString( "msg.min.columns.error", columnNames.size() ),
                    i18n.getString( "dialog.error" ),
                    JOptionPane.ERROR_MESSAGE );
                return;
            }

            // Save all column names
            allColumnNames = new ArrayList<>( columnNames );

            // Initialize unselected column names (first column defaults to X-axis)
            unselectedColumnNames.clear();
            unselectedColumnNames.addAll( columnNames.subList( 1, columnNames.size() ) );

            // Update X-axis panel (auto-selects first item)
            xAxisPanel.setAllColumnNames( columnNames );

            // Clear Y-axis panel
            yAxisPanel.clear();

            JOptionPane.showMessageDialog( this,
                i18n.getString( "msg.load.success", excelDataService.getData().size(), columnNames.size() ),
                i18n.getString( "dialog.success" ),
                JOptionPane.INFORMATION_MESSAGE );

        } catch( Exception e ) {
            JOptionPane.showMessageDialog( this,
                i18n.getString( "msg.load.error", e.getMessage() ),
                i18n.getString( "dialog.error" ),
                JOptionPane.ERROR_MESSAGE );
        }
    }

    private void generateChart() {
        // Validate data is loaded
        if( !excelDataService.isDataLoaded() ) {
            JOptionPane.showMessageDialog( this,
                i18n.getString( "msg.load.data.first" ),
                i18n.getString( "dialog.warning" ),
                JOptionPane.WARNING_MESSAGE );
            return;
        }

        // Validate X-axis selection
        String xAxisColumn = xAxisPanel.getSelectedColumn();
        if( xAxisColumn == null || xAxisColumn.isBlank() ) {
            JOptionPane.showMessageDialog( this,
                i18n.getString( "msg.select.xaxis" ),
                i18n.getString( "dialog.warning" ),
                JOptionPane.WARNING_MESSAGE );
            return;
        }

        // Validate Y-axis series
        List<YAxisSeriesConfig> yAxisSeries = yAxisPanel.getSeriesConfigs();
        if( yAxisSeries.isEmpty() ) {
            JOptionPane.showMessageDialog( this,
                i18n.getString( "msg.add.yaxis" ),
                i18n.getString( "dialog.warning" ),
                JOptionPane.WARNING_MESSAGE );
            return;
        }

        // Validate output path
        String outputPath = outputFileField.getText();
        if( outputPath.isBlank() ) {
            JOptionPane.showMessageDialog( this,
                i18n.getString( "msg.specify.output" ),
                i18n.getString( "dialog.warning" ),
                JOptionPane.WARNING_MESSAGE );
            return;
        }

        // Get image settings
        ImageSettings imageSettings = imageSettingsPanel.getImageSettings();

        // Create configuration and generate chart
        ChartConfiguration config = new ChartConfiguration( xAxisColumn, yAxisSeries, imageSettings );

        try {
            chartGenerationService.generateChart( config, outputPath );
            JOptionPane.showMessageDialog( this,
                i18n.getString( "msg.generate.success", outputPath ),
                i18n.getString( "dialog.success" ),
                JOptionPane.INFORMATION_MESSAGE );
        } catch( Exception e ) {
            JOptionPane.showMessageDialog( this,
                i18n.getString( "msg.generate.error", e.getMessage() ),
                i18n.getString( "dialog.error" ),
                JOptionPane.ERROR_MESSAGE );
            e.printStackTrace();
        }
    }
}
