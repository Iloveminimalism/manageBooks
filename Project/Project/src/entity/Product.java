/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;

/**
 *
 * @author ACER
 */

public class Product implements Serializable{

    private String productID;
    private String name;
    private int storage;
    private String type;
    private double price;
    private String origin;
    private String importDate;
    private String EXP;
    private int quantitySell;
    private String dateOfSell;
    private double totalPrice;
    private String image;

    public Product(String productID, String name, int storage, String type, double price, String origin, String importDate, String EXP, String image) {
        this.productID = productID;
        this.name = name;
        this.storage = storage;
        this.type = type;
        this.price = price;
        this.origin = origin;
        this.importDate = importDate;
        this.EXP = EXP;
        this.image = image;
    }

    

    public Product(String productID, String name, int storage, String type, double price, int quantitySell, String dateOfSell, double totalPrice) {
        this.productID = productID;
        this.name = name;
        this.storage = storage;
        this.type = type;
        this.price = price;
        this.quantitySell = quantitySell;
        this.dateOfSell = dateOfSell;
        this.totalPrice = totalPrice;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

   
    

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getImportDate() {
        return importDate;
    }

    public void setImportDate(String importDate) {
        this.importDate = importDate;
    }

    public String getEXP() {
        return EXP;
    }

    public void setEXP(String EXP) {
        this.EXP = EXP;
    }
    
    

    public int getQuantitySell() {
        return quantitySell;
    }

    public void setQuantitySell(int quantitySell) {
        this.quantitySell = quantitySell;
    }

    public String getDateOfSell() {
        return dateOfSell;
    }

    public void setDateOfSell(String dateOfSell) {
        this.dateOfSell = dateOfSell;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

   
    
    

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getProductInfoAsString() {
        String str = this.productID;
        str += "   " + this.name;
        str += "   " + this.storage;
        str += "   " + this.type;
        str += "   " + this.price;
        return str;
    }
    
    public String getSoldProductInfoAsString(){
        String str = this.productID;
        str += "   " + this.name;
        str += "   " + this.price;
        str += "   " + this.quantitySell;
        str += "   " +this.totalPrice;
        str += "   " + this.dateOfSell;
        
        return str; 
    }

}
