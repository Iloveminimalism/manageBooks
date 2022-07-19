/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import dao.ProductDAO;
import entity.Product;
import java.util.ArrayList;

/**
 *
 * @author ACER
 */
public class ProductManager {
    private ArrayList<Product> listProducts;
    private ProductDAO myProductDAO;
    
    public ProductManager(){
        this.listProducts = new ArrayList();
        myProductDAO = new ProductDAO();
    }
    public ProductManager(ArrayList ListProduct){
        this.listProducts = ListProduct;
        myProductDAO = new ProductDAO();
    }
    
    public Product findProductbyID(String id){
        Product resProduct=null;
        for (int i = 0; i < this.listProducts.size(); i++) {
            Product currentProduct =  listProducts.get(i);
            if (currentProduct.getProductID().equalsIgnoreCase(id)) {
                resProduct=currentProduct;
                return resProduct;
            }
        }
        return resProduct;
    }
    
    public boolean addProduct(Product product){
        boolean result = true;
        if (this.findProductbyID(product.getProductID()) == null) {
            this.listProducts.add(product);
        } else {
            result = false;
        }

        return result;
    }
    
    
    public boolean deleleProductByID(String id) {
        boolean res = false;
        Product tempProduct = this.findProductbyID(id);
        if (tempProduct != null) {
            this.listProducts.remove(tempProduct);
            res = true;
        }
        return res;
    }
    
    
    public boolean updateProduct(Product editiedProduct){
        boolean result = false;

        Product oldProduct;     
        oldProduct=this.findProductbyID(editiedProduct.getProductID());
        
        if ( oldProduct != null) {
            int i = this.listProducts.indexOf(oldProduct);
            this.listProducts.remove(i);
            this.listProducts.add(i, editiedProduct);
            result = true;
        }
        
        return result;
    }
    
    public ArrayList findProductbyName(String name){
        ArrayList resultProductList = new ArrayList();
        Product tempProduct;
        for (int i = 0; i < this.listProducts.size(); i++) {
            tempProduct =  listProducts.get(i);
            if (tempProduct.getName().toLowerCase().contains(name.toLowerCase())) {
                resultProductList.add(tempProduct);
            }
        }
        return resultProductList;
    }
    
     public ArrayList findProductbyType(String type){
        ArrayList resultProductList = new ArrayList();
        Product tempProduct;
        for (int i = 0; i < this.listProducts.size(); i++) {
            tempProduct =  listProducts.get(i);
            if (tempProduct.getType().equalsIgnoreCase(type)) {
                resultProductList.add(tempProduct);
            }
        }
        return resultProductList;
    }
      public ArrayList findProductbyOrigin(String origin){
        ArrayList resultProductList = new ArrayList();
        Product tempProduct;
        for (int i = 0; i < this.listProducts.size(); i++) {
            tempProduct =  listProducts.get(i);
            if (tempProduct.getOrigin().toLowerCase().contains(origin.toLowerCase())) {
                resultProductList.add(tempProduct);
            }
        }
        return resultProductList;
    }
     
     
      public ArrayList findProductbyPriceRange(double fromPrice, double toPrice){
        ArrayList resultProductList = new ArrayList();
        Product tempProduct;
        for (int i = 0; i < this.listProducts.size(); i++) {
            tempProduct =  listProducts.get(i);
            if (tempProduct.getPrice()<= toPrice && tempProduct.getPrice()>=fromPrice) {
                resultProductList.add(tempProduct);
            }
        }
        return resultProductList;
    }
      
      public ArrayList getListProduct(){
          return this.listProducts;
      }
      
      public int saveByteData(){
        this.myProductDAO.saveListProductAsByte(this.listProducts);
        return this.listProducts.size();
    }
    public int saveCharDate(String fileName, String total){
        this.myProductDAO.saveListProductAsChar(this.listProducts, fileName, total);
        return this.listProducts.size();
    }
    public int getByteData(){
        this.listProducts = this.myProductDAO.readListProductAsByte();
        return this.listProducts.size();
    }

}
