/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package python;

import com.sun.media.jfxmedia.logging.Logger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import util.StringUtil;

/**
 *
 * @author Sharky - Really more of "swordie-python" to JS
 */
public class PythonToJavascript {
    
    public static final Map<String, LinkedList<String>> mScriptLines = Collections.synchronizedMap(new HashMap<>());
    public static final Map<String, String> mReplace = new LinkedHashMap<>(); //all text-to-text replacements
    public static final Map<String, String> mFuncArgsAppend = new LinkedHashMap<>(); //appends arguments to funcs - "myFunc(arg1)->myFunc(arg1, true)"
    public static final Map<String, LinkedList<String>> mDebugFuncName = new HashMap<>(); //debugging variables, (Key-Function, Value-Files)
    public static final Map<Integer, LinkedList<String>> mDebugFuncNameCount = new LinkedHashMap<>(); //(Key-# of Files, Value-List of Functions)
    public static final Map<String, String> mFuncAppend = new HashMap<>(); //adds lines after specific patterns are read
    public static final Map<String, String> mReplacePythonKeyword = new LinkedHashMap<>(); //all python keyword-to-text replacements
    public static final Map<String, LinkedList<String[]>> mPythonKeywordFileInfo = new LinkedHashMap<>(); //records file and line information about unknown syntax
    public static final List<String> aFileSkip = new LinkedList<>(); //records a list of skipped files due to syntax errors
    public static int nCreatedScriptCount = 0, nSkippedScriptCount = 0;

