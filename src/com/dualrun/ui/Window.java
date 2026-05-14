package com.dualrun.ui;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Window {
    private static int w = 980;
    private static int h = w/2+w/10-100;
    private static int bw = w/6+65;
    private static int bh = bw/6;
    private static int bx = w-w/4-25;
    private static int by = h/6-50;
    private static double space = 100.0;

    private static boolean userCreated = false;
    private static String username = "DualRun";
    //And set those later on in appropriate condition blocks, because makes no sense to laden code with another during-process actual username check;
    //other errors will work in any case and display the full issue demonstrably

    private static String[] options = {
        "--Select an app--",
        "Viber",
        "Telegram",
        "Discord"
    };

    private static int runScript(String path, StringBuilder output) throws IOException,InterruptedException{
        ProcessBuilder pb = new ProcessBuilder(
            "powershell.exe",
            "-ExecutionPolicy",
            "Bypass",
            "-File",
            path
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try(
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))){
                    String line;
                    while ((line = reader.readLine())!=null){
                        output.append(line).append("\n");
                    }
                } 
        return process.waitFor();
    }

    private static String selectedApp = null;



public static void Log(JTextPane con, String msg, Color color) {
    StyledDocument doc = con.getStyledDocument();

    Style style = con.addStyle("style", null);
    StyleConstants.setForeground(style, color);

    try {
        doc.insertString(doc.getLength(), msg /*+ "\n"*/, style);
    } catch (Exception e) {
        e.printStackTrace();
    }

    con.setCaretPosition(doc.getLength());
}

    public static void main(String [] args){
        SwingUtilities.invokeLater(()->{

            JFrame frame = new JFrame("DualRun by ic3dust");
            frame.setLayout(null);

            JTextPane con = new JTextPane();
            con.setEditable(false);
            con.setBackground(Color.BLACK);
            con.setFont(new Font("Consolas", Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(con);
            scrollPane.setBounds(1/w+w/9-10, 1/h+h/9, w/2+25, h/2+75);
            Log(con, "Console initiated successfully\n", Color.decode("#0B80F4"));
            Log(con, "Step 1: Create user for storing second instance\n", Color.WHITE);

            JComboBox<String> selector = new JComboBox<String>(options);
            selector.setBounds(bx, (int)(by+(space*0.75)), bw, bh);
            selector.setSelectedIndex(0);
            selector.setEnabled(false);

            JButton Usr = new JButton();
            Usr.setText("1. Create user-container");
            Usr.setBounds(bx, by, bw, bh);

            JButton delUsr = new JButton("Delete DualRun user");
            delUsr.setBounds(bx, (int)(by+3*space), bw, bh);
            delUsr.setEnabled(true);

            Usr.addActionListener(e -> {
                Log(con, "Creating temp user profile.\nRunning script...\n\n", Color.WHITE);

                try {
                    StringBuilder out = new StringBuilder();
                    int exitCode = runScript("C:\\Program Files\\DualRun\\Scripts\\user.ps1",out);
                    if (exitCode == 0) {

                        ProcessBuilder verify = new ProcessBuilder(
                    "powershell.exe",
                            "-Command",
                            "Get-LocalUser -Name 'DualRun'"
                        );
                        verify.redirectErrorStream(true);
                        Process verifyProc = verify.start();
                        int verifyExit = verifyProc.waitFor();

                        if(verifyExit ==0){
                            userCreated = true;
                            SwingUtilities.invokeLater(() -> {
                                selector.setEnabled(true);
                            });

                            selector.setEnabled(true);
                            selector.revalidate();
                            selector.repaint();

                            Log(con, "User",Color.GREEN);
                            Log(con, " "+username+" ",Color.WHITE);
                            Log(con,"created successfully! \n", Color.GREEN);
                            Log(con,"exit code: "+exitCode, Color.WHITE);
                        
                        }else{
                            userCreated=false;
                            Log(con,"An error occured while verifying DualRun user creation",Color.RED);
                        }


                        } else if (exitCode == 1) {
                            userCreated = false;
                            Log(con, "[CONFLICT]: ", Color.RED);
                            Log(con, "User", Color.YELLOW);
                            Log(con, " "+username+" ", Color.WHITE);
                            Log(con,"already exists or validation failed. Try deleting user and running script again.\n", Color.YELLOW);
                            Log(con,"exit code: "+exitCode, Color.WHITE);
                        } else {
                        userCreated = false;
                            Log(con, "User creation unexpectedly failed.\n", Color.RED);
                            Log(con, "exit code: "+exitCode, Color.WHITE);
                        }

                    } catch (Exception ex) {
                        Log(con, "Failed to create user: " + ex.getMessage(), Color.RED);
                    }
            });

            JButton Inst = new JButton("2. Specify app before the install");
            Inst.setEnabled(false);
            Inst.setBounds(bx, (int)(by+1.5*space), bw, bh);



            
            selector.addActionListener(e->{
                // App wraper safety-check
                if(!userCreated){
                    Log(con, "Create user profile first.\n", Color.RED);
                    return;
                }
                    String opt = (String) selector.getSelectedItem();
                    if(opt == null || opt.equals("-Select an app-")){
                        selectedApp = null;
                        Inst.setEnabled(false);
                        return;
                    }
                    selectedApp = opt;
                    Inst.setEnabled(true);
                    Inst.setText("2. Install " + opt + " on '" + username+"' ");
                    Log(con, "\nReady to install "+opt+" on profile '"+username+"' ", Color.WHITE);
                    
                });
            

            //Log(con, "Installing "+app+" on profile '"+username+"' ...");
            // Installation block
            boolean appAndUserSuccess  =false;
            //appAndUserSuccess  =true;

            //Log(con, ""+app+" installed successfully on system user '"+username+"' ");

            JButton Fin = new JButton();
            Fin.setBounds(bx, (int)(by+2.25*space), bw, bh);
            if(appAndUserSuccess){
                Fin.setText("3. Log off " + username + " & exit DualRun");
                Fin.addActionListener(e->{
                    //logoffUser.run();
                    Log(con, "User '"+username+"' was logged off on current computer\n Closing DualRun...", Color.WHITE);
                    frame.dispose();
                });
            }else{
                Fin.setText("3. Close DualRun");
                Fin.addActionListener(e -> frame.dispose());
            }

            delUsr.addActionListener(e -> {
                try{
                    StringBuilder out = new StringBuilder();
                    int exitCode = runScript("C:\\Program Files\\DualRun\\Scripts\\delUser.ps1",out);
                    if(exitCode ==0){
                        userCreated = false;
                        selector.setEnabled(false);
                        selector.revalidate();
                        selector.repaint();

                        Log(con, "\nUser ", Color.GREEN);
                        Log(con, username + " ", Color.WHITE);
                        Log(con, "deleted successfully!\n", Color.GREEN);
                    }else if(exitCode == 1){
                        userCreated = false;
                            selector.setEnabled(false);
                            selector.revalidate();
                            selector.repaint();

                            Log(con, "User ", Color.RED);
                            Log(con, username + " ", Color.WHITE);
                            Log(con, "was not deleted successfully.\n", Color.RED);
                            Log(con, "Full error:" + out, Color.WHITE);
                    }else{
                        Log(con, "[ERROR] Unexpected failure while deleting DualRun\n", Color.RED);
                        Log(con, "Exit code: " + exitCode + "\n", Color.WHITE);
                        Log(con, "Full error:" + out, Color.WHITE);

                        userCreated = true;
                        selector.setEnabled(false);
                        selector.revalidate();
                        selector.repaint();
                    }
                }catch(Exception ex){
                    Log(con, "Failed to delete user DualRun: "+ex.getMessage(), Color.RED);
                }
            });
             

            Inst.addActionListener(e->{

                Log(con, "Installing "+selectedApp+" on user DualRun...",Color.WHITE);
                try{
                StringBuilder out = new StringBuilder();
                int exitCode;
                if("Viber".equals(selectedApp)){
                    exitCode = runScript("C:\\Program Files\\DualRun\\Scripts\\iViber.ps1",out);
                }else if("Telegram".equals(selectedApp)){
                    exitCode = runScript("C:\\Program Files\\DualRun\\Scripts\\iTelegram.ps1",out);
                }else if("Discord".equals(selectedApp)){
                    exitCode = runScript("C:\\Program Files\\DualRun\\Scripts\\iDiscord.ps1",out);
                }else{
                    Log(con, "[ERROR] Unknown app or unselected app to install", Color.RED);
                    return;
                }

                if(exitCode == 0){
                    Log(con, ""+selectedApp+" ",Color.WHITE);
                    Log(con,"was installed successfully on user DualRun!",Color.GREEN);
                }else{
                    Log(con, ""+selectedApp+" ",Color.WHITE);
                    Log(con,"failed to install", Color.RED);
                    Log(con, "\n"+out.toString(),Color.WHITE);
                }
                }catch(Exception ex){
                    Log(con,"Failed to install app "+selectedApp+" on user DualRun: " + ex.getMessage(),Color.RED);
                }
            });


            frame.add(scrollPane);
            frame.add(Usr);
            frame.add(selector);
            frame.add(Inst);
            frame.add(Fin);
            frame.add(delUsr);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(w,h);
            frame.setVisible(true);
        });
    }

}