/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Product;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 *
 * @author ACER
 */
public class ProductDAO {
    private String pathStr = "D:\\NetBeansSCR\\JavaSCR\\Project\\privatedata";
    private String fileNameByte = "ProductData_Byte.dat";
    private String fileNameChar;

    public ProductDAO() {
        this.createFolder();
    }
    public void createFolder(){
        File folder = new File(pathStr);
        if(folder.exists()){
            System.out.println("Folder exists");
        }else{
            System.out.println("Folder not exsts");
            folder.mkdirs();
            System.out.println("Folder is created");
        }
    }
    
    public void saveListProductAsByte(ArrayList<Product> listProduct){
        FileOutputStream fos = null;
        ObjectOutputStream oos= null;
        
        try {
            fos = new FileOutputStream(this.pathStr + "\\" + this.fileNameByte);
            oos = new ObjectOutputStream(fos);

            oos.writeObject(listProduct);
            oos.flush();
        } catch (IOException ex) {
            System.out.println("Exception:  " + ex.getMessage());
        }
        
    } 
    
    public ArrayList<Product> readListProductAsByte(){
        FileInputStream fis= null;
        ObjectInputStream ois=null;
        
        try {
            fis = new FileInputStream(this.pathStr + "\\" + this.fileNameByte);
            ois = new ObjectInputStream(fis);

            ArrayList<Product> listProduct = (ArrayList<Product>) ois.readObject();

            return listProduct;

        } catch (IOException ex) {
            System.out.println("Exception:  " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println("Exception:  " + ex.getMessage());
        }      
        return null;
    }
    
    public void saveListProductAsChar(ArrayList<Product> listProduct, String fileName, String total){
       FileWriter fw = null;
        BufferedWriter bw = null;

        
        try {
            fw = new FileWriter(this.pathStr + "\\" + fileName);

            bw = new BufferedWriter(fw);

            Product tempProduct;

            for (int i = 0; i < listProduct.size(); i++) {
                tempProduct = listProduct.get(i);
                bw.write(tempProduct.getSoldProductInfoAsString());
                bw.newLine();
            }
            bw.write(total);

            bw.flush();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } 
    }
}
