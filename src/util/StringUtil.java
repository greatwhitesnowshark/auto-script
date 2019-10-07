/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Sharky
 */
public class StringUtil {
    
    public static String AddStringPaddingChar(String sLine, int nPadding) {
        StringBuilder sLinePadded = new StringBuilder();
        for (int i = 0; i < nPadding; i++) {
            sLinePadded.append(" ");
        }
        sLinePadded.append(sLine);
        return sLinePadded.toString();
    }

    public static String TrimWhitespaceFromEnd(String sLine) {
        if (!sLine.isEmpty()) {
            while (sLine.length() > 0 && sLine.charAt(sLine.length() - 1) == ' ') {
                sLine = sLine.substring(0, sLine.length() - 1);
            }
        }
        return sLine;
    }
    
    public static int CountStringPaddingChar(String sLine) {
        int nPadding = 0;
        while (nPadding < sLine.length() && sLine.subSequence(nPadding, nPadding + 1).equals(" ")) {
            nPadding++;
        }
        return nPadding;
    }
    
    public static int CountStringPaddingTab(String sLine) {
        int nPadding = 0;
        String sTrimmed = sLine.contains("(") ? sLine.substring(0, sLine.indexOf('(')) : sLine;
        while (sTrimmed.contains("\t")) {
            sTrimmed = sTrimmed.substring(1);
            nPadding++;
        }
        return nPadding;
    }
}
