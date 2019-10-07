/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package python;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static python.DebugInfo.*;
import static python.ForceComment.*;
import static python.FunctionAppend.*;
import static python.FunctionInsert.*;
import static python.FunctionKeyword.*;
import static python.FunctionKeywordPython.*;
import static python.StringModifier.*;

/**
 *
 * @author Sharky - Really more of "swordie-python" to JS
 */
public class PythonToJavascript {

    /** User/Directory Variables -- Modify to set project configuration **/

    public static String sPythonDirectory = "C:\\Users\\Chris\\Desktop\\Swordie\\scripts_new";//directory for your python scripts to convert to javascript formatting

    public static String sJavascriptDirectory = "C:\\Users\\Chris\\Desktop\\Swordie\\scripts_javascript\\";//directory for your newly created javascript files to populate (this also is used for the PurgeDirectory-project path setting)

    //output configuration settings
    public static boolean bAlienFunctionView = true,
                          bAlienFunctionFileView = true,
                          bSyntaxErrorView = false,
                          bAlienImportView = true,
                          bSkippedScriptView = true;

    /** End User/Directory Variables **/

    public static final Map<String, LinkedList<String>> mScriptLines = Collections.synchronizedMap(new HashMap<>()); //stores copies of files and their lines

    public static void main(String[] args) {
        try {
            Files.walk(Paths.get(sPythonDirectory)).forEach((pFile) -> {
                if (!pFile.toFile().isDirectory()) {
                    try {
                        ConvertPythonToJavascript(pFile.toFile());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    util.Logger.LogReport("Processing directory [" + pFile.getFileName() + "]....");
                }
            });
            PrintDebugInfoOutput();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void ConvertPythonToJavascript(File pFile) throws IOException {
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
        try (BufferedReader pReader = new BufferedReader(new FileReader(pFile))) {
            int nBlockPadding = -1, nLineNumber = 1, nArrayIndex = -1;
            String sClosingBracketAdditionalLine = "";
            while (pReader.ready()) {
                String sScriptLine = pReader.readLine();
                if (!sScriptLine.trim().isEmpty()) {
                    if (!IsSkippedLine(sScriptLine)) {
                        sScriptLine = ConvertForceComment(sScriptLine);
                        if (sScriptLine.trim().indexOf("//") != 0) {
                            if (IsOpenArrayBracket(sScriptLine)) {
                                nArrayIndex++;
                            } else if (IsCloseArrayBracket(sScriptLine)) {
                                nArrayIndex--;
                            }
                            sScriptLine = ConvertComments(sScriptLine, nArrayIndex >= 0);
                            sScriptLine = ConvertIfElseStatements(sScriptLine);
                            if (IsCorrectPaddingForClosingBracket(sScriptLine, nBlockPadding)) {
                                if (nBlockPadding > 0) {
                                    while (nBlockPadding >= GetLinePaddingByNumChar(sScriptLine, false) && nBlockPadding > 0) {
                                        if (!sClosingBracketAdditionalLine.isEmpty()) {
                                            aScriptLines.add(ToPaddedString(sClosingBracketAdditionalLine, nBlockPadding + 4));
                                            sClosingBracketAdditionalLine = "";
                                        }
                                        aScriptLines.add(ToPaddedString("}", nBlockPadding));
                                        nBlockPadding -= 4;
                                    }
                                    nBlockPadding = -1;
                                } else {
                                    if (nBlockPadding == GetLinePaddingByNumChar(sScriptLine, false)) {
                                        if (!sClosingBracketAdditionalLine.isEmpty()) {
                                            aScriptLines.add(ToPaddedString(sClosingBracketAdditionalLine, nBlockPadding + 4));
                                            sClosingBracketAdditionalLine = "";
                                        }
                                        aScriptLines.add(ToPaddedString("}", nBlockPadding));
                                        nBlockPadding = -1;
                                    }
                                }
                            }
                            if (IsClosingBracketInsertNeeded(sScriptLine)) {
                                nBlockPadding = GetLinePaddingByNumChar(sScriptLine, false);
                            }
                            while (IsLineSplitForMergeWithNextLine(sScriptLine) && !sScriptLine.contains("\")") && pReader.ready()) {
                                String sAppend = pReader.readLine().trim();
                                sAppend = ConvertComments(sAppend, nArrayIndex >= 0);
                                if (sAppend.length() > 1 && sAppend.contains(("\""))) {
                                    sScriptLine = ConvertMergeWithNextLine(sScriptLine, sAppend); //todo:: verify this holds up
                                } else {
                                    break;
                                }
                            }
                            sScriptLine = ConvertFunctionAppend(sScriptLine);
                        }
                        sScriptLine = ConvertFunctionKeyword(sScriptLine, sFileNamePython);
                        sScriptLine = ConvertSemicolon(sScriptLine, nArrayIndex >= 0);
                        sScriptLine = ConvertPythonKeyword(sScriptLine, sFileNamePython, nLineNumber);
                        if (!sScriptLine.isEmpty()) {
                            String sAdditionalVariableLine = GetIteratorInsertVarForLoop(sScriptLine);
                            if (!sAdditionalVariableLine.isEmpty()) {
                                aScriptLines.add(ToPaddedString(sAdditionalVariableLine, nBlockPadding));
                            }
                            aScriptLines.add(sScriptLine);
                            if (sScriptLine.trim().indexOf("//") != 0) {
                                String sAdditionalScriptLine = GetFunctionFollowInsert(sScriptLine);
                                if (!sAdditionalScriptLine.isEmpty()) {
                                    int nPadding = GetLinePaddingByNumChar(sScriptLine, false);
                                    aScriptLines.add(ToPaddedString(sAdditionalScriptLine, nPadding));
                                }
                                String sLoopVariable = GetIteratorIncrementInsertForLoop(sScriptLine);
                                sClosingBracketAdditionalLine = !sLoopVariable.isEmpty() ? sLoopVariable : sClosingBracketAdditionalLine;
                                LogDebugInfo(sScriptLine, sFileNamePython);
                            }
                        }
                        nLineNumber++;
                    }
                }
            }
            while (nBlockPadding >= 0) {
                aScriptLines.add(ToPaddedString("}", nBlockPadding));
                nBlockPadding -= 4;
            }
        }
        if (!aScriptLines.isEmpty()) {
            mScriptLines.put(sFileNameJavascript, aScriptLines);
        }
    }


    static {

        InitPythonKeywordReplaceMap();
        InitFunctionKeywordReplaceMap();
        InitFunctionArgumentAppendMap();
        InitFunctionFollowInsertMap();
        InitForceCommentLineList();

    }
}
