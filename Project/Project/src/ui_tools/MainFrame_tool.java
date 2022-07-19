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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import manager.ProductManager;
import tableModel.ProductTableModel;

/**
 *
 * @author ACER
 */
public class MainFrame_tool extends javax.swing.JFrame implements Runnable {

    private ProductManager myMainProductManager;
    private Product editProduct;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    int hour, minute, second;

    /**
     * Creates new form MainFrame_tool
     */
    public MainFrame_tool() {
        initComponents();
        this.myMainProductManager = new ProductManager();
        addListener();
        refreshTable();
        Thread t = new Thread(this);
        t.start();
        this.txtToPrice.setVisible(false);
        this.txtFromPrice.setVisible(false);
        this.cmbxTypeSearch.setVisible(false);
        this.lblFromPrice.setVisible(false);
        this.lblToPrice.setVisible(false);
        this.txtLink.setVisible(false);
    }

    void refreshTable() {
        ArrayList<Product> listProducts = this.myMainProductManager.getListProduct();
        ProductTableModel tbtModel = new ProductTableModel(listProducts);
        this.tblProduct.setModel(tbtModel);
    }

    private void addListener() {
        this.menuSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doBackUp();
            }
        });
        this.menuRestore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doRestore();
            }
        });

        this.menuSortbyNameDecrease.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doSortbyNameDecrease();
            }
        });
        this.menuSortbyNameIncrease.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doSortbyNameIncrease();
            }
        });
        this.menuSortbyPriceDecrease.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doSortbyPriceDecrease();
            }
        });
        this.menuSortbyPriceIncrease.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doSortbyPriceIncrease();
            }
        });

        this.btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });
        this.btnImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (doValidateImport()) {
                    doAdd();
                }
            }
        });
        this.btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doShowExportProductDialog();
            }
        });

        this.tblProduct.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    fillInData();
                }
            }

        });
        this.btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                doUpdateProduct();
                doClear();

            }
        });
        this.btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showConfirmDialog("Delete Product") == 0) {

                    doDelete();
                    doClear();
                }
            }
        });
        this.btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doClear();
            }
        });

        this.btnFind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (doValidateSearch()) {
                    doSearch();
                }

            }
        });
        this.btnChooseImg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doChooseImg();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (showConfirmDialog("Quit") == 0) {
                    doBackUp();
                    doShowByeMess();
                    System.exit(0);
                }
            }

        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                doShowWelComeMess();

            }

        });

    }

    private void doChooseImg() {
        String namefile = null;
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File f = chooser.getSelectedFile();
        namefile = f.getAbsolutePath();
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(namefile).getImage().getScaledInstance(this.lblImage.getWidth(), this.lblImage.getHeight(), Image.SCALE_SMOOTH));
        this.lblImage.setIcon(imageIcon);
        this.txtLink.setText(namefile);

    }

    private void doRestore() {
        int count = this.myMainProductManager.getByteData();
//        JOptionPane.showMessageDialog(this, "Restore " + count + " cds");
        this.refreshTable();
    }

    private void doBackUp() {
        int count = this.myMainProductManager.saveByteData();
//        JOptionPane.showMessageDialog(this, "Backup " + count + " cds");
    }

    private void doAdd() {
        String id, name, type, origin, importDate, EXP, image;
        int storage;
        double price;

        image = this.txtLink.getText();

        importDate = sdf.format(dateChooserImport.getDate());
        EXP = sdf.format(dateChooserEXP.getDate());

        id = this.txtID.getText();
        name = this.txtName.getText();
        storage = Integer.parseInt(this.txtStorage.getText());
        type = (String) this.cmbxType.getSelectedItem();
        price = Double.parseDouble(this.txtPrice.getText());
        origin = this.txtOrigin.getText();

        Product newProduct = new Product(id, name, storage, type, price, origin, importDate, EXP, image);

        if (this.myMainProductManager.addProduct(newProduct)) {
            doShowSuccessfulMess("Add Successful");
            doClear();
            refreshTable();
        } else {
            doShowErrorMess("Add Unsuccessful");
        }
    }

    private void doClear() {
        this.txtID.setText("");
        this.txtName.setText("");
        this.txtPrice.setText("");
        this.txtStorage.setText("");
        this.dateChooserEXP.setDate(null);
        this.txtOrigin.setText("");
        this.dateChooserImport.setDate(null);
        this.lblImage.setIcon(null);

        this.cmbxType.setSelectedIndex(0);
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

    private boolean doValidateImport() {
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

    private boolean doValidateSearch() {
        boolean isValid = true;
        String cate = (String) this.cmbxSearch.getSelectedItem();

        if (cate.equals("Range Price")) {
            if (this.txtFromPrice.getText().length() == 0) {
                doShowErrorMess("From Price can not blank");
                return false;
            }
            if (this.txtToPrice.getText().length() == 0) {
                doShowErrorMess("To Price can not blank");
                return false;
            }
        } else {
            if (this.txtSearch.getText().length() == 0) {
                doShowErrorMess("Searching Content can not blank");
                return false;
            }
        }

        return isValid;
    }

    private void doUpdateProduct() {

        String id, name, type, origin, importDate, EXP, image;
        int storage;
        double price;
        id = this.txtID.getText();
        if (id.equalsIgnoreCase("")) {
            doShowErrorMess("Fill information first, Please");
        } else {

            image = this.txtLink.getText();
            importDate = sdf.format(dateChooserImport.getDate());
            EXP = sdf.format(dateChooserEXP.getDate());

            name = this.txtName.getText();
            storage = Integer.parseInt(this.txtStorage.getText());
            type = (String) this.cmbxType.getSelectedItem();
            price = Double.parseDouble(this.txtPrice.getText());
            origin = this.txtOrigin.getText();

            Product updateProduct = new Product(id, name, storage, type, price, origin, importDate, EXP, image);
            if (this.myMainProductManager.updateProduct(updateProduct)) {
                doShowSuccessfulMess("Update Successful");
                refreshTable();
            } else {
                doShowErrorMess("Update Fail");
            }
        }

    }

    private void fillInData() {
        int row = this.tblProduct.getSelectedRow();
        ProductTableModel tblModel = (ProductTableModel) this.tblProduct.getModel();
        Product selectedProduct = (Product) tblModel.getObjectAtRow(row);

        this.txtID.setText(selectedProduct.getProductID());
        this.txtName.setText(selectedProduct.getName());
        this.txtStorage.setText(String.valueOf(selectedProduct.getStorage()));
        this.txtPrice.setText(String.valueOf(selectedProduct.getPrice()));
        this.txtOrigin.setText(selectedProduct.getOrigin());
        String image = selectedProduct.getImage();

        ImageIcon imageIcon = new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(this.lblImage.getWidth(), this.lblImage.getHeight(), Image.SCALE_SMOOTH));
        this.lblImage.setIcon(imageIcon);
        this.txtLink.setText(image);

        try {
            Date importDate = new SimpleDateFormat("dd/MM/yyyy").parse(selectedProduct.getImportDate());
            this.dateChooserImport.setDate(importDate);
            Date EXP = new SimpleDateFormat("dd/MM/yyyy").parse(selectedProduct.getEXP());
            this.dateChooserEXP.setDate(EXP);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        this.cmbxType.setSelectedItem(selectedProduct.getType());
    }

    private void doDelete() {
        String id = txtID.getText();
        if (id.equalsIgnoreCase("")) {
            doShowErrorMess("Choose Product first, Please");
        } else {
            if (this.myMainProductManager.deleleProductByID(id)) {
                doShowSuccessfulMess("Remove Successful");
            } else {
                doShowErrorMess("Remove Fail");
            }
            refreshTable();
        }
    }

    private void doSortbyNameIncrease() {
        ArrayList<Product> listSortProduct = this.myMainProductManager.getListProduct();
        Collections.sort(listSortProduct, (p1, p2) -> {
            int compare = p1.getName().compareToIgnoreCase(p2.getName());
            return (int) compare;
        });
        ProductTableModel tblModel = new ProductTableModel(listSortProduct);
        this.tblProduct.setModel(tblModel);
    }

    private void doSortbyNameDecrease() {
        ArrayList<Product> listSortProduct = this.myMainProductManager.getListProduct();
        Collections.sort(listSortProduct, (p1, p2) -> {
            int compare = p2.getName().compareToIgnoreCase(p1.getName());
            return (int) compare;
        });
        ProductTableModel tblModel = new ProductTableModel(listSortProduct);
        this.tblProduct.setModel(tblModel);
    }

    private void doSortbyPriceIncrease() {
        ArrayList<Product> listSortProduct = this.myMainProductManager.getListProduct();
        Collections.sort(listSortProduct, (p1, p2) -> {
            int compare = Double.compare(p1.getPrice(), p2.getPrice());
            return compare;
        });
        ProductTableModel tblModel = new ProductTableModel(listSortProduct);
        this.tblProduct.setModel(tblModel);
    }

    private void doSortbyPriceDecrease() {
        ArrayList<Product> listSortProduct = this.myMainProductManager.getListProduct();
        Collections.sort(listSortProduct, (p1, p2) -> {
            int compare = Double.compare(p2.getPrice(), p1.getPrice());
            return compare;
        });
        ProductTableModel tblModel = new ProductTableModel(listSortProduct);
        this.tblProduct.setModel(tblModel);
    }

    private void doSearch() {
        String cate = (String) this.cmbxSearch.getSelectedItem();
        String searchStr;

        ArrayList<Product> listSearchProduct = new ArrayList<>();
        if (cate.equals("Name")) {
            searchStr = this.txtSearch.getText();
            listSearchProduct = this.myMainProductManager.findProductbyName(searchStr);

        }
        if (cate.equals("Origin")) {
            searchStr = this.txtSearch.getText();
            listSearchProduct = this.myMainProductManager.findProductbyOrigin(searchStr);

        }
        if (cate.equals("Type")) {
            searchStr = (String) this.cmbxTypeSearch.getSelectedItem();
            listSearchProduct = this.myMainProductManager.findProductbyType(searchStr);
        }
        if (cate.equals("Range Price")) {
            double fromPrice = Double.parseDouble(this.txtFromPrice.getText());
            double toPrice = Double.parseDouble(this.txtToPrice.getText());
            listSearchProduct = this.myMainProductManager.findProductbyPriceRange(fromPrice, toPrice);
            this.txtFromPrice.setText("");
            this.txtToPrice.setText("");
        }
        ProductTableModel tblModel = new ProductTableModel(listSearchProduct);
        this.tblProduct.setModel(tblModel);
    }

    private void doShowExportProductDialog() {
        ExportDialog_tool exportDialog = new ExportDialog_tool(this, true, this.myMainProductManager);
        exportDialog.setVisible(true);
    }

    private void doShowWelComeMess() {
        String title = "WelCome";
        String content = "";
        int typeMess = JOptionPane.INFORMATION_MESSAGE;
        ImageIcon icon = new ImageIcon("Icon\\iconHello.gif");
        JOptionPane.showMessageDialog(this, content, title, typeMess, icon);
    }

    private void doShowByeMess() {
        String title = "Good Bye !!!";
        String content = "";
        int typeMess = JOptionPane.INFORMATION_MESSAGE;
        ImageIcon icon = new ImageIcon("Icon\\Goodbye.gif");
        JOptionPane.showMessageDialog(this, content, title, typeMess, icon);
    }

    private int showConfirmDialog(String title) {
        String content = "Are You Sure ?";
        int type = JOptionPane.YES_NO_OPTION;
        ImageIcon icon = new ImageIcon("Icon\\iconQuestion.gif");
        int ans = JOptionPane.showConfirmDialog(this, content, title, type, type, icon);

        return ans;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
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
        btnChooseImg = new javax.swing.JButton();
        txtLink = new javax.swing.JTextField();
        panelManager = new javax.swing.JPanel();
        btnImport = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProduct = new javax.swing.JTable();
        cmbxSearch = new javax.swing.JComboBox<>();
        txtSearch = new javax.swing.JTextField();
        lblFromPrice = new javax.swing.JLabel();
        txtFromPrice = new javax.swing.JTextField();
        lblToPrice = new javax.swing.JLabel();
        txtToPrice = new javax.swing.JTextField();
        cmbxTypeSearch = new javax.swing.JComboBox<>();
        btnFind = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuSave = new javax.swing.JMenuItem();
        menuRestore = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menuSortbyNameIncrease = new javax.swing.JMenuItem();
        menuSortbyNameDecrease = new javax.swing.JMenuItem();
        menuSortbyPriceIncrease = new javax.swing.JMenuItem();
        menuSortbyPriceDecrease = new javax.swing.JMenuItem();

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Mechardise Management");

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

        txtPrice.setColumns(15);

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

        btnChooseImg.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnChooseImg.setText("Browse");
        btnChooseImg.setToolTipText("Set link to the Picture");

        javax.swing.GroupLayout panelInforLayout = new javax.swing.GroupLayout(panelInfor);
        panelInfor.setLayout(panelInforLayout);
        panelInforLayout.setHorizontalGroup(
            panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInforLayout.createSequentialGroup()
                .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInforLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnChooseImg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(txtLink, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelInforLayout.createSequentialGroup()
                        .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelInforLayout.createSequentialGroup()
                                .addGap(120, 120, 120)
                                .addComponent(lblImport))
                            .addGroup(panelInforLayout.createSequentialGroup()
                                .addGap(127, 127, 127)
                                .addComponent(lblEXP)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(panelInforLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInforLayout.createSequentialGroup()
                        .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblImage, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                                    .addGap(2, 2, 2)
                                    .addComponent(lblPrice)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(lblType, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cmbxType, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(panelInforLayout.createSequentialGroup()
                                    .addGap(69, 69, 69)
                                    .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(dateChooserEXP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(dateChooserImport, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)))
                                .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(16, 16, 16))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblID, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
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
                    .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbxType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblType))
                    .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblImport, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateChooserImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEXP, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateChooserEXP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelInforLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnChooseImg)
                    .addComponent(txtLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );

        panelManager.setLayout(null);

        btnImport.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\IconImport.jpg")); // NOI18N
        btnImport.setText("Import");
        btnImport.setToolTipText("Fill Infor and Import Product");
        panelManager.add(btnImport);
        btnImport.setBounds(20, 10, 100, 40);

        btnExport.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\IconDatabase.jpg")); // NOI18N
        btnExport.setText("Export");
        btnExport.setToolTipText("Export Product");
        panelManager.add(btnExport);
        btnExport.setBounds(130, 10, 100, 40);

        btnUpdate.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\IconUpdate.jpg")); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.setToolTipText("Update Information for Product");
        panelManager.add(btnUpdate);
        btnUpdate.setBounds(350, 10, 110, 40);

        btnDelete.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\IconDelete (1).jpg")); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setToolTipText("Delete Product");
        panelManager.add(btnDelete);
        btnDelete.setBounds(590, 10, 110, 40);

        btnRefresh.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\Refresh (1).jpg")); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.setToolTipText("Refresh Table");
        btnRefresh.setPreferredSize(new java.awt.Dimension(110, 33));
        panelManager.add(btnRefresh);
        btnRefresh.setBounds(470, 10, 110, 40);

        btnClear.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\Refresh (2).jpg")); // NOI18N
        btnClear.setText("Clear");
        btnClear.setToolTipText("Clear Information");
        btnClear.setPreferredSize(new java.awt.Dimension(110, 33));
        panelManager.add(btnClear);
        btnClear.setBounds(240, 10, 100, 40);

        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblProduct);

        panelManager.add(jScrollPane1);
        jScrollPane1.setBounds(20, 60, 680, 530);

        cmbxSearch.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cmbxSearch.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Origin", "Range Price", "Type" }));
        cmbxSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbxSearchActionPerformed(evt);
            }
        });
        panelManager.add(cmbxSearch);
        cmbxSearch.setBounds(50, 610, 130, 30);

        txtSearch.setColumns(15);
        txtSearch.setMinimumSize(new java.awt.Dimension(20, 20));
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        panelManager.add(txtSearch);
        txtSearch.setBounds(50, 650, 150, 30);

        lblFromPrice.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblFromPrice.setForeground(new java.awt.Color(255, 255, 255));
        lblFromPrice.setText("From");
        panelManager.add(lblFromPrice);
        lblFromPrice.setBounds(0, 650, 50, 40);

        txtFromPrice.setColumns(5);
        panelManager.add(txtFromPrice);
        txtFromPrice.setBounds(50, 650, 110, 30);

        lblToPrice.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblToPrice.setForeground(new java.awt.Color(255, 255, 255));
        lblToPrice.setText("To");
        panelManager.add(lblToPrice);
        lblToPrice.setBounds(10, 690, 20, 20);

        txtToPrice.setColumns(5);
        panelManager.add(txtToPrice);
        txtToPrice.setBounds(50, 680, 120, 30);

        cmbxTypeSearch.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Beef", "Pork", "SeaFood", "Fruit", " " }));
        cmbxTypeSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbxTypeSearchActionPerformed(evt);
            }
        });
        panelManager.add(cmbxTypeSearch);
        cmbxTypeSearch.setBounds(50, 650, 120, 30);

        btnFind.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnFind.setText("Find");
        panelManager.add(btnFind);
        btnFind.setBounds(200, 610, 90, 30);

        jLabel2.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\Logo.png")); // NOI18N
        panelManager.add(jLabel2);
        jLabel2.setBounds(480, 600, 220, 100);

        jLabel1.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\stripes-gc2ce1f5ee_1280.png")); // NOI18N
        panelManager.add(jLabel1);
        jLabel1.setBounds(0, -20, 760, 760);

        jMenu1.setText("File");
        jMenu1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMenu1.setMaximumSize(new java.awt.Dimension(50, 32767));

        menuSave.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        menuSave.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Example30112021(Tool NB)\\icon\\save.jpg")); // NOI18N
        menuSave.setText("Save");
        menuSave.setToolTipText("Backup Data");
        jMenu1.add(menuSave);

        menuRestore.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        menuRestore.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Example30112021(Tool NB)\\icon\\restore.jpg")); // NOI18N
        menuRestore.setText("Restore");
        menuRestore.setToolTipText("");
        jMenu1.add(menuRestore);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("View");
        jMenu2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        menuSortbyNameIncrease.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\iconUp.jpg")); // NOI18N
        menuSortbyNameIncrease.setText("Sort by Name");
        menuSortbyNameIncrease.setToolTipText("A -> Z");
        jMenu2.add(menuSortbyNameIncrease);

        menuSortbyNameDecrease.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\iconDown.jpg")); // NOI18N
        menuSortbyNameDecrease.setText("Sort by Name");
        menuSortbyNameDecrease.setToolTipText("Z -> A");
        jMenu2.add(menuSortbyNameDecrease);

        menuSortbyPriceIncrease.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\iconUp.jpg")); // NOI18N
        menuSortbyPriceIncrease.setText("Sort by Price");
        menuSortbyPriceIncrease.setToolTipText("Increase");
        jMenu2.add(menuSortbyPriceIncrease);

        menuSortbyPriceDecrease.setIcon(new javax.swing.ImageIcon("D:\\NetBeansSCR\\JavaSCR\\Project\\Icon\\iconDown.jpg")); // NOI18N
        menuSortbyPriceDecrease.setText("Sort by Price");
        menuSortbyPriceDecrease.setToolTipText("Decrease");
        jMenu2.add(menuSortbyPriceDecrease);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelInfor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelManager, javax.swing.GroupLayout.PREFERRED_SIZE, 730, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelInfor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelManager, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbxSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbxSearchActionPerformed
        // TODO add your handling code here:
        String cate = (String) this.cmbxSearch.getSelectedItem();

        if (cate.equals("Name")) {
            this.txtSearch.setVisible(true);
            this.txtToPrice.setVisible(false);
            this.txtFromPrice.setVisible(false);
            this.lblFromPrice.setVisible(false);
            this.lblToPrice.setVisible(false);
            this.cmbxTypeSearch.setVisible(false);
        }
        if (cate.equals("Origin")) {
            this.txtSearch.setVisible(true);
            this.txtToPrice.setVisible(false);
            this.txtFromPrice.setVisible(false);
            this.lblFromPrice.setVisible(false);
            this.lblToPrice.setVisible(false);
            this.cmbxTypeSearch.setVisible(false);
        }

        if (cate.equals("Type")) {
            this.txtSearch.setVisible(false);
            this.txtToPrice.setVisible(false);
            this.txtFromPrice.setVisible(false);
            this.lblFromPrice.setVisible(false);
            this.lblToPrice.setVisible(false);
            this.cmbxTypeSearch.setVisible(true);
        }
        if (cate.equals("Range Price")) {
            this.txtSearch.setVisible(false);
            this.txtToPrice.setVisible(true);
            this.txtFromPrice.setVisible(true);
            this.lblFromPrice.setVisible(true);
            this.lblToPrice.setVisible(true);
            this.cmbxTypeSearch.setVisible(false);
        }

    }//GEN-LAST:event_cmbxSearchActionPerformed

    private void cmbxTypeSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbxTypeSearchActionPerformed
        // TODO add your handling code here:
        doSearch();
    }//GEN-LAST:event_cmbxTypeSearchActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        doSearch();
    }//GEN-LAST:event_txtSearchKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseImg;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cmbxSearch;
    private javax.swing.JComboBox<String> cmbxType;
    private javax.swing.JComboBox<String> cmbxTypeSearch;
    private com.toedter.calendar.JDateChooser dateChooserEXP;
    private com.toedter.calendar.JDateChooser dateChooserImport;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAppName;
    private javax.swing.JLabel lblAppName2;
    private javax.swing.JLabel lblEXP;
    private javax.swing.JLabel lblFromPrice;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblImport;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblOrigin;
    private javax.swing.JLabel lblPrice;
    private javax.swing.JLabel lblStorage;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblToPrice;
    private javax.swing.JLabel lblType;
    private javax.swing.JMenuItem menuRestore;
    private javax.swing.JMenuItem menuSave;
    private javax.swing.JMenuItem menuSortbyNameDecrease;
    private javax.swing.JMenuItem menuSortbyNameIncrease;
    private javax.swing.JMenuItem menuSortbyPriceDecrease;
    private javax.swing.JMenuItem menuSortbyPriceIncrease;
    private javax.swing.JPanel panelInfor;
    private javax.swing.JPanel panelManager;
    private javax.swing.JTable tblProduct;
    private javax.swing.JTextField txtFromPrice;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtLink;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtOrigin;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtStorage;
    private javax.swing.JTextField txtToPrice;
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
