/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import packet.inputstream.PacketInputStream;
import packet.Packet;
import packet.LoopbackCode;
import script.ScriptTemplateMap;
import script.Compiler;
import java.awt.Color;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import packet.ClientCode;

/**
 *
 * @author Chris
 * @version Purpose is to analyze the packet data from the .msb files, give the buffer data to LoopbackCode for decoding, then give to the compiler
 */
public class Analyzer extends javax.swing.JFrame {
    
    /**
     * Creates new form Analyzer
     */
    public Analyzer() {
        try {
            ScriptTemplateMap.GetInstance().LoadTemplateMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sharky's Special Scripting Script-Maker");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFont(new java.awt.Font("Georgia", 2, 12)); // NOI18N

        jFileChooser1.setCurrentDirectory(new java.io.File("C:\\Users\\Chris\\Desktop\\GUI\\analyzer"));
        jFileChooser1.setDialogTitle("");
        jFileChooser1.setMultiSelectionEnabled(true);
        jFileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser1ActionPerformed(evt);
            }
        });

        jLabel1.setText("    Select 1 or more .msb files to analyze...");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 516, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jFileChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 536, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jFileChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed
        // TODO add your handling code here:
        //Create the new Text Area to place in the JScrollPane (panel)
        JTextArea jLog = new JTextArea(25, 50);
        jLog.setMargin(new Insets(5,5,5,5));
        jLog.setEditable(true);
        jLog.setVisible(true);
        
        //Add content to the (text area) based on the jFileChooser1's event responses
        if (jFileChooser1.getSelectedFiles() != null && jFileChooser1.getSelectedFiles().length > 0) {
            //Add content to the window.
            for (File pFile : jFileChooser1.getSelectedFiles()) {
                try {
                    PacketInputStream pStream = new PacketInputStream(new DataInputStream(new FileInputStream(pFile)));
                    short nVersion = pStream.ReadShort();
                    String sLocalEndPoint = pStream.ReadString();
                    short nLocalPort = pStream.ReadShort();
                    String sRemoteEndPoint = pStream.ReadString();
                    short nRemotePort = pStream.ReadShort();
                    byte nLocale = pStream.readByte();
                    short nBuild = pStream.ReadShort();
                    String sPatchLocation = pStream.ReadString();
                    while (true) {
                        // no length encoded :(
                        long tTimestamp = pStream.ReadLong();
                        int nSize = pStream.ReadInt();
                        short nHeader = pStream.ReadShort();
                        pStream.SetHeader(nHeader);
                        boolean bLoopback = !pStream.readBoolean();
                        byte[] aContent = pStream.ReadArr(nSize);
                        byte[] aContentBuffer = new byte[2 + aContent.length];
                        aContentBuffer[0] = (byte) (nHeader & 0xFF);
                        aContentBuffer[1] = (byte) ((nHeader >> 8) & 0xFF);
                        System.arraycopy(aContent, 0, aContentBuffer, 2, aContent.length);
                        int nPreDecodeIV = pStream.ReadInt();
                        int nPostDecodeIV = pStream.ReadInt();
                        Packet pPacket = null;
                        if (bLoopback) {
                            LoopbackCode pCode = LoopbackCode.GetLoopback(nHeader);
                            if (pCode != null) {
                                pPacket = pCode.pDecodePacket.ReadPacket(new PacketInputStream(nHeader, new ByteArrayInputStream(aContent)));
                            }
                        } else {
                            ClientCode pCode = ClientCode.GetClient(nHeader);
                            if (pCode != null) {
                                pPacket = pCode.pDecodePacket.ReadPacket(new PacketInputStream(nHeader, new ByteArrayInputStream(aContent)));
                            }
                        }
                        if (pPacket != null) {
                            Compiler.ProcessPacket(pPacket);
                        }
                    }
                } catch (EOFException e) {
                    // no length specified, so this is intended.
                    util.Logger.LogReport("End of file.");
                } catch (IOException ex) {
                    util.Logger.LogError("Exception thrown:  " + ex.toString());
                }
            }
            jLog.append(Compiler.GetOutputLog());
        } else if (jFileChooser1.getSelectedFile() != null) {
            jLog.append(jFileChooser1.getSelectedFile().getName() + "\r\n");
        }
        
        //Create the new window
        JFrame frame = new JFrame("Sniff Analyzer");
        //Add Content
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JScrollPane jScrollPane = new JScrollPane(jLog);
        frame.add(jScrollPane);
        frame.setBackground(Color.GRAY);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }//GEN-LAST:event_jFileChooser1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Analyzer().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}