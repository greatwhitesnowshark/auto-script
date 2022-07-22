/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package python;

import base.util.Logger;
import python.handle.*;
import python.output.OutputLogger;
import python.output.SortFieldScript;
import python.output.SortFieldScript.FieldScriptType;
import python.output.SortFiveScripts;
import util.Pointer;
import util.StringUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static python.handle.SyntaxModifier.*;

/**
 *
 * @author Sharky - Really more of "<unnamed-target-app>-python" to JS
 */
public class PythonToJavascript {

    /** User/Directory Variables -- Modify to set project configuration **/

    public static String sPythonDirectory = "scripts_current_py\\scripts\\";//directory for your python scripts to convert to javascript formatting

    public static String sJavascriptDirectory = "scripts_current_js\\";//directory for your newly created javascript files to populate (this also is used for the PurgeDirectory-project path setting)

    //output configuration settings
    public static boolean bFunctionToInstancesView = true, //List of functions not handled sorted by # of occurrences (high-low)
            bFunctionToFileView = true, //List of functions not handled with a list of file reference locations for the occurrences
            bPythonKeywordFileView = true, //List of files containing python keywords or syntax not already handled
            bStaticImportsView = true, //List of constant/static import classes that reference objects and functions in the source that are not yet handled
            bSkippedScriptView = true; //List of every script that wasn't created due to one of the above categorizations

    /** End User/Directory Variables **/

    public static final Map<String, LinkedList<String>> mScriptLines = Collections.synchronizedMap(new HashMap<>()); //stores copies of files and their lines

    public static final Map<String, LinkedList<String>> mRemovedFiles = Collections.synchronizedMap(new HashMap<>());

    public static final List<String> aRemovedFiles = new ArrayList<>();

    public OutputLogger pDebug;

    public AbstractHandler pBlockModifier;

    public Path pDirPath;

    public long tTimestamp, tRunningTimestamp;


    public PythonToJavascript() {
        this.pDebug = new OutputLogger();
        this.pBlockModifier = new BlockHandler();
        this.tTimestamp = System.currentTimeMillis();
        this.tRunningTimestamp = 0;
        this.pDirPath = null;
    }

