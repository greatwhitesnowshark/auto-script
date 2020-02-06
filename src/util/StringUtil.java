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

    public static int GetLinePadding(String sLine) {
        return CountChar(sLine, ' ') > CountChar(sLine, '\t') ? CountChar(sLine, ' ') : CountChar(sLine, '\t');
    }

    public static int CountChar(String sLine, char ch) {
        int nCount = 0;
        for (char c : sLine.toCharArray()) {
            if (c == ch) {
                nCount++;
            } else {
                break;
            }
        }
        return nCount;
    }
}
