/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ACER
 */
public class UserDAO {

    private String pathStr = "D:\\NetBeansSCR\\JavaSCR\\Project\\privatedata\\userData";
    private String fileNameByte = "UserData_Byte.dat";

    public UserDAO() {
        this.createFolder();
    }

    public void createFolder() {
        File folder = new File(pathStr);
        if (folder.exists()) {
            System.out.println("Folder is available");
        } else {
            System.out.println("Folder not exists");
            folder.mkdirs();
        }
    }

    public void saveListUserAsByte(HashMap<String, String> listUser) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(this.pathStr + "\\" + this.fileNameByte);
            oos = new ObjectOutputStream(fos);

            oos.writeObject(listUser);
            oos.flush();
        } catch (IOException ex) {
            System.out.println("Exception:  " + ex.getMessage());
        }

    }

    public HashMap<String, String> readListUserAsByte() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = new FileInputStream(this.pathStr + "\\" + this.fileNameByte);
            ois = new ObjectInputStream(fis);

            HashMap<String, String> listUser = (HashMap<String, String>) ois.readObject();

            return listUser;

        } catch (IOException ex) {
            System.out.println("Exception:  " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println("Exception:  " + ex.getMessage());
        }
        return null;
    }

}
