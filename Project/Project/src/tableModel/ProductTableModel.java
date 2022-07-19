/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tableModel;

import entity.Product;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author ACER
 */
public class ProductTableModel extends AbstractTableModel{
    private String[] columnNames = {"Product ID", "Product Name", "Storage", "Type","Price", "Origin", "Import Date", "EXP"};
    private ArrayList<Product> listData;
    private ArrayList<Object[]> listRow = new ArrayList<>();

    public ProductTableModel(ArrayList<Product> listProduct) {
        this.listData = listProduct;
        Product tempObj;
        for (int i = 0; i < this.listData.size(); i++) {
            tempObj = this.listData.get(i);
            Object[] row = {tempObj.getProductID() ,tempObj.getName(), tempObj.getStorage(), tempObj.getType(), tempObj.getPrice(), tempObj.getOrigin(), tempObj.getImportDate(), tempObj.getEXP()};
            this.listRow.add(row);
        }
    }

    @Override
    public int getRowCount() {
        return this.listRow.size();
    }
    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return listRow.get(rowIndex)[columnIndex];
    }
    public String getColumnName(int column)
    {
        return this.columnNames[column];
    }
    public java.lang.Class getColumnClass(int column)
    {
        return this.listRow.get(0)[column].getClass();
    }
    public Object getObjectAtRow(int row)
    {
        return this.listData.get(row);
    }
}
