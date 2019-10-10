/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package python;

import util.Pointer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static python.FunctionKeywordPython.*;

/**
 *
 * @author Sharky - Really more of "swordie-python" to JS
 */
public class PythonToJavascript {

    /** User/Directory Variables -- Modify to set project configuration **/

    public static String sPythonDirectory = "C:\\Users\\Chris\\Desktop\\Swordie\\scripts_new";//directory for your python scripts to convert to javascript formatting

    public static String sJavascriptDirectory = "C:\\Users\\Chris\\Desktop\\Swordie\\scripts_javascript\\";//directory for your newly created javascript files to populate (this also is used for the PurgeDirectory-project path setting)

    //output configuration settings
    public static boolean bFunctionToInstancesView = true, //List of functions not handled sorted by # of occurrences (high-low)
                          bFunctionToFileView = true, //List of functions not handled with a list of file reference locations for the occurrences
                          bPythonKeywordFileView = false, //List of files containing python keywords or syntax not already handled
                          bStaticImportsView = true, //List of constant/static import classes that reference objects and functions in the source that are not yet handled
                          bSkippedScriptView = true; //List of every script that wasn't created due to one of the above categorizations

    /** End User/Directory Variables **/

    public static final Map<String, LinkedList<String>> mScriptLines = Collections.synchronizedMap(new HashMap<>()); //stores copies of files and their lines

    public DebugInfo pDebug;

    public Modifier pModifier;

    public Path pDirPath;

    public long tTimestamp, tRunningTimestamp;


    public PythonToJavascript() {
        this.pDebug = new DebugInfo();
        this.pModifier = new StringModifier();
        this.tTimestamp = System.currentTimeMillis();
        this.tRunningTimestamp = 0;
        this.pDirPath = null;
    }