    public static void main(String[] args) {
        PythonToJavascript pPythtoJS = new PythonToJavascript();
        pPythtoJS.tRunningTimestamp = System.currentTimeMillis();
        PurgeDirectory.main(null);
        util.Logger.LogReport(pPythtoJS.LogReportTime("All javascript directories purged....... ", pPythtoJS.tRunningTimestamp));
        pPythtoJS.tRunningTimestamp = System.currentTimeMillis();
        SortFieldScript.SortFieldScriptMap();
        util.Logger.LogReport(pPythtoJS.LogReportTime("All directory naming sorted for field scripts....... ", pPythtoJS.tRunningTimestamp));
        pPythtoJS.tRunningTimestamp = System.currentTimeMillis();
        SortFiveScripts.SortFiveAuthorScripts();
        util.Logger.LogReport(pPythtoJS.LogReportTime("All user authored scripts sorted for exclusion....... ", pPythtoJS.tRunningTimestamp));
        try {
            Pointer<Integer> pCount = new Pointer<>(0);
            Files.walk(Paths.get(sPythonDirectory)).forEach((pFile) -> {
                if (!pFile.toFile().isDirectory()) {
                    try {
                        String sName = pFile.toFile().getName();
                        if (sName.contains(".py") && !SortFiveScripts.IsFiveAuthorScript(sName)) {
                            pPythtoJS.ConvertPythonToJavascript(pFile.toFile());
                            pCount.setElement(pCount.element() + 1);
                        }
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
        if (sPath.contains("field\\")) {
            FieldScriptType t = SortFieldScript.GetFieldScriptType(pFile.getName());
            sPath += t.name() + "\\";
        }
        sPath += pFile.getName();
        String sFileNameJavascript = sPath.contains(".py") ? sPath.replace(".py", ".js")
                :   !sPath.contains(".js") ? sPath + ".js"
                :   sPath;
        String sFileNamePython = sPath.contains(".js") ? sPath.replace(".js", ".py")
                :   !sPath.contains(".py") ? sPath + ".py"
                :   sPath;
        boolean bRemoveScript = false;
        LinkedList<String> aScriptLines = new LinkedList<>();
        pBlockModifier = new BlockHandler();
        try (BufferedReader pReader = new BufferedReader(new FileReader(pFile))) {
            int nBlockPadding = -1, nLineNumber = 1, nArrayIndex = -1;
            String sClosingBracketAdditionalLine = "", sOverrideNextLineRead = "";
            while (pReader.ready() || !sOverrideNextLineRead.isBlank()) {
                String sScriptLine;
                if (!sOverrideNextLineRead.isBlank()) {
                    sScriptLine = sOverrideNextLineRead;
                    sOverrideNextLineRead = "";
                } else {
                    sScriptLine = pReader.readLine();
                }
                if (!sScriptLine.trim().isEmpty()) {
                    if (!pBlockModifier.IsSkippedLine(sScriptLine)) {
                        pBlockModifier = KeywordIgnore.pInstance;
                        sScriptLine = pBlockModifier.Convert(sScriptLine);
                        if (sScriptLine.trim().indexOf("//") != 0) {
                            if (pBlockModifier.IsOpenArrayBracket(sScriptLine)) {
                                nArrayIndex++;
                            } else if (pBlockModifier.IsCloseArrayBracket(sScriptLine, nArrayIndex != -1)) {
                                nArrayIndex--;
                            }
                            sScriptLine = pBlockModifier.ConvertComments(sScriptLine, nArrayIndex >= 0);
                            sScriptLine = pBlockModifier.ConvertIfElseStatements(sScriptLine);
                            if (pBlockModifier.IsCorrectPaddingForClosingBracket(sScriptLine, nBlockPadding)) {
                                if (nBlockPadding > 0) {
                                    while (nBlockPadding >= StringUtil.GetLinePadding(sScriptLine) && nBlockPadding >= 0) {
                                        if (!sClosingBracketAdditionalLine.isEmpty()) {
                                            aScriptLines.add(pBlockModifier.ToPaddedString(sClosingBracketAdditionalLine, nBlockPadding + 4));
                                            sClosingBracketAdditionalLine = "";
                                        }
                                        if (nBlockPadding == StringUtil.GetLinePadding(sScriptLine) && sScriptLine.contains("}")) {
                                            nBlockPadding = -1;
                                            continue;
                                        }
                                        aScriptLines.add(pBlockModifier.ToPaddedString("}", nBlockPadding));
                                        nBlockPadding -= 4;
                                    }
                                    if (nBlockPadding < -1) {
                                        nBlockPadding = -1;
                                    }
                                } else {
                                    if (nBlockPadding == StringUtil.GetLinePadding(sScriptLine)) {
                                        if (!sClosingBracketAdditionalLine.isEmpty()) {
                                            aScriptLines.add(pBlockModifier.ToPaddedString(sClosingBracketAdditionalLine, nBlockPadding + 4));
                                            sClosingBracketAdditionalLine = "";
                                        }
                                        aScriptLines.add(pBlockModifier.ToPaddedString("}", nBlockPadding));
                                        nBlockPadding = -1;
                                    }
                                }
                            }
                            if (pBlockModifier.IsClosingBracketInsertNeeded(sScriptLine)) {
                                nBlockPadding = StringUtil.GetLinePadding(sScriptLine);
                            }
                            if (pReader.ready()) {
                                String sAppend = "", s;
                                while (pReader.ready() && (sAppend = pReader.readLine()) != null && pBlockModifier.IsMergeWithNextLine(sScriptLine, sAppend)) {
                                    sAppend = pBlockModifier.ConvertComments(sAppend, nArrayIndex >= 0).trim();
                                    if (sAppend.length() > 0 && sAppend.contains(("\""))) {
                                        if ((s = pBlockModifier.ConvertMergeWithNextLine(sScriptLine, sAppend)) != null) { //todo:: verify this holds up
                                            sScriptLine = s;
                                        }
                                    }
                                }
                                sOverrideNextLineRead = sAppend;
                            }
                            pBlockModifier = FunctionAppendArgument.pInstance;
                            sScriptLine = pBlockModifier.Convert(sScriptLine);
                        }
                        pBlockModifier = KeywordReplace.pInstance;
                        sScriptLine = pBlockModifier.Convert(sScriptLine);
                        sScriptLine = pBlockModifier.ConvertSemicolon(sScriptLine, nArrayIndex >= 0);
                        sScriptLine = pBlockModifier.ConvertAskMenu(sScriptLine);
                        sScriptLine = pBlockModifier.ConvertNestedAskYesNo(sScriptLine);
                        sScriptLine = pBlockModifier.ConvertForEachLoop(sScriptLine);
                        sScriptLine = ConvertPythonKeyword(sScriptLine, sFileNamePython, nLineNumber);
                        if (!sScriptLine.isEmpty()) {
                            sScriptLine = pBlockModifier.ConvertCleanComments(sScriptLine);
                            pBlockModifier = MistranslatedText.pInstance;
                            sScriptLine = pBlockModifier.Convert(sScriptLine);
                            pBlockModifier = QuoteEndOfLine.pInstance;
                            sScriptLine = pBlockModifier.Convert(sScriptLine);
                            pBlockModifier = MiscStringPart.pInstance;
                            sScriptLine = pBlockModifier.Convert(sScriptLine);
                            pBlockModifier = IncorrectTranslations.pInstance;
                            sScriptLine = pBlockModifier.Convert(sScriptLine);
                            ((IncorrectTranslations) pBlockModifier).Log(sFileNameJavascript, sScriptLine);
                            String sAdditionalVariableLine = pBlockModifier.GetIteratorInsertVarForLoop(sScriptLine);
                            if (!sAdditionalVariableLine.isEmpty()) {
                                aScriptLines.add(pBlockModifier.ToPaddedString(sAdditionalVariableLine, nBlockPadding));
                            }
                            String sRemoveScriptKey = pBlockModifier.GetRemoveScriptLineKey(sScriptLine);
                            if (!sRemoveScriptKey.isBlank()) {
                                //Logger.LogReport("IsRemoveScriptLine found -- (" + sFileNameJavascript + ") \"" + sScriptLine.trim() + "\"");
                                bRemoveScript = true;
                                if (!OutputLogger.aFilesSkip.contains(sFileNamePython)) {
                                    OutputLogger.aFilesSkip.add(sFileNamePython);
                                    if (!aRemovedFiles.contains(sFileNameJavascript)) {
                                        aRemovedFiles.add(sFileNameJavascript);
                                        LinkedList<String> l = !mRemovedFiles.containsKey(sRemoveScriptKey) ? new LinkedList<>() : mRemovedFiles.get(sRemoveScriptKey);
                                        l.add(sFileNamePython);
                                        mRemovedFiles.put(sRemoveScriptKey, l);
                                    }
                                }
                                break;
                            }
                            sScriptLine = pBlockModifier.ConvertCleanParenthesis(sScriptLine);
                            aScriptLines.add(sScriptLine);
                            if (sScriptLine.trim().indexOf("//") != 0) {
                                pBlockModifier = FunctionAddAction.pInstance;
                                String sAdditionalScriptLine = pBlockModifier.Convert(sScriptLine);
                                if (!sAdditionalScriptLine.isEmpty()) {
                                    int nPadding = StringUtil.GetLinePadding(sScriptLine);
                                    aScriptLines.add(pBlockModifier.ToPaddedString(sAdditionalScriptLine, nPadding));
                                }
                                String sLoopVariable = pBlockModifier.GetIteratorIncrementInsertForLoop(sScriptLine); //todo:: see if this is truly necessary by referencing the WhileLoop debug output
                                sClosingBracketAdditionalLine = !sLoopVariable.isEmpty() ? sLoopVariable : sClosingBracketAdditionalLine;
                                LogDebugInfo(sScriptLine, sFileNamePython);
                                if (!OutputLogger.aFilesSkip.contains(sFileNamePython) && !OutputLogger.aFilesSkip.contains(sFileNamePython.substring(0, sFileNamePython.indexOf(".")))) {
                                    pBlockModifier.LogMissedShitInfo(sScriptLine, sFileNameJavascript);
                                }
                            }
                        }
                        nLineNumber++;
                    }
                }
            }
            while (nBlockPadding >= 0) {
                aScriptLines.add(pBlockModifier.ToPaddedString("}", nBlockPadding));
                nBlockPadding -= 4;
            }
        }
        if (!aScriptLines.isEmpty() && !bRemoveScript) {
            mScriptLines.put(sFileNameJavascript, aScriptLines);
        }
    }


    public void LogDebugInfo(String sScriptLine, String sFileNamePython) {
        pDebug.LogDebugInfo(sScriptLine, sFileNamePython);
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

    public static void PrintRemovedFileOutput() {
        Logger.Println("");
        Logger.Println("Removed the following files: ");
        for (String sKey : mRemovedFiles.keySet()) {
            LinkedList<String> l = mRemovedFiles.get(sKey);
            Logger.Println("\"" + sKey + "\" - number of files removed: " + l.size());
            for (String s : l) {
                Logger.Println("\t- " + s);
            }
        }
        Logger.Println("");
    }
}
