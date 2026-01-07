package com.gwill.tools.graphics.excelcombochartplotter.ui;

import com.gwill.tools.graphics.excelcombochartplotter.i18n.I18nManager;
import com.gwill.tools.graphics.excelcombochartplotter.i18n.I18nManager.LocaleChangeListener;
import com.gwill.tools.graphics.excelcombochartplotter.model.ImageSettings;
import com.gwill.tools.graphics.excelcombochartplotter.model.SizeUnit;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Locale;

/**
 * Image settings panel
 */
public class ImageSettingsPanel extends JPanel implements LocaleChangeListener {

    private final I18nManager i18n;
    private final JTextField widthField;
    private final JComboBox<SizeUnit> widthUnitCombo;
    private final JTextField heightField;
    private final JComboBox<SizeUnit> heightUnitCombo;
    private final JTextField titleField;
    private final JCheckBox showLegendCheckBox;

    // Labels that need i18n updates
    private final JLabel widthLabel;
    private final JLabel heightLabel;
    private final JLabel titleLabel;
    private final JLabel legendLabel;

    public ImageSettingsPanel() {
        this.i18n = I18nManager.getInstance();

        setLayout( new GridBagLayout() );
        setBorder( BorderFactory.createTitledBorder( i18n.getString( "image.settings.title" ) ) );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets( 5, 5, 5, 5 );
        gbc.anchor = GridBagConstraints.WEST;

        // First row: width and height
        gbc.gridx = 0;
        gbc.gridy = 0;
        widthLabel = new JLabel( i18n.getString( "image.width" ) );
        add( widthLabel, gbc );

        gbc.gridx = 1;
        widthField = new JTextField( "1024", 8 );
        add( widthField, gbc );

        gbc.gridx = 2;
        widthUnitCombo = new JComboBox<>( SizeUnit.values() );
        add( widthUnitCombo, gbc );

        gbc.gridx = 3;
        gbc.insets = new Insets( 5, 30, 5, 5 );
        heightLabel = new JLabel( i18n.getString( "image.height" ) );
        add( heightLabel, gbc );

        gbc.gridx = 4;
        gbc.insets = new Insets( 5, 5, 5, 5 );
        heightField = new JTextField( "768", 8 );
        add( heightField, gbc );

        gbc.gridx = 5;
        heightUnitCombo = new JComboBox<>( SizeUnit.values() );
        add( heightUnitCombo, gbc );

        // Second row: title
        gbc.gridx = 0;
        gbc.gridy = 1;
        titleLabel = new JLabel( i18n.getString( "image.title" ) );
        add( titleLabel, gbc );

        gbc.gridx = 1;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        titleField = new JTextField();
        add( titleField, gbc );

        // Third row: show legend checkbox
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        JPanel legendPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
        legendLabel = new JLabel( i18n.getString( "image.show.legend" ) );
        legendPanel.add( legendLabel );
        showLegendCheckBox = new JCheckBox();
        showLegendCheckBox.setSelected( true );
        legendPanel.add( showLegendCheckBox );
        add( legendPanel, gbc );

        // Register for locale changes
        i18n.addLocaleChangeListener( this );
    }

    @Override
    public void onLocaleChanged( Locale newLocale ) {
        ( (TitledBorder) getBorder() ).setTitle( i18n.getString( "image.settings.title" ) );
        widthLabel.setText( i18n.getString( "image.width" ) );
        heightLabel.setText( i18n.getString( "image.height" ) );
        titleLabel.setText( i18n.getString( "image.title" ) );
        legendLabel.setText( i18n.getString( "image.show.legend" ) );

        // Update ComboBox items to reflect new locale
        updateSizeUnitComboBoxes();

        repaint();
    }

    /**
     * Update size unit ComboBoxes to reflect current locale
     */
    private void updateSizeUnitComboBoxes() {
        // Store current selections
        SizeUnit selectedWidth = (SizeUnit) widthUnitCombo.getSelectedItem();
        SizeUnit selectedHeight = (SizeUnit) heightUnitCombo.getSelectedItem();

        // Refresh ComboBox items
        widthUnitCombo.removeAllItems();
        heightUnitCombo.removeAllItems();
        for( SizeUnit unit : SizeUnit.values() ) {
            widthUnitCombo.addItem( unit );
            heightUnitCombo.addItem( unit );
        }

        // Restore selections
        widthUnitCombo.setSelectedItem( selectedWidth );
        heightUnitCombo.setSelectedItem( selectedHeight );
    }

    /**
     * Get image settings
     */
    public ImageSettings getImageSettings() {
        double width = parseDouble( widthField.getText(), 1024 );
        double height = parseDouble( heightField.getText(), 768 );

        return new ImageSettings(
            width,
            (SizeUnit) widthUnitCombo.getSelectedItem(),
            height,
            (SizeUnit) heightUnitCombo.getSelectedItem(),
            titleField.getText(),
            showLegendCheckBox.isSelected()
        );
    }

    /**
     * Safely parse double value
     */
    private double parseDouble( String text, double defaultValue ) {
        try {
            return Double.parseDouble( text.trim() );
        } catch( NumberFormatException e ) {
            return defaultValue;
        }
    }
}
