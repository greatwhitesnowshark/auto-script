/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package python;

import java.io.File;

/**
 *
 * @author Sharky
 */
public class PurgeDirectory {
    
    public static String sDirectory = "C:\\Users\\Chris\\Desktop\\Swordie\\scripts_javascript\\";
    public static String sNpcDirectory = "npc\\",
                         sQuestDirectory = "quest\\",
                         sPortalDirectory = "portal\\",
                         sReactorDirectory = "reactor\\",
                         sDeveloperDirectory = "developer\\",
                         sFieldScriptDirectory = "field\\fieldscript\\",
                         sUserEnterDirectory = "field\\userenter\\",
                         sFirstUserEnterDirectory = "field\\firstuserenter\\";
    
    public static void main(String[] args) {
        DeleteFromDirectory(new String[] {sNpcDirectory, sQuestDirectory, sPortalDirectory, sReactorDirectory, sDeveloperDirectory, sFieldScriptDirectory, sUserEnterDirectory, sFirstUserEnterDirectory});
    }
    
    public static void DeleteFromDirectory(String... aDir) {
        for (String sPath : aDir) {
            File pDirectory = new File(sDirectory + sPath);
            if (pDirectory.isDirectory()) {
                for(File pFile: pDirectory.listFiles()) {
                    if (!pFile.isDirectory()) {
                        pFile.delete();
                    }
                }
            }
        }
    }
}