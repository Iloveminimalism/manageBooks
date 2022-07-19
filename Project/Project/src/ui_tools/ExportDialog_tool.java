/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui_tools;

import entity.Product;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import manager.ProductManager;
import tableModel.SellProductTableModel;

/**
 *
 * @author ACER
 */
public class ExportDialog_tool extends javax.swing.JDialog implements Runnable {

    private ProductManager myProductManager2;
    private ProductManager mainProductManager;
    private int i = 1;
    private Product currentProduct;
    private double total = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat sdf24Hours = new SimpleDateFormat("HH:mm:ss");
    int hour, minute, second;

    /**
     * Creates new form ExportDialog_tool
     */
    public ExportDialog_tool(java.awt.Frame parent, boolean modal, ProductManager mainProductManager) {
        super(parent, modal);
        initComponents();
        this.myProductManager2 = new ProductManager();
        this.mainProductManager = mainProductManager;
        refreshCreateSellTable();
        Thread t = new Thread(this);//add giờ system
        t.start();
        addListener();
        this.txtLink.setVisible(false);

    }

    private void addListener() {
        this.btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doExit();
            }
        });
        this.btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (doValidate()) {
                    doAdd();
                }
            }
        });
        this.btnFind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doFind();
            }
        });
        this.btnPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doPrint();
                refreshAfterPrint();
            }
        });
    }

    private void doExit() {
        this.dispose();
    }

    private void doPrint() {
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_yyyy");
        Calendar cal = Calendar.getInstance();
        Date dat = cal.getTime();
        String timeSell = sdf2.format(dat);
        String total = this.txtTotal.getText();
        String fileName = "Profit (" + i + ") " + timeSell + ".txt";
        if (this.txtTotal.getText().equals("")) {
            doShowErrorMess("Add Sold Products First, please !");
        } else {
            int count = this.myProductManager2.saveCharDate(fileName, total);
            doShowSuccessfulMess("Thank You !");
            this.txtTotal.setText("");
            i++;
        }
    }

    private void doAdd() {
        String id, name, type, origin, importDate, EXP, timeSell, image;
        int storage, quantitySell;
        double price, totalPrice;

        image = this.txtLink.getText();
        importDate = sdf.format(dateChooserImport.getDate());
        EXP = sdf.format(dateChooserEXP.getDate());

        id = this.txtID.getText();
        name = this.txtName.getText();
        storage = Integer.parseInt(this.txtStorage.getText());
        type = (String) this.cmbxType.getSelectedItem();
        price = Double.parseDouble(this.txtPrice.getText());
        origin = this.txtOrigin.getText();

        if (this.txtQuantitySell.getText().equalsIgnoreCase("")) {
            doShowErrorMess("Enter Quantity, Please");
        } else {
            quantitySell = Integer.parseInt(this.txtQuantitySell.getText());
            Calendar cal = Calendar.getInstance();
            Date dat = cal.getTime();
            timeSell = sdf24Hours.format(dat) + " " + sdf.format(dat);

            totalPrice = quantitySell * price;

            Product updateProduct = new Product(id, name, storage - quantitySell, type, price, origin, importDate, EXP, image);
            Product soldProduct = new Product(id, name, storage, type, price, quantitySell, timeSell, totalPrice);
            total += soldProduct.getTotalPrice();

            if (this.myProductManager2.addProduct(soldProduct)) {
                doShowSuccessfulMess("Calculated Successful");
                this.mainProductManager.updateProduct(updateProduct);
                this.txtTotal.setText(String.valueOf(total));
                doClear();
                refreshTableMain();
                refreshCreateSellTable();
            } else {
                doShowSuccessfulMess("Calculated Successful");
                Product tempProduct = this.myProductManager2.findProductbyID(id);
                Product updateSoldProduct = new Product(id, name, storage, type, price, quantitySell + tempProduct.getQuantitySell(), timeSell, totalPrice + tempProduct.getTotalPrice());
                this.myProductManager2.updateProduct(updateSoldProduct);
                doClear();
                refreshTableMain();
                refreshCreateSellTable();
            }
        }
    }

    void refreshCreateSellTable() {
        ArrayList<Product> listProducts = this.myProductManager2.getListProduct();
        SellProductTableModel tbtModel = new SellProductTableModel(listProducts);
        this.tblProductSell.setModel(tbtModel);
    }

    void refreshAfterPrint() {
        ArrayList<Product> listProducts = this.myProductManager2.getListProduct();
        listProducts.removeAll(listProducts);
        SellProductTableModel tbtModel = new SellProductTableModel(listProducts);
        this.tblProductSell.setModel(tbtModel);
    }

    void refreshTableMain() {
        MainFrame_tool myBigBoss = (MainFrame_tool) this.getParent();
        myBigBoss.refreshTable();
    }

    private void doClear() {
        this.txtID.setText("");
        this.txtName.setText("");
        this.txtPrice.setText("");
        this.txtStorage.setText("");
        this.dateChooserEXP.setDate(null);
        this.txtOrigin.setText("");
        this.dateChooserImport.setDate(null);
        this.txtQuantitySell.setText("");
        this.lblImage.setIcon(null);

        this.cmbxType.setSelectedIndex(0);
    }

    private void doFind() {
        String id, image;
        id = this.txtID.getText();

        currentProduct = this.mainProductManager.findProductbyID(id);
        if (currentProduct != null) {
            this.txtID.setText(currentProduct.getProductID());
            this.txtName.setText(currentProduct.getName());
            this.txtStorage.setText(String.valueOf(currentProduct.getStorage()));
            this.txtPrice.setText(String.valueOf(currentProduct.getPrice()));
            this.txtOrigin.setText(currentProduct.getOrigin());

            image = currentProduct.getImage();
            ImageIcon imageIcon = new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(this.lblImage.getWidth(), this.lblImage.getHeight(), Image.SCALE_SMOOTH));
            this.lblImage.setIcon(imageIcon);
            this.txtLink.setText(image);

            try {
                Date importDate = new SimpleDateFormat("dd/MM/yyyy").parse(currentProduct.getImportDate());
                this.dateChooserImport.setDate(importDate);
                Date EXP = new SimpleDateFormat("dd/MM/yyyy").parse(currentProduct.getEXP());
                this.dateChooserEXP.setDate(EXP);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }

            this.cmbxType.setSelectedItem(currentProduct.getType());
        } else {
            JOptionPane.showMessageDialog(this, "This ID is Unvailable, please Try again.");
        }

    }

    private boolean doValidate() {
        boolean isValid = true;

        if (this.txtID.getText().length() == 0) {
            doShowErrorMess("ID can not blank");
            return false;
        }

        if (this.txtName.getText().length() == 0) {
            doShowErrorMess("Name can not blank");
            return false;
        }

        if (this.txtOrigin.getText().length() == 0) {
            doShowErrorMess("Origin can not blank");
            return false;
        }

        Date importDate = this.dateChooserImport.getDate();
        Date EXP = this.dateChooserEXP.getDate();
        if (importDate == null) {
            doShowErrorMess("Please, Choose Import Date");
            return false;
        }
        if (EXP == null) {
            doShowErrorMess("Please, Choose EXP");
            return false;
        }
        if (EXP.compareTo(importDate) == -1) {
            doShowErrorMess("EXP can not smaller than Import Date");
            return false;
        }
        try {
            Float.parseFloat(this.txtPrice.getText());
            Integer.parseInt(this.txtStorage.getText());
        } catch (Exception excp) {
            doShowErrorMess("Price and Quantity must be number");
            return false;

        }

        return isValid;
    }

    private void doShowSuccessfulMess(String content) {
        String title = "Message";
        int typeMess = JOptionPane.INFORMATION_MESSAGE;
        ImageIcon icon = new ImageIcon("Icon\\iconGoodJod.gif");
        JOptionPane.showMessageDialog(this, content, title, typeMess, icon);
    }

    private void doShowErrorMess(String content) {
        String title = "Message";
        int typeMess = JOptionPane.INFORMATION_MESSAGE;
        ImageIcon icon = new ImageIcon("Icon\\Warning.gif");
        JOptionPane.showMessageDialog(this, content, title, typeMess, icon);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelInfor = new javax.swing.JPanel();
        lblID = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        lblName = new javax.swing.JLabel();
        lblStorage = new javax.swing.JLabel();
        lblOrigin = new javax.swing.JLabel();
        lblPrice = new javax.swing.JLabel();
        txtStorage = new javax.swing.JTextField();
        txtOrigin = new javax.swing.JTextField();
        txtPrice = new javax.swing.JTextField();
        lblType = new javax.swing.JLabel();
        cmbxType = new javax.swing.JComboBox<>();
        lblImport = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
        lblEXP = new javax.swing.JLabel();
        lblIcon = new javax.swing.JLabel();
        lblAppName = new javax.swing.JLabel();
        lblAppName2 = new javax.swing.JLabel();
        dateChooserImport = new com.toedter.calendar.JDateChooser();
        dateChooserEXP = new com.toedter.calendar.JDateChooser();
        lblTime = new javax.swing.JLabel();
        btnFind = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtQuantitySell = new javax.swing.JTextField();
        txtLink = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblProductSell = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Export Form");

        panelInfor.setBackground(new java.awt.Color(46, 49, 49));
        panelInfor.setForeground(new java.awt.Color(255, 153, 102));

        lblID.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblID.setForeground(new java.awt.Color(255, 255, 255));
        lblID.setText("Merchadise ID");

        lblName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblName.setForeground(new java.awt.Color(255, 255, 255));
        lblName.setText("Merchadise Name");

        lblStorage.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblStorage.setForeground(new java.awt.Color(255, 255, 255));
        lblStorage.setText("Storage");

        lblOrigin.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblOrigin.setForeground(new java.awt.Color(255, 255, 255));
        lblOrigin.setText("Origin");

        lblPrice.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblPrice.setForeground(new java.awt.Color(255, 255, 255));
        lblPrice.setText("Price");

        lblType.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblType.setForeground(new java.awt.Color(255, 255, 255));
        lblType.setText("Type");

        cmbxType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Beef", "Pork", "SeaFood", "Fruit", " " }));

        lblImport.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblImport.setForeground(new java.awt.Color(255, 255, 255));
        lblImport.setText("Import");

        lblImage.setBackground(new java.awt.Color(255, 255, 255));
        lblImage.setForeground(new java.awt.Color(255, 255, 255));

        lblEXP.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblEXP.setForeground(new java.awt.Color(255, 255, 255));
        lblEXP.setText("EXP");

        lblIcon.setBackground(new java.awt.Color(46, 49, 49));
        lblIcon.setForeground(new java.awt.Color(255, 255, 255));
        lblIcon.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\Appicon64.jpg")); // NOI18N

        lblAppName.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblAppName.setForeground(new java.awt.Color(255, 153, 0));
        lblAppName.setText("Mechardise");

        lblAppName2.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblAppName2.setForeground(new java.awt.Color(255, 153, 51));
        lblAppName2.setText("Management");

        lblTime.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        lblTime.setForeground(new java.awt.Color(255, 153, 51));

        btnFind.setText("Find");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Quantity");

        txtQuantitySell.setColumns(5);

        txtLink.setEditable(false);
        txtLink.setColumns(20);

        javax.swing.GroupLayout panelInforLayout = new javax.swing.GroupLayout(panelInfor);
        panelInfor.setLayout(panelInforLayout);
        panelInforLayout.setHorizontalGroup(
            panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInforLayout.createSequentialGroup()
                .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(panelInforLayout.createSequentialGroup()
                            .addComponent(lblID)
                            .addGap(39, 39, 39)
                            .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelInforLayout.createSequentialGroup()
                            .addComponent(lblName)
                            .addGap(18, 18, 18)
                            .addComponent(txtName))
                        .addGroup(panelInforLayout.createSequentialGroup()
                            .addComponent(lblStorage)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtStorage, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelInforLayout.createSequentialGroup()
                            .addComponent(lblOrigin)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtOrigin, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelInforLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(dateChooserEXP, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(txtLink, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEXP)
                    .addGroup(panelInforLayout.createSequentialGroup()
                        .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblImport)
                            .addGroup(panelInforLayout.createSequentialGroup()
                                .addComponent(lblPrice)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelInforLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel1))
                            .addGroup(panelInforLayout.createSequentialGroup()
                                .addComponent(lblType, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbxType, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelInforLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInforLayout.createSequentialGroup()
                        .addComponent(lblImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(panelInforLayout.createSequentialGroup()
                        .addComponent(lblIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelInforLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(lblAppName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInforLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblAppName2)
                                .addContainerGap())))))
            .addGroup(panelInforLayout.createSequentialGroup()
                .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInforLayout.createSequentialGroup()
                        .addGap(128, 128, 128)
                        .addComponent(btnFind, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelInforLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(dateChooserImport, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtQuantitySell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelInforLayout.setVerticalGroup(
            panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInforLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelInforLayout.createSequentialGroup()
                        .addComponent(lblAppName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAppName2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInforLayout.createSequentialGroup()
                        .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblID, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFind)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblName, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblStorage, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtStorage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblOrigin, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtOrigin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPrice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblType, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPrice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                            .addComponent(cmbxType, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblImport, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateChooserImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelInforLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(txtQuantitySell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEXP, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dateChooserEXP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.setLayout(new java.awt.BorderLayout());

        tblProductSell.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tblProductSell);

        jPanel1.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        btnAdd.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\IconAdd.jpg")); // NOI18N
        btnAdd.setText("Add to Bill");

        btnPrint.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\printer-3-24.jpg")); // NOI18N
        btnPrint.setText("Print");

        btnExit.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\exit-24.jpg")); // NOI18N
        btnExit.setText("Exit");

        jLabel2.setText("Total Price");

        txtTotal.setColumns(10);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(btnAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnPrint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 163, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPrint)
                    .addComponent(btnAdd)
                    .addComponent(btnExit)))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelInfor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelInfor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnPrint;
    private javax.swing.JComboBox<String> cmbxType;
    private com.toedter.calendar.JDateChooser dateChooserEXP;
    private com.toedter.calendar.JDateChooser dateChooserImport;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblAppName;
    private javax.swing.JLabel lblAppName2;
    private javax.swing.JLabel lblEXP;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblImport;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblOrigin;
    private javax.swing.JLabel lblPrice;
    private javax.swing.JLabel lblStorage;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblType;
    private javax.swing.JPanel panelInfor;
    private javax.swing.JTable tblProductSell;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtLink;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtOrigin;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtQuantitySell;
    private javax.swing.JTextField txtStorage;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        while (true) {
            Calendar cal = Calendar.getInstance();

            hour = cal.get(Calendar.HOUR_OF_DAY);
            minute = cal.get(Calendar.MINUTE);
            second = cal.get(Calendar.SECOND);

            SimpleDateFormat sdf24Hours = new SimpleDateFormat("HH:mm:ss");
            Date dat = cal.getTime();
            String time = sdf24Hours.format(dat);
            this.lblTime.setText(time);
        }
    }
}
