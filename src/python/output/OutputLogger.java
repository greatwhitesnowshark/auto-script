package python.output;

import base.util.Logger;
import python.handle.AbstractHandler;
import python.handle.BlockHandler;

import java.io.*;
import java.util.*;

import static python.handle.KeywordReplace.mFunctionKeywordReplace;
import static python.handle.SyntaxModifier.mPythonKeywordToFileLine;
import static python.PythonToJavascript.*;

/**
 *
 * @author Sharky
 */
public class OutputLogger {

    public static List<String> aFilesCreated = new ArrayList<>(); //mostly used for verification and count
    public static List<String> aFilesSkip = new LinkedList<>(); //records a list of skipped files due to alien functions
    public LinkedHashMap<String, String> mWhileLoopDebug = new LinkedHashMap<>(); //all instances where a while loop or a for-each loop is used for manual referencing
    public Map<String, LinkedList<String>> mFunctionToFileSkip = new HashMap<>(); //debugging variables, (Key-Function, Value-Files)
    public Map<Integer, LinkedList<String>> mNumInstancesToFunctionSkip = new LinkedHashMap<>(); //(Key-# of Files, Value-List of Functions)
    public AbstractHandler pBlockModifier = new BlockHandler();
    public int nCreatedScripts, nSkippedScripts; //tracks the # of total created and skipped scripts

    public void LogDebugInfo(String sScriptLine, String sFileName) {
        String sLine = sScriptLine;
        String[] aLineSplitInterleaveQuotes = sScriptLine.contains("\"") ? sScriptLine.split("\"") : new String[] {};
        if (aLineSplitInterleaveQuotes.length > 0) {
            for (int i = 0; i < aLineSplitInterleaveQuotes.length; i++) {
                String sLineSegment = aLineSplitInterleaveQuotes[i];
                if (i % 2 == 0) {
                    LogWhileLoopInfo(sLineSegment, sScriptLine, sFileName);
                    LogDebugFunctionInfo(sLineSegment, sFileName);
                }
            }
        } else {
            LogWhileLoopInfo(sScriptLine, sScriptLine, sFileName);
            LogDebugFunctionInfo(sLine, sFileName);
        }
    }

    public void LogWhileLoopInfo(String sLineSegment, String sScriptLine, String sFileName) {
        if (sLineSegment.contains("while (") || (sLineSegment.contains("for") && sLineSegment.contains("in"))) {
            if (!mWhileLoopDebug.containsKey(sFileName)) {
                mWhileLoopDebug.put(sFileName, " '" + sScriptLine.trim() + "'  GetAdditionalVariableForLoop() --> '" + pBlockModifier.GetIteratorIncrementInsertForLoop(sScriptLine) + "'");
            }
        }
    }

