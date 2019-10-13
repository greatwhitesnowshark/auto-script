/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scriptmaker;

import game.network.ClientPacket;
import game.network.InPacket;
import io.netty.buffer.Unpooled;
import packet.PacketWrapper;
import packet.opcode.LoopbackCode;
import script.ScriptTemplateMap;
import script.Compiler;
import java.awt.Color;
import java.awt.Insets;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import packet.opcode.ClientCode;
import util.Logger;
import static util.PacketUtil.*;

/**
 *
 * @author Sharky
 * @todo:  Portal scripts aren't currently being padded correctly throughout the conditional flow-blocks
 *         Add handling for item scripts
 *         Need to determine when the script is transferring your field or when its a user doing it
 *         Need to end FieldScript Scripts at the end of the direction node
 *         Need to add UserEffectLocal and FieldEffect packets
 *         Need to update ScriptMessage switch/case section
 *         Need to verify if the quest result packets should be in fact coming from the field scripts
 *         Need to make sure that when SetField happens, there will be a new case label created for it if one does not exist
 */
public class ScriptMaker extends javax.swing.JFrame {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
    private static final int INCREMENT_CACHED_KEY_VALUE = 2;
    
    /**
     * Creates new form ScriptMaker
     */
    public ScriptMaker() {
        try {
            ScriptTemplateMap.GetInstance().LoadTemplateMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initComponents();
    }

    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new ScriptMaker().setVisible(true);
        });
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

        jFileChooser1.setCurrentDirectory(new java.io.File("C:\\Users\\Chris\\Desktop"));
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
        long tPacketTime = 0;
        //Add content to the (text area) based on the jFileChooser1's event responses
        if (jFileChooser1.getSelectedFiles() != null && jFileChooser1.getSelectedFiles().length > 0) {
            //Add content to the window.
            for (File pFile : jFileChooser1.getSelectedFiles()) {
                Compiler.GetScript().ClearSessionQuestID();

                try (DataInputStream pDataInputStream = new DataInputStream(new FileInputStream(pFile))) {
                    long tCur = tPacketTime = System.currentTimeMillis();
                    System.out.print(String.format("Processing file `%s`.....", pFile.getName()));
                    short nVersion = ReadShort(pDataInputStream);
                    String nLocalEndPoint = ReadString(pDataInputStream);
                    short nLocalPort = ReadShort(pDataInputStream);
                    String sRemoteEndPoint = ReadString(pDataInputStream);
                    short nRemotePort = ReadShort(pDataInputStream);
                    byte nLocale = pDataInputStream.readByte();
                    short nBuild = ReadShort(pDataInputStream);
                    String sPatchLocation = ReadString(pDataInputStream);
                    while (true) {
                        // no length encoded :(
                        if (System.currentTimeMillis() - tCur >= 1000) {
                            System.out.print(".");
                            tCur = System.currentTimeMillis();
                        }
                        long tTimestamp = ReadLong(pDataInputStream);
                        int nSize = ReadInt(pDataInputStream);
                        short nHeader = ReadShort(pDataInputStream);
                        boolean bLoopback = !pDataInputStream.readBoolean();
                        byte[] aContent = ReadArr(pDataInputStream, nSize);
                        byte[] aTotalContent = new byte[2 + aContent.length];
                        aTotalContent[0] = (byte) (nHeader & 0xFF);
                        aTotalContent[1] = (byte) ((nHeader >> 8) & 0xFF);
                        System.arraycopy(aContent, 0, aTotalContent, 2, aContent.length);
                        int aPreDecodeIV = ReadInt(pDataInputStream);
                        int aPostDecodeIV = ReadInt(pDataInputStream);
                        InPacket iPacket = new InPacket(Unpooled.wrappedBuffer(aContent));
                        PacketWrapper pPacketWrapper = null;
                        if (bLoopback) {
                            LoopbackCode pCode = LoopbackCode.GetLoopback(nHeader);
                            if (pCode != null) {
                                pPacketWrapper = pCode.pDecodePacket.ReadPacket(iPacket);
                            }
                        } else {
                            if ((nHeader - INCREMENT_CACHED_KEY_VALUE) >= ClientPacket.BeginUser.Get() && (nHeader - INCREMENT_CACHED_KEY_VALUE) < ClientPacket.Count.Get()) {
                                nHeader += INCREMENT_CACHED_KEY_VALUE;
                            }
                            ClientCode pCode = ClientCode.GetClient(nHeader);
                            if (pCode != null) {
                                pPacketWrapper = pCode.pDecodePacket.ReadPacket(iPacket);
                            }
                        }
                        if (pPacketWrapper != null) {
                            Compiler.Compile(pPacketWrapper);
                        }
                    }
                } catch (EOFException e) {
                    // no length specified, so this is intended.
                    long nTimeTaken = System.currentTimeMillis() - tPacketTime;
                    System.out.println("completed.");
                    Logger.LogReport("Done analyzing file [%s]... time: %dms", pFile.getName(), nTimeTaken);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            jLog.append(Compiler.GetOutputLog());
        } else if (jFileChooser1.getSelectedFile() != null) {
            jLog.append(jFileChooser1.getSelectedFile().getName() + "\r\n");
        }
        
        //Create the new window
        JFrame frame = new JFrame("Sniff ScriptMaker");
        //Add Content
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JScrollPane jScrollPane = new JScrollPane(jLog);
        frame.add(jScrollPane);
        frame.setBackground(Color.GRAY);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }//GEN-LAST:event_jFileChooser1ActionPerformed
}