    public static void main(String[] args) {
        try {
            boolean bPrintCompactView = true,
                    bPrintExpandedView = true,
                    bPrintSyntaxErrView = true,
                    bPrintConstantsView = true;
            String sDirectory = "C:\\Users\\Chris\\Desktop\\Swordie\\scripts_new";

            Files.walk(Paths.get(sDirectory)).forEach((pFile) -> {
                if (!pFile.toFile().isDirectory()) {
                    try {
                        ConvertPythonToJavascript(pFile.toFile());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Logger.logMsg(Logger.INFO, "Processing directory [" + pFile.getFileName() + "]....");
                }
            });
            
            sDirectory = "C:\\Users\\Chris\\Desktop\\Swordie\\scripts_javascript\\";

            for (String sFileName : mScriptLines.keySet()) {
                if (!aFileSkip.contains(sFileName.split("\\\\")[1])) {
                    try (BufferedWriter pWriter = new BufferedWriter(new FileWriter(sDirectory + sFileName))) {
                        LinkedList<String> aScriptLines = mScriptLines.get(sFileName);

                        for (String sLine : aScriptLines) {
                            pWriter.write(sLine);
                            pWriter.newLine();
                        }
                    }
                    nCreatedScriptCount++;
                } else {
                    nSkippedScriptCount++;
                }
            }

            Object[] aSortedKey = null;
            StringBuilder sCompactView = new StringBuilder("<CompactView - overview of functions not accounted for yet>"),
                    sExpandedView = new StringBuilder("<ExpandedView - same as CompactView but contains file references>"),
                    sFuncSkipped = new StringBuilder("<SyntaxErrView - overview of keywords relating to python not accounted for yet>"),
                    sConstantsView = new StringBuilder("<ImportConstantsView - overview of imports/constant-classes not accounted for yet>");

            if (!mDebugFuncName.isEmpty()) {
                aSortedKey = mDebugFuncName.keySet().toArray();
                Arrays.sort(aSortedKey);

                sCompactView.append("\r\n<These are the [" + aSortedKey.length + "] methods that are currently unaccounted for:> \r\n");
                sExpandedView.append("\r\n<These are the [" + aSortedKey.length + "] methods that are currently unaccounted for (with file references):> \r\n");

                for (Object o : aSortedKey) {
                    String sDebug = (String) o;
                    int nCount = mDebugFuncName.get(sDebug).size();

                    if (!mDebugFuncNameCount.containsKey(nCount)) {
                        mDebugFuncNameCount.put(nCount, new LinkedList<>());
                    }

                    LinkedList<String> lFunc = mDebugFuncNameCount.get(nCount);

                    if (!lFunc.contains(sDebug)) {
                        lFunc.add(sDebug);
                    }
                }

                Object[] aSortedKeyCount = mDebugFuncNameCount.keySet().toArray();
                Arrays.sort(aSortedKeyCount);

                for (int i = aSortedKeyCount.length - 1; i >= 0; i--) {
                    Object o = aSortedKeyCount[i];
                    Integer nCount = (Integer) o;

                    sExpandedView.append("\r\nFunctions located in " + nCount + " files each: (" + mDebugFuncNameCount.get(nCount).size() + " functions total)");

                    for (String sFunc : mDebugFuncNameCount.get(nCount)) {
                        sCompactView.append("\r\n\t:: " + sFunc + " ...number of occurrences: " + nCount);
                        sExpandedView.append("\r\n\r\n\t\t:: " + sFunc);

                        for (String sFileInfo : mDebugFuncName.get(sFunc)) {
                            sExpandedView.append("\r\n\r\n\t\t\t\t- " + sFileInfo);
                        }
                    }
                }
            }

            Map<String, LinkedList<String[]>> mConstants = new LinkedHashMap<>();
            if (!mPythonKeywordFileInfo.isEmpty()) {
                List<String> aFileCount = new LinkedList<>();
                for (String sKeyword : mPythonKeywordFileInfo.keySet()) {
                    for (String[] aFileInfo : mPythonKeywordFileInfo.get(sKeyword)) {
                        if (!aFileCount.contains(aFileInfo[0])) {
                            aFileCount.add(aFileInfo[0]);
                        }
                    }
                }
                sFuncSkipped.append("\r\n<These are the files that utilize python keywords ........ " + aFileCount.size() + " files>\r\n");
                for (String sKeyword : mPythonKeywordFileInfo.keySet()) {
                    LinkedList<String[]> lFileInfo = mPythonKeywordFileInfo.get(sKeyword);
                    if (lFileInfo != null && !lFileInfo.isEmpty()) {
                        sFuncSkipped.append("\r\n\r\n\t:: [keyword '" + sKeyword + "']  occurrences: " + lFileInfo.size() + "\r\n");
                        for (String[] aFileInfo : lFileInfo) {
                            String sFileName = aFileInfo[0];
                            String sLine = aFileInfo[1].trim();
                            String[] aConstantInfo = new String[] {sFileName, sLine};
                            sFuncSkipped.append("\r\n\t\t\t- " + sFileName + "  \r\n\t\t\t\t\t- " + sLine);
                            if (sLine.contains("import ") && sLine.contains("from ")) {
                                String[] aSplit = sLine.split(" ");
                                if (aSplit != null && aSplit.length > 3) {
                                    String sConstant = aSplit[1] + "." + aSplit[3];
                                    if (sConstant.contains(";")) {
                                        sConstant = sConstant.replace(";", "");
                                    }
                                    if (!mConstants.containsKey(sConstant)) {
                                        mConstants.put(sConstant, new LinkedList<>());
                                    }
                                    LinkedList<String[]> aConstantFileInfo = mConstants.get(sConstant);
                                    if (!aConstantFileInfo.contains(aConstantInfo)) {
                                        aConstantFileInfo.add(aConstantInfo);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!mConstants.isEmpty()) {
                sConstantsView.append("\r\n<These are the imported classes/constants that need to be transferred over..... " + mConstants.size() + " classes>\r\n");
                for (String sConstant : mConstants.keySet()) {
                    sConstantsView.append("\r\n\r\n\t\t:: " + sConstant + "  .....number of file occurrences: " + mConstants.get(sConstant).size() + "\r\n");
                    for (String[] aFileInfo : mConstants.get(sConstant)) {
                        sConstantsView.append("\r\n\t\t\t\t- (" + aFileInfo[0] + ") " + aFileInfo[1]);
                    }
                }
            }

            sCompactView.append("\r\n</CompactView>\r\n");
            sExpandedView.append("\r\n</ExpandedView>\r\n");
            sFuncSkipped.append("\r\n</SyntaxErrView>\r\n");
            sConstantsView.append("\r\n</ImportConstantsView>\r\n");

            if (bPrintCompactView) System.out.println(sCompactView.toString());
            if (bPrintExpandedView) System.out.println("\r\n" + sExpandedView.toString());
            if (bPrintSyntaxErrView) System.out.println("\r\n" + sFuncSkipped.toString());
            if (bPrintConstantsView) System.out.println("\r\n" + sConstantsView.toString());

            if (aSortedKey != null) System.out.println("Total number of unaccounted methods/funcs:  " + aSortedKey.length);
            if (!mConstants.isEmpty()) System.out.println("Total number of constants classes/enums to import: " + mConstants.size());
            System.out.println("Total number of created scripts:  " + nCreatedScriptCount);
            System.out.println("Total number of skipped scripts: " + nSkippedScriptCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static final String ConvertComments(String sScriptLine) {
        if (!sScriptLine.isEmpty()) {
            if (sScriptLine.trim().charAt(0) == '#') {
                sScriptLine = sScriptLine.replace("#", "// ");
            } else if (sScriptLine.contains(" #")) {
                sScriptLine = sScriptLine.replace(" #", (!sScriptLine.contains("{") && !sScriptLine.contains(":") ? "; // " : " // "));
            }
            if (GetBlockPaddingChar(sScriptLine, false) < 4) {
                sScriptLine = sScriptLine.trim();
            }
        }
        return sScriptLine;
    }
    
    public static final String ConvertIfElseStatements(String sScriptLine) {
        if (sScriptLine.contains("while ") && !sScriptLine.contains("while (")) {
            sScriptLine = sScriptLine.replace("while ", "while (");
        } else if (sScriptLine.contains("elif")) {
            sScriptLine = sScriptLine.replace("elif", "} else if (");
        } else if (sScriptLine.contains("if ") && sScriptLine.contains(":")) {
            sScriptLine = sScriptLine.replace("if ", "if (");
        } else if (sScriptLine.contains("else:")) {
            sScriptLine = sScriptLine.replace("else:", "} else:");
        }
        String sComment = "";
        if (sScriptLine.contains(":")) {
            if (sScriptLine.contains("//")) {
                sComment = sScriptLine.substring(sScriptLine.indexOf("//"));
                sScriptLine = sScriptLine.substring(0, sScriptLine.indexOf("//"));
            }
            if (sScriptLine.contains("\"")) {
                String[] aScriptLine = sScriptLine.split("\"");
                sScriptLine = "";
                for (int i = 0; i < aScriptLine.length; i++) {
                    if (i % 2 == 0) {
                        if (aScriptLine[i].contains(":")) {
                            aScriptLine[i] = aScriptLine[i].replace(":", sScriptLine.contains("else:") ? " {" : ") {");
                        }
                    }
                    sScriptLine += aScriptLine[i];
                    if (i != aScriptLine.length - 1) {
                        sScriptLine += "\"";
                    }
                }
            } else {
                sScriptLine = sScriptLine.replace(":", sScriptLine.contains("else:") ? " {" : ") {");
            }
        }
        if (sScriptLine.contains(" ( ")) {
            sScriptLine = sScriptLine.replace(" ( ", " (");
        }
        sScriptLine += sComment;
        return sScriptLine;
    }
    
    public static final String ConvertFuncArgsAppend(String sScriptLine) {
        for (String sLine : mFuncArgsAppend.keySet()) {
            if (sScriptLine.contains(sLine)) {
                if (sScriptLine.contains(")") && !sScriptLine.contains(mFuncArgsAppend.get(sLine))) {
                    sScriptLine = sScriptLine.replace(")", mFuncArgsAppend.get(sLine) + ")");
                    break;
                }
            }
        }
        return sScriptLine;
    }
    
    public static final String ConvertReplaceFunc(String sScriptLine) {
        for (String sLine : mReplace.keySet()) {
            if (sScriptLine.contains(sLine)) {
                sScriptLine = sScriptLine.replace(sLine, mReplace.get(sLine));
            }
        }
        return sScriptLine;
    }
    
    public static final String ConvertSemicolon(String sScriptLine) {
        if (!sScriptLine.contains("//") && sScriptLine.trim().charAt(sScriptLine.trim().length() -1) != '{') {
            sScriptLine += ";";
        }
        return sScriptLine;
    }
    
    public static final String ConvertMergeWithNextLine(String sScriptLine, String sAppend) {
        int nLineSubstr, nAppendSubstr;
        boolean bOperatorAppend = sScriptLine.trim().charAt(sScriptLine.trim().length() - 1) == '+';
        if (bOperatorAppend) {
            nLineSubstr = sScriptLine.lastIndexOf("+") + 1;
            nAppendSubstr = 0;
        } else {
            if (sAppend.trim().length() > 3 && sAppend.trim().substring(0, 3).equals("+ \"")) {
                nLineSubstr = sScriptLine.lastIndexOf("\"");
                nAppendSubstr = sAppend.indexOf("\"") + 1;
            } else {
                nLineSubstr = sScriptLine.length() - 1;
                nAppendSubstr = sAppend.indexOf("\"") + 1;
            }
        }
        return sScriptLine.substring(0, nLineSubstr) + (nAppendSubstr == 0 ? " " : "") + sAppend.substring(nAppendSubstr);
    }
    
    public static final String GetAdditionalScriptLine(String sScriptLine) {
        for (String sKey : mFuncAppend.keySet()) {
            if (sScriptLine.contains(sKey)) {
                return mFuncAppend.get(sKey);
            }
        }
        return "";
    }
    
    public static final boolean IsSkippedLine(String sScriptLine) {
        return sScriptLine.contains("sm.dispose") || sScriptLine.contains("sm.diposse") || sScriptLine.contains("sm.disose");
    }
    
    public static final boolean IsClosingBracketInsert(String sScriptLine, int nBlockPadding) {
        if (sScriptLine.contains("}") && GetBlockPaddingChar(sScriptLine, false) == nBlockPadding) {
            return false;
        }
        return nBlockPadding >= 0 && GetBlockPaddingChar(sScriptLine, false) <= nBlockPadding;
    }
    
    public static final boolean IsClosingBracketNeeded(String sScriptLine) {
        return sScriptLine.contains("{") && (sScriptLine.contains("if") || sScriptLine.contains("else"));
    }
    
    public static final boolean IsLineSplit(String sScriptLine) {
        char c = sScriptLine.trim().length() > 1 ? sScriptLine.trim().charAt(sScriptLine.trim().length() - 1) : '_', 
             b = sScriptLine.trim().length() > 2 ? sScriptLine.trim().charAt(sScriptLine.trim().length() - 2) : '_', 
             a = sScriptLine.trim().length() > 3 ? sScriptLine.trim().charAt(sScriptLine.trim().length() - 3) : '_';
        if (c == '+' || c == '"' || (c == ')' && (b == '(' || (b == ')' && a == '(')))) {
            return (sScriptLine.contains("sm.") && (sScriptLine.contains("(\"") || sScriptLine.contains(", \""))) || sScriptLine.contains("= \"");
        }
        return false;
    }
    
    public static final int GetBlockPaddingChar(String sScriptLine, boolean bSet) {
        int nCharPad = StringUtil.CountStringPaddingChar(sScriptLine);
        int nTabPad = StringUtil.CountStringPaddingTab(sScriptLine);
        return nCharPad > nTabPad * 4 ? nCharPad : nTabPad * 4;
    }

    public static final String ConvertPythonSingleKeyword(String sKeyword, String sLineSegment, String sScriptLine) {
        String sKeywordSetDebug = "";
        String sLineCompare = sLineSegment;
        switch (sKeyword) {
            case "global":
                sLineSegment = sLineSegment.replace("global ", "").replace(";", " = 0;");
                break;
            case "None":
                while (sLineSegment.contains("is not None")) {
                    sLineSegment = sLineSegment.replace("is not None", "!= null");
                }
                while (sLineSegment.contains("is None")) {
                    sLineSegment = sLineSegment.replace("is None", "== null");
                }
                while (sLineSegment.contains(" = None")) {
                    sLineSegment = sLineSegment.replace(" = None", " = null");
                }
                break;
            case "not":
                if (sLineSegment.contains("not ") && sLineSegment.contains(" == ")) {
                    String[] aLineSplit = sLineSegment.substring(sLineSegment.indexOf("not ")).split(" ");
                    if (aLineSplit.length > 2) {
                        String sExpression = aLineSplit[2];
                        if (sExpression.equals("==")) {
                            while (sLineSegment.contains("not ") && sLineSegment.contains("==")) {
                                sLineSegment = sLineSegment.replace("not ", "");
                                sLineSegment = sLineSegment.replace("==", "!=");
                            }
                        }
                    }
                }
                while (sLineSegment.contains("not ")) {
                    sLineSegment = sLineSegment.replace("not ", "!");
                }
                break;
            case "def":
                while (sLineSegment.contains("def ")) {
                    sLineSegment = sLineSegment.replace("def ", "function ");
                }
                if (sLineSegment.contains(")) {")) {
                    sLineSegment = sLineSegment.replace(")) {", ") {");
                }
                break;
            case "len(":
                while (sLineSegment.contains("len(")) {
                    String sLineSegmentPrefix = sLineSegment.substring(0, sLineSegment.indexOf("len("));
                    int nIdxStart = sLineSegment.indexOf("len("), nIdxEnd = nIdxStart;
                    int nOpenPCount = 0;
                    int nClosedPCount = 0;
                    for (int i = nIdxStart; i < sLineSegment.length(); i++, nIdxEnd++) {
                        char c = sLineSegment.charAt(i);
                        if (c == '(') {
                            nOpenPCount++;
                        } else if (c == ')') {
                            nClosedPCount++;
                            if (nClosedPCount == nOpenPCount) {
                                sLineSegmentPrefix += ".length";
                                break;
                            }
                        }
                        sLineSegmentPrefix += c;
                    }
                    sLineSegmentPrefix += sLineSegment.substring(nIdxEnd+1);
                    sLineSegmentPrefix = sLineSegmentPrefix.replace("len(", "");
                    sLineSegment = sLineSegmentPrefix;
                }
                break;
            default:
                while (sLineSegment.contains(sKeyword)) {
                    if (mReplacePythonKeyword.get(sKeyword).equals(sKeyword)) {
                        break;
                    }
                    sLineSegment = sLineSegment.replace(sKeyword, mReplacePythonKeyword.get(sKeyword));
                }
                break;
        }
        if (!sKeywordSetDebug.isEmpty() && sKeyword.equals(sKeywordSetDebug) && !sLineCompare.equals(sLineSegment)) {
            System.out.println("[keyword '" + sKeyword + "' debug]");
            System.out.println("\tsLineCompare: " + sLineCompare);
            System.out.println("\tsLineSegment: " + sLineSegment + "\r\n");
        }
        return sLineSegment;
    }

    public static final String ConvertPythonKeywordLogInfo(String sLineSegment, String sScriptLine, String sFileName, int nLineNumber) {
        for (String sKeyword : mReplacePythonKeyword.keySet()) {
            if ((sLineSegment.contains(sKeyword) || (sKeyword.contains("(") && sLineSegment.contains(" " + sKeyword))) && !(sLineSegment.contains("(\"") || sLineSegment.contains("\")"))) {
                int nIdxKeyword = sLineSegment.indexOf(sKeyword);
                if (nIdxKeyword > 0 && (sLineSegment.charAt(nIdxKeyword-1) != ' ' && sLineSegment.charAt(nIdxKeyword-1) != '(' && sLineSegment.charAt(nIdxKeyword-1) != '\t')) {
                    String sLineSegmentSuffix = sLineSegment.substring(nIdxKeyword + sKeyword.length());
                    if (sLineSegment.length() > nIdxKeyword + sKeyword.length() && (sLineSegmentSuffix.contains(" " + sKeyword) || sLineSegmentSuffix.contains("(" + sKeyword))) {
                        System.out.println("\r\n[MANUAL-FIX SYNTAX OCCURRENCE] [" + sKeyword + "]  " + sScriptLine);
                        System.out.println("\t\tat File -  " + sFileName + "\r\n");
                    }
                    continue;
                }
                sLineSegment = ConvertPythonSingleKeyword(sKeyword, sLineSegment, sScriptLine);
                if (sLineSegment.contains(sKeyword)) {
                    if (!mPythonKeywordFileInfo.containsKey(sKeyword)) {
                        mPythonKeywordFileInfo.put(sKeyword, new LinkedList<>());
                    }
                    LinkedList<String[]> lFileInfo = mPythonKeywordFileInfo.get(sKeyword);
                    if (!lFileInfo.contains(new String[]{sFileName, sScriptLine + " [at line " + nLineNumber + "]"})) {
                        lFileInfo.add(new String[]{sFileName, sScriptLine + " [at line " + nLineNumber + "]"});
                    }
                    mPythonKeywordFileInfo.put(sKeyword, lFileInfo);
                }
            }
        }
        return sLineSegment;
    }

    public static final String ConvertPythonKeyword(String sScriptLine, String sFileName, int nLineNumber) {
        String sLine = "";
        String sLineComments = sScriptLine.contains("//") ? sScriptLine.substring(sScriptLine.indexOf("//")) : "";
        String sLineCommentsTrimmed = sScriptLine.contains("//") ? sScriptLine.substring(0, sScriptLine.indexOf("//")) : sScriptLine;
        String[] aLineSplitInterleaveQuotes = sLineCommentsTrimmed.contains("\"") ? sLineCommentsTrimmed.split("\"") : new String[] {};
        if (aLineSplitInterleaveQuotes.length > 0) {
            for (int i = 0; i < aLineSplitInterleaveQuotes.length; i++) {
                String sLineSegment = aLineSplitInterleaveQuotes[i];
                if (i % 2 == 0) {
                    sLineSegment = ConvertPythonKeywordLogInfo(sLineSegment, sScriptLine, sFileName, nLineNumber);
                }
                sLine += sLineSegment;
                if (i != aLineSplitInterleaveQuotes.length - 1) {
                    sLine += "\"";
                }
            }
            if (!sLineComments.isEmpty()) {
                sLine += sLineComments;
            }
        } else {
            if (!sLineCommentsTrimmed.isEmpty()) {
                sScriptLine = ConvertPythonKeywordLogInfo(sLineCommentsTrimmed, sScriptLine, sFileName, nLineNumber);
                sScriptLine += sLineComments;
            }
            sLine += sScriptLine;
        }
        if (sLine.contains("import ") && sLine.contains("from ")) {
            sLine = "";
        }
        return sLine;
    }
    
    public static final void LogDebugInfo(String sScriptLine, String sFileName) {
        String sDebugFuncName = "";
        if (sScriptLine.contains(".")) {
            int nIndex = sScriptLine.indexOf(".");
            if (sScriptLine.length() > nIndex + 1) {
                String s = sScriptLine.substring(sScriptLine.indexOf('.'));
                if (Character.isLowerCase(sScriptLine.charAt(nIndex + 1)) ||
                        (s.contains("(") && mReplace.containsValue(s.substring(0, s.indexOf('('))))) {
                    int nIdxMiddle = sScriptLine.indexOf('.');
                    int nIdxStart = nIdxMiddle;
                    for (int i = nIdxMiddle; i >= 0; i--) {
                        if (sScriptLine.charAt(i) == ' ') {
                            nIdxStart = i+1;
                            break;
                        }
                    }
                    if (nIdxStart != nIdxMiddle) {
                        String sDebug = sScriptLine.substring(nIdxStart);
                        if (sDebug.contains("(")) {
                            int nIdxEnd = sDebug.indexOf("(");
                            sDebugFuncName = sDebug.substring(0, nIdxEnd);
                            if (mDebugFuncName.containsKey(sDebugFuncName)) {
                                sDebugFuncName = "";
                            }
                        }
                    }
                }
            }
        }
        if (!sDebugFuncName.isEmpty()) {LinkedList<String> aFileInfo;
            if (!mDebugFuncName.keySet().contains(sDebugFuncName)) {
                aFileInfo = new LinkedList<>();
                aFileInfo.add(sFileName);
            } else {
                aFileInfo = mDebugFuncName.get(sDebugFuncName);
                if (!aFileInfo.contains(sFileName)) {
                    aFileInfo.add(sFileName);
                }
            }
            mDebugFuncName.put(sDebugFuncName, aFileInfo);
            String sFileInfoName = sFileName.contains(".py") ? sFileName.replace(".py", ".js")
                    : !sFileName.contains(".") ? sFileName.concat(".js")
                    : sFileName;
            if (!aFileSkip.contains(sFileInfoName)) {
                aFileSkip.add(sFileInfoName);
            }
        }
    }
    
    public static final void ConvertPythonToJavascript(File pFile) throws IOException {
        LinkedList<String> aScriptLines = new LinkedList<>();
        try (BufferedReader pReader = new BufferedReader(new FileReader(pFile))) {
            int nBlockPadding = -1, nLineNumber = 1;
            while (pReader.ready()) {
                String sScriptLine = pReader.readLine();
                if (!sScriptLine.trim().isEmpty()) {
                    if (!IsSkippedLine(sScriptLine)) {
                        sScriptLine = ConvertComments(sScriptLine);
                        sScriptLine = ConvertIfElseStatements(sScriptLine);
                        if (IsClosingBracketInsert(sScriptLine, nBlockPadding)) {
                            while (nBlockPadding > GetBlockPaddingChar(sScriptLine, false)) {
                                aScriptLines.add(ToPaddedString("}", nBlockPadding));
                                nBlockPadding -= 4;
                            }
                            nBlockPadding = -1;
                        }
                        if (IsClosingBracketNeeded(sScriptLine)) {
                            nBlockPadding = GetBlockPaddingChar(sScriptLine, false);
                        }
                        while (IsLineSplit(sScriptLine) && !sScriptLine.contains("\")") && pReader.ready()) {
                            String sAppend = pReader.readLine().trim();
                            sAppend = ConvertComments(sAppend);
                            if (sAppend.length() > 1 && sAppend.contains(("\""))) {
                                sScriptLine = ConvertMergeWithNextLine(sScriptLine, sAppend); //todo:: verify this holds up
                            } else {
                                break;
                            }
                        }
                        sScriptLine = ConvertFuncArgsAppend(sScriptLine);
                        sScriptLine = ConvertReplaceFunc(sScriptLine);
                        sScriptLine = ConvertSemicolon(sScriptLine);
                        sScriptLine = ConvertPythonKeyword(sScriptLine, pFile.getName(), nLineNumber);
                        if (!sScriptLine.isEmpty()) {
                            aScriptLines.add(sScriptLine);
                            String sAdditionalScriptLine = GetAdditionalScriptLine(sScriptLine);
                            if (!sAdditionalScriptLine.isEmpty()) {
                                int nPadding = GetBlockPaddingChar(sScriptLine, false);
                                aScriptLines.add(ToPaddedString(sAdditionalScriptLine, nPadding));
                            }
                        }
                        nLineNumber++;
                    }
                    LogDebugInfo(sScriptLine, pFile.getName());
                }
            }
            while (nBlockPadding >= 0) {
                aScriptLines.add(ToPaddedString("}", nBlockPadding));
                nBlockPadding -= 4;
            }
        }
        if (!aScriptLines.isEmpty()) {
            String sPrefix = pFile.getCanonicalPath().contains("npc") ? "npc\\"
                    :   pFile.getCanonicalPath().contains("quest") ? "quest\\"
                    :   pFile.getCanonicalPath().contains("portal") ? "portal\\"
                    :   pFile.getCanonicalPath().contains("reactor") ? "reactor\\"
                    :   pFile.getCanonicalPath().contains("field") ? "field\\"
                    :   pFile.getCanonicalPath().contains("item") ? "item\\"
                    :   "invalid\\";
            String sName = pFile.getName().contains(".py") ? pFile.getName().replace(".py", ".js")
                    :   !pFile.getName().contains(".") ? pFile.getName().concat(".js")
                    :   pFile.getName();
            mScriptLines.put((sPrefix + sName), aScriptLines);
        }
    }
    
    public static String ToPaddedString(String sText, int nPadding) {
        StringBuilder pBuilder = new StringBuilder();
        for (int i = 0; i < nPadding; i++) {
            pBuilder.append(" ");
        }
        pBuilder.append(sText);
        return pBuilder.toString();
    }


    static {

        mReplacePythonKeyword.put("None", "");//not done
        mReplacePythonKeyword.put("and", "&&");
        mReplacePythonKeyword.put("assert", "assert");//not done
        mReplacePythonKeyword.put("class", "class");//not done
        mReplacePythonKeyword.put("def", "");
        mReplacePythonKeyword.put("del", "del");//not done
        mReplacePythonKeyword.put("except", "except");//not done
        mReplacePythonKeyword.put("finally", "finally");//not done
        //mReplacePythonKeyword.put("from", "from");//not done
        mReplacePythonKeyword.put("global", "global");//not done
        mReplacePythonKeyword.put("import", "import");//not done
        mReplacePythonKeyword.put("lambda", "lambda");//not done //add to skipped
        mReplacePythonKeyword.put("len(", "len(");//not done
        mReplacePythonKeyword.put("list(", "list(");//not done //add to skipped
        mReplacePythonKeyword.put("map(", "map(");//not done //add to skipped
        mReplacePythonKeyword.put("nonlocal", "nonlocal");//not done
        mReplacePythonKeyword.put("not", "");
        mReplacePythonKeyword.put("pass", "pass");//not done
        mReplacePythonKeyword.put("raise", "raise");//not done
        mReplacePythonKeyword.put("try", "try");//not done
        mReplacePythonKeyword.put("with", "with");//not done
        mReplacePythonKeyword.put("yield", "yield");//not done
        
        mReplace.put("addAP", "UserIncAP");
        mReplace.put("addDamageSkin", "UserSaveDamageSkin");
        mReplace.put("addLevel", "UserIncLevel");
        mReplace.put("addMaxHP", "UserIncMHP");
        mReplace.put("addMaxMP", "UserIncMMP");
        mReplace.put("addSP", "UserIncSP");
        mReplace.put("addSp", "UserIncSP");
        mReplace.put("avatarLookSet(", "OnUserInGameDirectionEvent(InGameDirectionEvent.AvatarLookSet, ");
        mReplace.put("avatarOriented", "EffectAvatarOriented");
        mReplace.put("addPopupSay", "UserAddPopupSay");
        mReplace.put("addPopUpSay", "UserAddPopupSay");
        mReplace.put("addQRValue", "QuestRecordSet");
        mReplace.put("updateQRValue", "QuestRecordSet");
        mReplace.put("balloonMsg", "UserBalloonMsg");
        mReplace.put("blind", "FieldEffectBlind");
        mReplace.put("bgmVolume", "FieldEffectBGMVolumeOnly");
        mReplace.put("canHold", "InventoryIsSlotFreeItemID");
        mReplace.put("changeBGM", "FieldEffectChangeBGM");
        mReplace.put("changeChannelAndWarp", "RegisterTransferFieldChannel"); //review these to determine if these should be instanced locations
        mReplace.put("changeCharacterLook", "TryChangeHairSkinOrFace");
        mReplace.put("changeFoothold", "SendDynamicObjUrusSync");
        mReplace.put("chat", "UserScriptMessage");
        mReplace.put("chatBlue", "UserScriptMessage");
        mReplace.put("chatRed", "UserScriptMessage");
        mReplace.put("chatScript", "UserScriptProgressMessage");
        mReplace.put("checkAllianceName", "IsAllianceNameFree");
        mReplace.put("checkParty", "UserIsPartyReadyCheck");
        mReplace.put("closeUI", "UserCloseUI");
        mReplace.put("completeQuest", "QuestRecordSetComplete");
        mReplace.put("completeQuestNoCheck", "QuestRecordSetComplete");
        mReplace.put("completeQuestNoRewards", "QuestRecordSetComplete");
        mReplace.put("consumeItem", "UserStatChangeItemUseRequest");
        //mReplace.put("createAlliance", "createAlliance");//todo::
        mReplace.put("createClock", "FieldClock");
        mReplace.put("createFallingCatcher", "CreateFieldFallingCatcher");
        mReplace.put("createFieldTextEffect", "OnUserTextEffect");
        mReplace.put("createStopWatch", "FieldClockStopwatch");
        mReplace.put("createQuestWithQRValue", "QuestRecordExSet");
        mReplace.put("curNodeEventEnd", "OnInGameCurNodeEventEnd");
        mReplace.put("deductMesos", "UserDeductMoney");
        mReplace.put("deleteQuest", "QuestRecordRemove");
        mReplace.put("delta", "dlta"); //sharky note:: for python keyword 'del'
        //mReplace.put("doEventAndSendDelay", "doEventAndSendDelay"); //todo:: this is horse shit, needs to be addressed manually
        mReplace.put("dropItem", "FieldDropItem");
        mReplace.put("faceOff(", "OnUserInGameDirectionEvent(InGameDirectionEvent.FaceOff, ");
        mReplace.put("fadeInOut", "OnUserFadeInOutEffect");
        mReplace.put("False", "false");
        mReplace.put("forcedAction(", "OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedAction, ");
        mReplace.put("forcedFlip(", "OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedFlip, ");
        mReplace.put("forcedInput(", "OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedInput, ");
        mReplace.put("forcedMove(", "OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedMove, ");
        mReplace.put("gainItem", "InventoryItemExchange");
        mReplace.put("getChr", "GetUser");
        mReplace.put("getDay", "GetDay");
        //mReplace.put("getDropInRect", "getDropInRect"); //todo:: probably remove occurrences of this as we process drops from
        mReplace.put("getFieldID", "UserGetFieldID");
        //mReplace.put("getMPExpByMobId", "getMPExpByMobId"); //todo:: probably remove occurrences of this because its MP
        mReplace.put("getMesos", "UserGetMoney");
        //mReplace.put("getMonsterParkCount", "getMonsterParkCount"); //todo:: probably remove occurrences of this because its MP
        //mReplace.put("getNpcScriptInfo", "getNpcScriptInfo"); //todo:: remove this stupid shit
        //mReplace.put("getParty", "getParty"); //todo:: rewrite these to use our party-event handles
        mReplace.put("getPartyMembersInSameField", "UserGetPartyMemberFieldCount");
        mReplace.put("getQRValue", "QuestRecordExGet");
        mReplace.put("getQuantityOfItem", "InventoryGetItemCount");
        mReplace.put("getRandomIntBelow", "GetRand");
        mReplace.put("getReactorQuantity", "FieldGetReactorCount");
        mReplace.put("getReactorState", "FieldGetReactorState");
        mReplace.put("getReturnField", "GetReturnFieldID");
        //mReplace.put("getSkillByItem", "getSkillByItemID"); //todo:: verify where this should come from
        //mReplace.put("getUnionCoin", "getUnionCoin"); //todo:: remove all occurrences
        //mReplace.put("getUnionLevel", "getUnionLevel"); //todo:: remove all occurrences
        //mReplace.put("getnOptionByCTS", "getnOptionByCTS"); //todo:: modify this argument given
        //mReplace.put("giveAndEquip", "giveAndEquip"); //todo:: remove all occurrences (probably if only for beginner tutorials)
        //mReplace.put("giveCTS", "giveCTS"); //todo:: modify this argument given
        mReplace.put("giveExp", "UserIncEXP"); 
        mReplace.put("giveItem", "InventoryItemExchange");
        mReplace.put("giveMesos", "UserIncMoney");
        mReplace.put("giveSkill", "UserLearnSkill");
        mReplace.put("golluxPortalOpen", "FieldGolluxPortalEnable");
        //mReplace.put("hasCTS", "hasCTS"); //todo:: modify this argument given
        mReplace.put("hasHadQuest", "IsQuestActive");
        mReplace.put("hasItem", "InventoryIsHoldingItemCount");
        mReplace.put("hasMobsInField", "FieldMobExists");
        mReplace.put("hasQuest", "IsQuestInProgress");
        mReplace.put("hasQuestCompleted", "IsQuestComplete");
        mReplace.put("hasQuestWithValue", "QuestRecordExExists");
        mReplace.put("hasSkill", "UserSkillIsLearned");
        //mReplace.put("hasTutor", "hasTutor"); //todo:: figure out wtf tutor is
        mReplace.put("heal", "UserHealMax");
        mReplace.put("hideNpcByTemplateId", "FieldNpcViewOrHide");
        mReplace.put("hideUser(", "OnUserInGameDirectionEvent(InGameDirectionEvent.VansheeMode, ");
        mReplace.put("hireTutor", "UserHireTutor");
        mReplace.put("increaseReactorState", "FieldSetReactorState");
        //mReplace.put("isAbleToLevelUpMakingSkill", "isAbleToLevelUpMakingSkill"); //todo::
        mReplace.put("isEquipped", "InventoryIsEquippedItem");
        //mReplace.put("isFinishedEscort", "isFinishedEscort"); //todo:: remove usages of this, looks custom
        mReplace.put("isPartyLeader", "UserIsPartyBoss");
        //mReplace.put("jobAdvance", "jobAdvance"); //todo:: remove occurrences of this
        mReplace.put("killMobs", "FieldRemoveAllMob");
        mReplace.put("killmobs", "FieldRemoveAllMob");
        mReplace.put("levelUntil", "UserIncLevelSet");
        mReplace.put("localEmotion", "UserLocalEmotion");
        mReplace.put("lockInGameUI", "OnSetInGameDirectionMode");
        mReplace.put("moveCamera(", "OnUserInGameDirectionEvent(InGameDirectionEvent.CameraMove, ");
        mReplace.put("moveCameraBack(", "OnUserInGameDirectionEvent(InGameDirectionEvent.CameraMove, true, ");
        mReplace.put("moveLayer", "EffectOnOffLayer");
        mReplace.put("moveNpcByTemplateId", "OnForceMoveByScript"); //todo:: verify that the arguments passed correspond
        mReplace.put("moveParticleEff", "UserMoveParticleEff");
        mReplace.put("offLayer", "EffectOffLayer");
        mReplace.put("spineScreen", "FieldEffectSpineScreen");
        mReplace.put("offSpineScreen", "FieldEffectOffSpineScreen");
        mReplace.put("onLayer", "EffectOnLayer");
        mReplace.put("openDimensionalMirror", "UserOpenUIDimensionalMirror");//todo:: TEST & see if/how this works with AskSlideMenu
        mReplace.put("openNodestone", "ConsumeNodestone");
        mReplace.put("openNpc", "UserEnforceNpcChat");
        mReplace.put("openUI", "UserOpenUI");
        mReplace.put("patternInputRequest(", "OnUserInGameDirectionEvent(InGameDirectionEvent.PatternInputRequest, ");
        mReplace.put("playExclSoundWithDownBGM", "EffectPlayExclSoundWithDownBGM");
        mReplace.put("playSound", "EffectSound");
        //mReplace.put("playVideoByScript", "playVideoByScript"); //todo:: wtf is this, likely remove all occurrences
        mReplace.put("progressMessageFont", "SendProgressMessageFont");
        mReplace.put("randomInt", "rndInt"); //sharky note:: so that the 'and' python keyword doesn't pick this shit up
        mReplace.put("removeCTS", "UserRemoveBuff"); //todo:: probably need to change arguments if they used objects for CTS
        mReplace.put("removeAdditionalEffect(", "OnUserInGameDirectionEvent(InGameDirectionEvent.RemoveAdditionalEffect");
        mReplace.put("removeMobByTemplateId", "FieldRemoveMob");
        mReplace.put("removeNpc", "FieldVanishNpc");
        mReplace.put("removeOverlapScreen", "FieldEffectRemoveOverlapDetail");
        mReplace.put("removeReactor", "FieldRemoveAllReactor");
        mReplace.put("removeSkill", "UserRemoveSkill");
        mReplace.put("reservedEffect", "EffectReserved");
        mReplace.put("reservedEffectRepeat", "EffectReservedRepeat");
        mReplace.put("resetNpcSpecialActionByTemplateId", "NpcResetSpecialAction");
        mReplace.put("resetParam", "ResetModifyFlag");
        //mReplace.put("rideVehicle", "rideVehicle"); //todo:: cbf
        mReplace.put("sayMonologue(", "OnUserInGameDirectionEvent(InGameDirectionEvent.Monologue, ");
        mReplace.put("sendAskAccept", "AskAccept");
        mReplace.put("sendAskAvatar", "AskAvatar");
        mReplace.put("sendAskMenuNext", "AskMenuNoESC");
        mReplace.put("sendAskNumber", "AskNumber");
        mReplace.put("sendAskSelectMenu", "AskMenu");
        mReplace.put("sendAskText", "AskText");
        mReplace.put("sendAskYesNo", "AskYesNo");
        mReplace.put("sendPrev", "Say");
        mReplace.put("sendSay", "Say");
        mReplace.put("sendNext", "SayNext");
        mReplace.put("sendDelay(", "OnUserInGameDirectionEvent(InGameDirectionEvent.Delay, ");
        mReplace.put("setAP", "UserSetAP");
        mReplace.put("setCameraOnNpc(", "OnUserInGameDirectionEvent(InGameDirectionEvent.CameraZoom, ");
        mReplace.put("setDEX", "UserSetDex");
        mReplace.put("setDeathCount", "UserSetDeathCount");
        mReplace.put("setFieldColour", "setFieldColour");
        //mReplace.put("setFuncKeyByScript", "UserSetFuncMapKey");
        mReplace.put("setHp", "UserSetHP");
        mReplace.put("setINT", "UserSetINT");
        //mReplace.put("setInstanceInfo", "setInstanceInfo"); //todo:: remove all occurrences
        //mReplace.put("setInstanceTime", "setInstanceTime"); //todo:: remove all occurrences
        //mReplace.put("setInstanceTimer", "setInstanceTimer"); //todo:: remove all occurrences
        //mReplace.put("setJob", "setJob"); //todo:: examine and probably remove all occurrences as we handle this differently
        mReplace.put("setLUK", "UserSetLUK");
        mReplace.put("setLevel", "UserIncLevelSet");
        //mReplace.put("setMapTaggedObjectVisible", "setMapTaggedObjectVisible"); //look at this one
        mReplace.put("setMaxHp", "UserSetMHP");
        mReplace.put("setMaxMp", "UserSetMMP");
        mReplace.put("setMp", "UserSetMP");
        mReplace.put("setQRValue", "QuestRecordExSet");
        mReplace.put("setReturnField", "UserSetReturnFieldID");
        mReplace.put("setSpeakerID", "SetNPCTemplateID");
        mReplace.put("setInnerOverrideSpeakerTemplateID", "SetNPCTemplateID");
        mReplace.put("setSpeakerType", "SetNPCType");
        mReplace.put("setColor", "SetNPCColor");
        mReplace.put("setSTR", "UserSetSTR");
        mReplace.put("setUnionCoin", "UserSetUnionCoin");
        mReplace.put("showBalloonMsg", "UserBalloonMsg");
        //mReplace.put("showBalloonMsgOnNpc", "showBalloonMsgOnNpc"); //todo::
        //mReplace.put("showEffectOnPosition", "showEffectOnPosition"); //todo::
        //mReplace.put("showEffectToField", "showEffectToField"); //todo::
        mReplace.put("showEffect(", "OnUserInGameDirectionEvent(InGameDirectionEvent.EffectPlay, ");
        mReplace.put("showNpcSpecialActionByTemplateId", "OnNpcSpecialAction");
        mReplace.put("showFadeTransition", "FieldEffectOverlapDetail");
        mReplace.put("sm.", "self.");
        mReplace.put("spawnNpc", "FieldSummonNpc");
        mReplace.put("startQuest", "QuestRecordTryStartAct");
        mReplace.put("str(", "(");
        mReplace.put("systemMessage", "UserScriptMessage");
        mReplace.put("True", "true");
        mReplace.put("warp", "RegisterTransferField");
        mReplace.put("zoomCamera(", "OnUserInGameDirectionEvent(InGameDirectionEvent.CameraZoom, ");
        mReplace.put("zoomCameraNoResponse(", "OnUserInGameDirectionEvent(InGameDirectionEvent.CameraZoom, ");
        mReplace.put("oxChatPlayerAsSpeaker(", "SetModifyFlag(SpeakerTypeID.ScenarioIlluChat, SpeakerTypeID.NpcReplacedByUser");
        mReplace.put("flipBoxChat(", "SetModifyFlag(SpeakerTypeID.ScenarioIlluChat, SpeakerTypeID.FlipImage");
        mReplace.put("flipDialogue(", "SetModifyFlag(SpeakerTypeID.NpcReplayedByNpc");
        mReplace.put("flipDialoguePlayerAsSpeaker(", "SetModifyFlag(SpeakerTypeID.FlipImage, SpeakerTypeID.NpcReplacedByUser");
        mReplace.put("flipSpeaker(", "SetModifyFlag(SpeakerTypeID.FlipImage");
        mReplace.put("setPlayerAsSpeaker(", "SetModifyFlag(SpeakerTypeID.NpcReplacedByUser");
        mReplace.put("removeEscapeButton(", "SetModifyFlag(SpeakerTypeID.NoESC");
        mReplace.put("setBoxChat(", "SetModifyFlag(SpeakerTypeID.ScenarioIlluChat");
        mReplace.put("addEscapeButton(", "ResetModifyFlag(SpeakerTypeID.NoESC");
        mReplace.put("flipBoxChatPlayerNoEscape", "SetModifyFlag(SpeakerTypeID.ScenarioIlluChat, SpeakerTypeID.FlipImage, SpeakerTypeID.NpcReplacedByUser, SpeakerTypeID.NoESC");
        mReplace.put("sendAskSlideMenu", "AskSlideMenu");
        mReplace.put("setParam", "SetModifyFlag");
        mReplace.put("showFieldEffect", "EffectScreenAutoLetterBox");
        mReplace.put("flipNpcByTemplateId", "SetForceFlip");
        mReplace.put("spawnMob", "FieldSummonMob");
        mReplace.put("setJob", "UserJob");
        mReplace.put("setMapTaggedObjectVisible", "UserSetMapTaggedObjectVisible");
        mReplace.put("speechBalloon", "UserSpeechBalloonEffect");
        mReplace.put("jobAdvance", "UserJobAdvance");
        mReplace.put("getParty", "UserGetPartyData");
        mReplace.put("giveAndEquip", "UserEquipNewItem");
        mReplace.put("teleportToPortal", "UserTeleport");
        mReplace.put("showWeatherNotice", "UserWeatherEffectNotice");
        mReplace.put("getnOptionByCTS", "UserGetNOption");
        mReplace.put("invokeForParty", "UserExecFuncForParty"); //(Has TODO: Sharky Shit for the actual invoking of func)
        mReplace.put("rideVehicle", "UserRideVehicle");
        mReplace.put("setFieldColour", "EffectColorChange");
        mReplace.put("teleportInField", "UserTeleportPosition");
        mReplace.put("tutorAutomatedMsg", "UserTutorialMsg"); //(Must exec UserHireTutor first)
        mReplace.put("setFieldGrey", "EffectGrayScale");
        mReplace.put("showObjectFieldEffect", "EffectObject");
        mReplace.put("showWeatherNoticeToField", "FieldWeatherEffectNotice");
        mReplace.put("giveCTS", "UserGiveCTS");
        mReplace.put("setFuncKeyByScript", "UserSetFuncKey");
        mReplace.put("showEffectToField", "FieldEffectUOL");
        mReplace.put("showHP", "MobShowHP");
        mReplace.put("useItem", "UserGiveBuff");
        mReplace.put("getOffFieldEffectFromWz", "EffectTopScreenDelayed");
        mReplace.put("showFieldBackgroundEffect", "EffectScreenDelayed");
        mReplace.put("spawnMobOnChar", "FieldSummonMobOnChar");
        mReplace.put("getDropInRect", "FieldFindDropInRect");
        mReplace.put("getPartySize", "UserGetPartyMemberCount");
        mReplace.put("hasCTS", "UserHasCTS");
        mReplace.put("showOffFieldEffect", "EffectTopScreenDelayed");
        mReplace.put("spawnMobWithAppearType", "FieldSummonMob");
        mReplace.put("tutorCustomMsg", "UserTutorialMsg"); //(Must exec UserHireTutor first)
        mReplace.put("getSkillByItem", "UserGetSkillByItemID");

        //These methods get passed before the FuncReplaceConvert so using swordie-func names
        mFuncArgsAppend.put("addAP", ", true");
        mFuncArgsAppend.put("addLevel", ", true");
        mFuncArgsAppend.put("addMaxHP", ", true");
        mFuncArgsAppend.put("addMaxMP", ", true");
        mFuncArgsAppend.put("addSP", ", true");
        mFuncArgsAppend.put("addSp", ", true");
        mFuncArgsAppend.put("giveExp", ", true");
        mFuncArgsAppend.put("giveMesos", ", true");
        mFuncArgsAppend.put("sendPrev", ", true");

        //Adds necessary actions as subsequent lines on the discovery of a given pattern-key
        mFuncAppend.put("InGameDirectionEvent.Delay", "self.Wait();");



    }
}
