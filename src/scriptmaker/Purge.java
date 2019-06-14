/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;

/**
 *
 * @author Sharky
 */
public class Purge {
    
    public static String sNpcDirectory = "script\\npc\\",
                         sQuestDirectory = "script\\quest\\",
                         sPortalDirectory = "script\\portal\\",
                         sReactorDirectory = "script\\reactor\\",
                         sDeveloperDirectory = "script\\developer\\",
                         sFieldScriptDirectory = "script\\field\\FieldScript\\",
                         sUserEnterDirectory = "script\\field\\UserEnter\\",
                         sFirstUserEnterDirectory = "script\\field\\FirstUserEnter\\";
    
    public static void main(String[] args) {
        DeleteFromDirectory(new String[] {sNpcDirectory, sQuestDirectory, sPortalDirectory, sReactorDirectory, sDeveloperDirectory, sFieldScriptDirectory, sUserEnterDirectory, sFirstUserEnterDirectory});
    }
    
    public static void DeleteFromDirectory(String... aDir) {
        for (String sDirectory : aDir) {
            File pDirectory = new File(sDirectory);
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