    public void LogDebugFunctionInfo(String sLine, String sFileName) {
        String sDebugFuncName;
        while (sLine.contains(".")) {
            int nIndex = sLine.indexOf('.');
            if (sLine.length() > nIndex + 1) {
                String s = sLine.substring(sLine.indexOf('.') + 1);
                if (Character.isLowerCase(sLine.charAt(nIndex + 1)) ||
                        (Character.isUpperCase(s.charAt(0)) && s.contains("(") && !mFunctionKeywordReplace.containsValue(s.substring(0, s.indexOf('('))))) {
                    int nIdxMiddle = sLine.indexOf('.');
                    int nIdxStart = nIdxMiddle;
                    for (int i = nIdxMiddle; i >= 0; i--) {
                        if (i <= nIdxMiddle - 1) {
                            if (sLine.charAt(i) == '(') {
                                if (sLine.charAt(i + 1) == ')') {
                                    continue;
                                }
                                nIdxStart = i+1;
                                break;
                            }
                        }
                        if (sLine.charAt(i) == ' ' || sLine.charAt(i) == '!' || sLine.charAt(i) == '-' || sLine.charAt(i) == '[') {
                            nIdxStart = i+1;
                            break;
                        }
                    }
                    boolean bLogDebugReport = false;
                    if (nIdxStart != nIdxMiddle) {
                        String sDebug = sLine.substring(nIdxStart);
                        if (!sDebug.contains(".includes")) {
                            String sDebugImportClass = sLine.substring(nIdxStart, nIdxMiddle);
                            if ((!mFunctionKeywordReplace.containsValue(sDebugImportClass) || sDebugImportClass.equals("self")) && sDebug.contains("(")) {
                                int nIdxEnd = sDebug.indexOf('(');
                                sDebugFuncName = sDebug.substring(0, nIdxEnd);
                                if (bLogDebugReport && !sDebugFuncName.contains(".")) {
                                    util.Logger.LogReport("sDebugFuncName does not contain a '.' = " + sDebugFuncName);
                                    util.Logger.LogReport("sLine = " + sLine);
                                    util.Logger.LogReport("sDebug = " + sDebug);
                                    util.Logger.LogReport("sDebugImportClass = " + sDebugImportClass);
                                    util.Logger.LogReport("nIdxStart = %d, nIdxMiddle = %d, nIdxEnd = %d", nIdxStart, nIdxMiddle, nIdxEnd);
                                }
                                if (!mFunctionKeywordReplace.containsValue(sDebugFuncName) && !mFunctionKeywordReplace.containsValue(sDebugFuncName.substring(sDebugFuncName.indexOf('.'))) && !aRemovedFiles.contains(sFileName)) {
                                    if (!sDebugFuncName.isEmpty() && !sDebugFuncName.contains(",")) {
                                        LinkedList<String> aFileInfo;
                                        if (!mFunctionToFileSkip.keySet().contains(sDebugFuncName)) {
                                            aFileInfo = new LinkedList<>();
                                            aFileInfo.add(sFileName);
                                        } else {
                                            aFileInfo = mFunctionToFileSkip.get(sDebugFuncName);
                                            if (!aFileInfo.contains(sFileName)) {
                                                aFileInfo.add(sFileName);
                                            }
                                        }
                                        mFunctionToFileSkip.put(sDebugFuncName, aFileInfo);
                                        String sFileInfoName = sFileName.contains(".py") ? sFileName.replace(".py", ".js")
                                                : !sFileName.contains(".") ? sFileName.concat(".js")
                                                : sFileName;
                                        if (!aFilesSkip.contains(sFileInfoName.substring(0, sFileInfoName.indexOf(".")))) {
                                            aFilesSkip.add(sFileInfoName.substring(0, sFileInfoName.indexOf(".")));
                                        }
                                        sLine = sLine.substring(sLine.indexOf(sDebugFuncName) + sDebugFuncName.length());
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }
                sLine = sLine.substring(nIndex + 1);
                continue;
            }
            break;
        }
    }

    public void PrintDebugInfoOutput() throws IOException {
        for (String sFileName : mScriptLines.keySet()) {
            if (!aFilesSkip.contains(sFileName.substring(0, sFileName.indexOf("."))) && !aRemovedFiles.contains((sFileName.substring(0, sFileName.indexOf("."))))) {
                try (BufferedWriter pWriter = new BufferedWriter(new FileWriter(sJavascriptDirectory + sFileName))) {
                    LinkedList<String> aScriptLines = mScriptLines.get(sFileName);
                    for (int i = 0; i < aScriptLines.size(); i++) {
                        String sLine = aScriptLines.get(i);
                        if (!sLine.isBlank()) {
                            pWriter.write(sLine);
                            if (i < aScriptLines.size() - 1) {
                                pWriter.newLine();
                            }
                        }
                    }
                    if (!aFilesCreated.contains(sFileName.substring(0, sFileName.indexOf(".")))) {
                        aFilesCreated.add(sFileName.substring(0, sFileName.indexOf(".")));
                    }
                    nCreatedScripts++;
                } catch (Exception e) {
                    nSkippedScripts++;
                }
            } else {
                nSkippedScripts++;
            }
        }
        Object[] aSortedKey = null;
        StringBuilder sFunctionToFileView = new StringBuilder("<Function-to-Instances View>  overview/list of remaining alien functions and their number of instances"),
                sFunctionToFileLineView = new StringBuilder("<Function-to-File View>  overview/list of remaining alien functions and their file references"),
                sPythonKeywordToFileView = new StringBuilder("<Python-Keyword-to-File View>  overview/list of alien functions that relate to python's syntax keywords"),
                sStaticImportsView = new StringBuilder("<Static-Imports View>  overview/listing of every singular call to an alien imported object or class"),
                sSkippedFileView = new StringBuilder("<Skipped-Files View>  overview/listing of every skipped file that wasn't created due to unknown handling");

        if (!mFunctionToFileSkip.isEmpty()) {
            aSortedKey = mFunctionToFileSkip.keySet().toArray();
            Arrays.sort(aSortedKey);
            sFunctionToFileView.append("\r\n<These are the [" + aSortedKey.length + "] methods that are currently unaccounted for:> \r\n");
            sFunctionToFileLineView.append("\r\n<These are the [" + aSortedKey.length + "] methods that are currently unaccounted for (with file references):>");
            for (Object o : aSortedKey) {
                String sDebug = (String) o;
                int nCount = mFunctionToFileSkip.get(sDebug).size();
                if (!mNumInstancesToFunctionSkip.containsKey(nCount)) {
                    mNumInstancesToFunctionSkip.put(nCount, new LinkedList<>());
                }
                LinkedList<String> lFunc = mNumInstancesToFunctionSkip.get(nCount);
                if (!lFunc.contains(sDebug)) {
                    lFunc.add(sDebug);
                }
            }
            Object[] aSortedKeyCount = mNumInstancesToFunctionSkip.keySet().toArray();
            Arrays.sort(aSortedKeyCount);
            for (int i = aSortedKeyCount.length - 1; i >= 0; i--) {
                Object o = aSortedKeyCount[i];
                Integer nCount = (Integer) o;
                sFunctionToFileLineView.append("\r\n\r\nFunctions located in " + nCount + " files each: (" + mNumInstancesToFunctionSkip.get(nCount).size() + " functions total)");
                for (String sFunc : mNumInstancesToFunctionSkip.get(nCount)) {
                    sFunctionToFileView.append("\r\n\t:: " + sFunc + " ...number of occurrences: " + nCount);
                    sFunctionToFileLineView.append("\r\n\r\n\t\t:: " + sFunc);
                    for (String sFileInfo : mFunctionToFileSkip.get(sFunc)) {
                        sFunctionToFileLineView.append("\r\n\t\t\t\t- " + sFileInfo);
                    }
                }
            }
        }
        Map<String, LinkedList<String[]>> mAlienImportClass = new LinkedHashMap<>();
        if (!mPythonKeywordToFileLine.isEmpty()) {
            List<String> aFileCount = new LinkedList<>();
            for (String sKeyword : mPythonKeywordToFileLine.keySet()) {
                for (String[] aFileInfo : mPythonKeywordToFileLine.get(sKeyword)) {
                    if (!aFileCount.contains(aFileInfo[0])) {
                        aFileCount.add(aFileInfo[0]);
                    }
                }
            }
            sPythonKeywordToFileView.append("\r\n<These are the files that utilize python keywords ........ " + aFileCount.size() + " files>\r\n");
            for (String sKeyword : mPythonKeywordToFileLine.keySet()) {
                LinkedList<String[]> lFileInfo = mPythonKeywordToFileLine.get(sKeyword);
                if (lFileInfo != null && !lFileInfo.isEmpty()) {
                    sPythonKeywordToFileView.append("\r\n\r\n\t:: [keyword '" + sKeyword + "']  occurrences: " + lFileInfo.size() + "\r\n");
                    for (String[] aFileInfo : lFileInfo) {
                        String sFileName = aFileInfo[0];
                        String sLine = aFileInfo[1].trim();
                        String[] aConstantInfo = new String[] {sFileName, sLine};
                        sPythonKeywordToFileView.append("\r\n\t\t\t- " + sFileName + "  \r\n\t\t\t\t\t- " + sLine);
                        if (sLine.contains("import ") && sLine.contains("from ")) {
                            String[] aSplit = sLine.split(" ");
                            if (aSplit != null && aSplit.length > 3) {
                                String sConstant = aSplit[1] + "." + aSplit[3];
                                if (sConstant.contains(";")) {
                                    sConstant = sConstant.replace(";", "");
                                }
                                if (!mAlienImportClass.containsKey(sConstant)) {
                                    mAlienImportClass.put(sConstant, new LinkedList<>());
                                }
                                LinkedList<String[]> aConstantFileInfo = mAlienImportClass.get(sConstant);
                                if (!aConstantFileInfo.contains(aConstantInfo)) {
                                    aConstantFileInfo.add(aConstantInfo);
                                }
                            }
                        }
                    }
                }
            }
        }
        Map<String, LinkedHashMap<String, LinkedList<String>>> mAlienImportClassCall = new LinkedHashMap<>(); //Key #1: (String) sConstant. Value #1: HashMap return value -> Key #2: (String) sFileName. Value #2: (LinkedList<String>) sScriptLine
        Map<String, String> mImportClassPackage = new HashMap<>();
        if (!mAlienImportClass.isEmpty()) {
            for (String sConstant : mAlienImportClass.keySet()) {
                String sConstantSplit = sConstant.replace(".", "@@");
                String[] aImport = sConstantSplit.split("@@");
                String sImport = aImport[aImport.length - 1].contains(" ") ? aImport[aImport.length - 1].substring(0, aImport[aImport.length - 1].indexOf(" ")) : aImport[aImport.length - 1];
                for (String[] aFileInfo : mAlienImportClass.get(sConstant)) {
                    if (sConstant.contains(".")) {
                        String sFileName = sPythonDirectory + "\\" + aFileInfo[0];
                        int nLineNumber = 1;
                        String sPythonFileName = sFileName;
                        if (sFileName.contains("\\FirstUserEnter") || sFileName.contains("\\UserEnter") || sFileName.contains("\\FieldScript") || sFileName.contains("\\Etc")) {
                            sPythonFileName = sPythonFileName.replace("\\FirstUserEnter", "").replace("\\UserEnter", "").replace("\\FieldScript", "").replace("\\Etc", "");
                        }
                        try (BufferedReader pReader = new BufferedReader(new FileReader(sPythonFileName))) {
                            while (pReader.ready()) {
                                String sLine = pReader.readLine();
                                if (sLine.contains((sImport + ".")) || (sLine.contains(sImport) && sLine.contains("from ") && sLine.contains(" import "))) {
                                    if (!(sLine.contains("from ") && sLine.contains(" import "))) {
                                        String sLineTrimmed = sLine.substring(sLine.indexOf(sImport));
                                        int nIndex = -1;
                                        nIndex = sLineTrimmed.indexOf(" ") >= 0 ? sLineTrimmed.indexOf(" ") < nIndex || nIndex == -1 ? sLineTrimmed.indexOf(" ") : nIndex : nIndex;
                                        nIndex = sLineTrimmed.indexOf(")") >= 0 ? sLineTrimmed.indexOf(")") < nIndex || nIndex == -1 ? sLineTrimmed.indexOf(")") : nIndex : nIndex;
                                        nIndex = sLineTrimmed.indexOf("(") >= 0 ? sLineTrimmed.indexOf("(") < nIndex || nIndex == -1 ? sLineTrimmed.indexOf("(") : nIndex : nIndex;
                                        nIndex = sLineTrimmed.indexOf(",") >= 0 ? sLineTrimmed.indexOf(",") < nIndex || nIndex == -1 ? sLineTrimmed.indexOf(",") : nIndex : nIndex;
                                        if (nIndex >= 0) {
                                            sLineTrimmed = sLineTrimmed.substring(0, nIndex);
                                            if (!mFunctionKeywordReplace.keySet().contains(sLineTrimmed.trim())) {
                                                if (!mAlienImportClassCall.containsKey(sLineTrimmed.trim())) {
                                                    mAlienImportClassCall.put(sLineTrimmed.trim(), new LinkedHashMap<>());
                                                }
                                                LinkedHashMap<String, LinkedList<String>> mCall = mAlienImportClassCall.get(sLineTrimmed.trim());
                                                if (!mCall.keySet().contains(aFileInfo[0])) {
                                                    mCall.put(aFileInfo[0], new LinkedList<>());
                                                }
                                                LinkedList<String> lLines = mCall.get(aFileInfo[0]);
                                                String sLineToAdd = sLine.trim() + " [at line: " + nLineNumber + "]";
                                                if (!lLines.contains(sLineToAdd)) {
                                                    lLines.add(sLineToAdd);
                                                }
                                                mCall.put(aFileInfo[0], lLines);
                                                mAlienImportClassCall.put(sLineTrimmed.trim(), mCall);
                                                if (!mImportClassPackage.containsKey(sLineTrimmed.trim())) {
                                                    mImportClassPackage.put(sLineTrimmed.trim(), sConstant);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Object[] aSortedImportClassCall = mAlienImportClassCall.keySet().toArray();
            Arrays.sort(aSortedImportClassCall);
            LinkedList<String> aImportClass = new LinkedList<>();
            if (aSortedImportClassCall.length > 0) {
                sStaticImportsView.append("\r\n<These are the individual objects called through import-class declarations..... " + mAlienImportClassCall.keySet().size() + " calls>\r\n");
                for (Object o : aSortedImportClassCall) {
                    String sCall = (String) o, sClass;
                    if ((sClass = mImportClassPackage.get(sCall)) != null && !aImportClass.contains(sClass)) {
                        aImportClass.add(sClass);
                        ///sStaticImportsView.append("\r\n\r\n\t\t:: " + sCall);
                        sStaticImportsView.append("\r\n\t\t" + sClass);
                        /*if (!mAlienImportClassCall.get(sCall).keySet().isEmpty()) {
                            for (String sFileInfo : mAlienImportClassCall.get(sCall).keySet()) {
                                sStaticImportsView.append("\r\n\r\n\t\t\t\t\t-(" + sFileInfo + ")");
                                LinkedList<String> lLines = mAlienImportClassCall.get(sCall).get(sFileInfo);
                                if (!lLines.isEmpty()) {
                                    for (String sLine : lLines) {
                                        sStaticImportsView.append("\r\n\t\t\t\t\t\t\t" + sLine);
                                    }
                                }
                            }
                            sStaticImportsView.append("\r\n");
                        }*/
                    }
                }
            }
        }
        if (!aFilesSkip.isEmpty()) {
            Object[] aSortedSkippedFiles = aFilesSkip.toArray();
            Arrays.sort(aSortedSkippedFiles);
            if (aSortedSkippedFiles.length > 0) {
                sSkippedFileView.append("\r\n<These are the skipped files that were not created due to unknown handling syntax..... " + (aSortedSkippedFiles.length - aRemovedFiles.size()) + " files>\r\n");
                int i = 1;
                for (Object o : aSortedSkippedFiles) {
                    String sFlName = (String) o;
                    String sFleName = sFlName.contains(".") ? sFlName.substring(0, sFlName.indexOf(".")) : sFlName;
                    if (!aRemovedFiles.contains(sFleName + ".py")) {
                        sSkippedFileView.append("\r\n\t\t" + i++ + ": " + sFlName);
                    }
                }
            }
        }
        sFunctionToFileView.append("\r\n</Function-to-Instances View>\r\n");
        sFunctionToFileLineView.append("\r\n</Function-to-File View>\r\n");
        sPythonKeywordToFileView.append("\r\n</Python-Keyword-to-File View>\r\n");
        sStaticImportsView.append("\r\n</Static-Imports View>\r\n");
        sSkippedFileView.append("\r\n</Skipped-Files View>\r\n");
        util.Logger.LogReport("Number of files containing while loops......... " + mWhileLoopDebug.size());
        for (String sFile : mWhileLoopDebug.keySet()) {
            Logger.Println("(" + sFile + ") - " + mWhileLoopDebug.get(sFile));
        }
        if (!mWhileLoopDebug.isEmpty()) Logger.Println("\r\n");
        if (bFunctionToInstancesView) Logger.Println(sFunctionToFileView.toString());
        if (bFunctionToFileView) Logger.Println("\r\n" + sFunctionToFileLineView.toString());
        if (bPythonKeywordFileView) Logger.Println("\r\n" + sPythonKeywordToFileView.toString());
        if (bStaticImportsView && !mAlienImportClassCall.isEmpty()) Logger.Println("\r\n" + sStaticImportsView.toString());
        if (bSkippedScriptView && !aFilesSkip.isEmpty()) Logger.Println("\r\n" + sSkippedFileView.toString());
        if (!aRemovedFiles.isEmpty()) PrintRemovedFileOutput();
        if (!mAlienImportClass.isEmpty()) Logger.Println("Total number of constants classes/enums to import.......... " + mAlienImportClass.size());
        if (aSortedKey != null) Logger.Println("Total number of alien/unknown functions.................... " + aSortedKey.length);
        Logger.Println("Total number of created scripts............................ " + nCreatedScripts);
        Logger.Println("Total number of skipped scripts............................ " + (aFilesSkip.size() - aRemovedFiles.size()));
        Logger.Println("Total number of removed files.............................. " + aRemovedFiles.size());
        Logger.Println("");
    }
}
