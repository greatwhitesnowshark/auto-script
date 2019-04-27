/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Five
 */
public class StringUtil {
    
    public static int CountStringPadding(String sLine) {
        int nPadding = 0;
        String sTrimmed = sLine.contains("(") ? sLine.substring(0, sLine.indexOf('(')) : sLine;
        while (sTrimmed.contains("\t")) {
            sTrimmed = sTrimmed.substring(1);
            nPadding++;
        }
        return nPadding;
    }
}