    public static void main(String[] args) {
        PythonToJavascript pPythtoJS = new PythonToJavascript();
        try {
            Pointer<Integer> pCount = new Pointer<>(0);
            Files.walk(Paths.get(sPythonDirectory)).forEach((pFile) -> {
                if (!pFile.toFile().isDirectory()) {
                    try {
                        pPythtoJS.ConvertPythonToJavascript(pFile.toFile());
                        pCount.setElement(pCount.element() + 1);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    if (pPythtoJS.tRunningTimestamp > 0 && pPythtoJS.pDirPath != null && pCount.element() > 0) {
                        util.Logger.LogReport(pPythtoJS.LogReportTime(pCount.element() + " files parsed in directory [" + pPythtoJS.pDirPath + "]....... ", pPythtoJS.tRunningTimestamp));
                    }
                    pPythtoJS.tRunningTimestamp = System.currentTimeMillis();
                    pPythtoJS.pDirPath = pFile.getFileName();
                    pCount.setElement(0);
                }
            });
            util.Logger.LogReport(pPythtoJS.LogReportTime(pCount.element() + " files parsed in directory [" + pPythtoJS.pDirPath + "]....... ", pPythtoJS.tRunningTimestamp));
            pPythtoJS.PrintDebugInfoOutput();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            util.Logger.LogReport(pPythtoJS.LogReportTime("Processing/compiling debug information and creating new scripts....... ", pPythtoJS.tRunningTimestamp));
            util.Logger.LogReport(pPythtoJS.LogReportTime("Program execution time....... ", pPythtoJS.tTimestamp));
        }
    }


    public final void ConvertPythonToJavascript(File pFile) throws IOException {
        String sPath = pFile.getCanonicalPath().contains("npc") ? "npc\\"
                :   pFile.getCanonicalPath().contains("quest") ? "quest\\"
                :   pFile.getCanonicalPath().contains("portal") ? "portal\\"
                :   pFile.getCanonicalPath().contains("reactor") ? "reactor\\"
                :   pFile.getCanonicalPath().contains("field") ? "field\\"
                :   pFile.getCanonicalPath().contains("item") ? "item\\"
                :   "invalid\\";
        sPath += pFile.getName();
        String sFileNameJavascript = sPath.contains(".py") ? sPath.replace(".py", ".js")
                :   !sPath.contains(".js") ? sPath + ".js"
                :   sPath;
        String sFileNamePython = sPath.contains(".js") ? sPath.replace(".js", ".py")
                :   !sPath.contains(".py") ? sPath + ".py"
                :   sPath;
        LinkedList<String> aScriptLines = new LinkedList<>();
        pModifier = new StringModifier();
        try (BufferedReader pReader = new BufferedReader(new FileReader(pFile))) {
            int nBlockPadding = -1, nLineNumber = 1, nArrayIndex = -1;
            String sClosingBracketAdditionalLine = "";
            while (pReader.ready()) {
                String sScriptLine = pReader.readLine();
                if (!sScriptLine.trim().isEmpty()) {
                    if (!pModifier.IsSkippedLine(sScriptLine)) {
                        pModifier = ForceComment.pInstance;
                        sScriptLine = pModifier.Convert(sScriptLine);
                        if (sScriptLine.trim().indexOf("//") != 0) {
                            if (pModifier.IsOpenArrayBracket(sScriptLine)) {
                                nArrayIndex++;
                            } else if (pModifier.IsCloseArrayBracket(sScriptLine)) {
                                nArrayIndex--;
                            }
                            sScriptLine = pModifier.ConvertComments(sScriptLine, nArrayIndex >= 0);
                            sScriptLine = pModifier.ConvertIfElseStatements(sScriptLine);
                            if (pModifier.IsCorrectPaddingForClosingBracket(sScriptLine, nBlockPadding)) {
                                if (nBlockPadding > 0) {
                                    while (nBlockPadding >= pModifier.GetLinePaddingByNumChar(sScriptLine, false) && nBlockPadding > 0) {
                                        if (!sClosingBracketAdditionalLine.isEmpty()) {
                                            aScriptLines.add(pModifier.ToPaddedString(sClosingBracketAdditionalLine, nBlockPadding + 4));
                                            sClosingBracketAdditionalLine = "";
                                        }
                                        aScriptLines.add(pModifier.ToPaddedString("}", nBlockPadding));
                                        nBlockPadding -= 4;
                                    }
                                    nBlockPadding = -1;
                                } else {
                                    if (nBlockPadding == pModifier.GetLinePaddingByNumChar(sScriptLine, false)) {
                                        if (!sClosingBracketAdditionalLine.isEmpty()) {
                                            aScriptLines.add(pModifier.ToPaddedString(sClosingBracketAdditionalLine, nBlockPadding + 4));
                                            sClosingBracketAdditionalLine = "";
                                        }
                                        aScriptLines.add(pModifier.ToPaddedString("}", nBlockPadding));
                                        nBlockPadding = -1;
                                    }
                                }
                            }
                            if (pModifier.IsClosingBracketInsertNeeded(sScriptLine)) {
                                nBlockPadding = pModifier.GetLinePaddingByNumChar(sScriptLine, false);
                            }
                            while (pModifier.IsLineSplitForMergeWithNextLine(sScriptLine) && !sScriptLine.contains("\")") && pReader.ready()) {
                                String sAppend = pReader.readLine().trim();
                                sAppend = pModifier.ConvertComments(sAppend, nArrayIndex >= 0);
                                if (sAppend.length() > 1 && sAppend.contains(("\""))) {
                                    sScriptLine = pModifier.ConvertMergeWithNextLine(sScriptLine, sAppend); //todo:: verify this holds up
                                } else {
                                    break;
                                }
                            }
                            pModifier = FunctionAppend.pInstance;
                            sScriptLine = pModifier.Convert(sScriptLine);
                        }
                        pModifier = FunctionKeyword.pInstance;
                        sScriptLine = pModifier.Convert(sScriptLine);
                        sScriptLine = pModifier.ConvertSemicolon(sScriptLine, nArrayIndex >= 0);
                        sScriptLine = ConvertPythonKeyword(sScriptLine, sFileNamePython, nLineNumber);
                        if (!sScriptLine.isEmpty()) {
                            String sAdditionalVariableLine = pModifier.GetIteratorInsertVarForLoop(sScriptLine);
                            if (!sAdditionalVariableLine.isEmpty()) {
                                aScriptLines.add(pModifier.ToPaddedString(sAdditionalVariableLine, nBlockPadding));
                            }
                            aScriptLines.add(sScriptLine);
                            if (sScriptLine.trim().indexOf("//") != 0) {
                                pModifier = FunctionInsert.pInstance;
                                String sAdditionalScriptLine = pModifier.Convert(sScriptLine);
                                if (!sAdditionalScriptLine.isEmpty()) {
                                    int nPadding = pModifier.GetLinePaddingByNumChar(sScriptLine, false);
                                    aScriptLines.add(pModifier.ToPaddedString(sAdditionalScriptLine, nPadding));
                                }
                                String sLoopVariable = pModifier.GetIteratorIncrementInsertForLoop(sScriptLine);
                                sClosingBracketAdditionalLine = !sLoopVariable.isEmpty() ? sLoopVariable : sClosingBracketAdditionalLine;
                                LogDebugInfo(sScriptLine, sFileNamePython);
                            }
                        }
                        nLineNumber++;
                    }
                }
            }
            while (nBlockPadding >= 0) {
                aScriptLines.add(pModifier.ToPaddedString("}", nBlockPadding));
                nBlockPadding -= 4;
            }
        }
        if (!aScriptLines.isEmpty()) {
            mScriptLines.put(sFileNameJavascript, aScriptLines);
        }
    }


    public void LogDebugInfo(String sScriptLine, String sFileNamePython) {
        pDebug.LogDebugInfo(sScriptLine, sFileNamePython);
    }


    public String LogReportTime(long tTime) {
        return LogReportTime("time:  ", tTime);
    }


    public String LogReportTime(String sText, long tTime) {
        int nMS = (int) ((System.currentTimeMillis() - tTime) % 1000);
        int nSeconds = (int) (System.currentTimeMillis() - tTime) / 1000;
        int nMinutes = nSeconds / 60;
        if (nMinutes > 0) nSeconds = nSeconds % 60;
        return (sText + ((nMinutes > 0 ? nMinutes + "m" : "") + nSeconds) + "." + nMS + "s");
    }


    public void PrintDebugInfoOutput() throws IOException {
        pDebug.PrintDebugInfoOutput();
    }
}
