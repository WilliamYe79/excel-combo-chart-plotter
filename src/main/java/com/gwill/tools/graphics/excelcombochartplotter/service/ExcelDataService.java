package com.gwill.tools.graphics.excelcombochartplotter.service;

import com.gwill.io.excel.ExcelIO;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Excel数据读取服务
 */
public class ExcelDataService {

    @Getter
    private List<String> columnNames;

    @Getter
    private List<Map<String, Object>> data;

    private boolean dataLoaded = false;

    /**
     * 从Excel文件加载数据
     *
     * @param filePath Excel文件路径
     * @throws Exception 如果读取失败或列名重复
     */
    public void loadData( String filePath ) throws Exception {
        // 读取第一个Sheet的数据
        data = ExcelIO.read( filePath )
            .sheet( 0 )
            .asMaps();

        if( data == null || data.isEmpty() ) {
            throw new Exception( "Excel文件中没有数据" );
        }

        // 获取列名
        columnNames = new ArrayList<>( data.getFirst().keySet() );

        // 检查列名是否有重复
        Set<String> uniqueNames = new HashSet<>( columnNames );
        if( uniqueNames.size() != columnNames.size() ) {
            throw new Exception( "Excel文件中存在重复的列名，请确保所有列名唯一" );
        }

        dataLoaded = true;
    }

    /**
     * 检查数据是否已加载
     */
    public boolean isDataLoaded() {
        return dataLoaded;
    }

    /**
     * 获取指定列的数据
     *
     * @param columnName 列名
     * @return 该列的所有数据值
     */
    public List<Object> getColumnData( String columnName ) {
        if( !dataLoaded ) {
            throw new IllegalStateException( "数据尚未加载" );
        }
        return data.stream()
            .map( row -> row.get( columnName ) )
            .toList();
    }

    /**
     * 获取指定列的数值数据（用于Y轴）
     *
     * @param columnName 列名
     * @return 数值列表
     */
    public List<Number> getNumericColumnData( String columnName ) {
        if( !dataLoaded ) {
            throw new IllegalStateException( "数据尚未加载" );
        }
        List<Number> numbers = new ArrayList<>();
        for( Map<String, Object> row : data ) {
            Object value = row.get( columnName );
            if( value instanceof Number number ) {
                numbers.add( number );
            } else if( value != null ) {
                try {
                    numbers.add( Double.parseDouble( value.toString() ) );
                } catch( NumberFormatException e ) {
                    numbers.add( 0.0 );
                }
            } else {
                numbers.add( 0.0 );
            }
        }
        return numbers;
    }

    /**
     * 获取指定列的字符串数据（用于X轴分类）
     *
     * @param columnName 列名
     * @return 字符串列表
     */
    public List<String> getStringColumnData( String columnName ) {
        if( !dataLoaded ) {
            throw new IllegalStateException( "数据尚未加载" );
        }
        return data.stream()
            .map( row -> {
                Object value = row.get( columnName );
                return value != null ? value.toString() : "";
            } )
            .toList();
    }

    /**
     * 清除已加载的数据
     */
    public void clear() {
        columnNames = null;
        data = null;
        dataLoaded = false;
    }
}
