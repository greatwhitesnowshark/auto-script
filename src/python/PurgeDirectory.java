/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package python;

import util.Logger;

import java.io.File;

/**
 *
 * @author Sharky
 */
public class PurgeDirectory {

    public static String sNpcDirectory = "npc\\",
                         sQuestDirectory = "quest\\",
                         sPortalDirectory = "portal\\",
                         sItemScriptDirectory = "item\\",
                         sReactorDirectory = "reactor\\",
                         sDeveloperDirectory = "developer\\",
                         sFieldDirectory = "field\\",
                         sFieldScriptDirectory = "field\\fieldscript\\",
                         sUserEnterDirectory = "field\\userenter\\",
                         sFirstUserEnterDirectory = "field\\firstuserenter\\";
    
    public static void main(String[] args) {
        DeleteFromDirectory(new String[] {sNpcDirectory, sQuestDirectory, sPortalDirectory, sItemScriptDirectory, sReactorDirectory, sDeveloperDirectory, sFieldDirectory, sFieldScriptDirectory, sUserEnterDirectory, sFirstUserEnterDirectory});
    }
    
    public static void DeleteFromDirectory(String... aDir) {
        for (String sPath : aDir) {
            File pDirectory = new File(PythonToJavascript.sJavascriptDirectory + sPath);
            if (pDirectory.isDirectory()) {
                for(File pFile: pDirectory.listFiles()) {
                    if (!pFile.isDirectory()) {
                        pFile.delete();
                    }
                }
                Logger.LogReport("Purging directory - " + sPath + "...");
            }
        }
        Logger.LogReport("-All directories purged.");
    }
}