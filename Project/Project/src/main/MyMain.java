/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import dao.ProductDAO;
import entity.Product;
import java.util.ArrayList;
import javax.swing.UIManager;
import login.LoginForm1;

import ui_tools.MainFrame_tool;


/**
 *
 * @author ACER
 */
public class MyMain {
    public static void main(String[] args) {
          try
        {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.bernstein.BernsteinLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
            UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.texture.TextureLookAndFeel");
        }catch(Exception ex)
        {
            
        }
        
        
//            MainFrame_tool myFrame = new MainFrame_tool();
//            myFrame.setVisible(true);
            LoginForm1 myLogin = new LoginForm1();
            myLogin.setVisible(true);
        
    }
    
}
