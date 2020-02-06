package hex;

import util.Logger;
import util.StringUtil;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This file is a part of the project auto-script, and is the intellectual property of its developer(s)
 * File created on JULY 26, 2019
 *
 * @author Sharky
 *
 * This program is designed to search a structure from IDA, and find the sub_????? function for a given header value.
 *
 */
public class FunctionFinder {

    /** User-modify variable arguments **/
    public static int nTargetHeaderValue = 1867; //the # of the header you are trying to locate
    public static String sTargetVarName = "a2"; //the name of the variable that is your "header" const in your IDA function
    public static String sFile = "C:\\Users\\Chris\\Desktop\\auto-script\\hex\\FunctionFinder.txt"; //location of this project on your cpu
    /** End user-modify variable arguments **/

    /** Do-not-modify **/
    public static final ScriptEngineManager pScriptEngine = new ScriptEngineManager();
    public static final ScriptEngine pEngineEval = pScriptEngine.getEngineByName("JavaScript");

    public static void main(String[] args) {
        String sFunctionName = HexDecompile(null);
        if (sFunctionName.contains("LABEL")) {
            String sLabel = sFunctionName.substring(sFunctionName.indexOf("LABEL"), sFunctionName.indexOf(";"));
            Logger.LogReport("goto-LABEL instance found, new label being created.");
            Logger.LogReport("\tsLabel:  -%s", sLabel);
            Logger.LogReport("\tCurrent sFunctionName:  -%s", sFunctionName);
            sFunctionName = HexDecompile(sLabel);
            Logger.LogReport("\tsFunctionName after:  -%s", sFunctionName);
        }
        Logger.Println("Result found at function:  -%s", sFunctionName);
        Logger.Println("Program terminating successfully.");
    }

    public static String HexDecompile(String sLabel) {
        int nLines = 0, nPaddingToFind = -1, nPaddingToEnd = -1;
        boolean bCaseBlock = false, bIfElseBlock = false, bLabel = sLabel != null;
        String sLine = "", sPreviousLine = "";
        try (BufferedReader pReader = new BufferedReader(new FileReader(new File(sFile)))) {
            while (pReader.ready()) {
                if (sLine.contains(";")) {
                    sPreviousLine = sLine + " //Line-Number:  " + nLines;
                }
                sLine = pReader.readLine();
                nLines++;
                int nPadding = StringUtil.GetLinePadding(sLine);
                if (!bLabel && sLabel != null) {
                    nPaddingToEnd = nPadding;
                }
                if (nPadding < nPaddingToEnd) {
                    Logger.LogError("\r\nProgram terminating abnormally - nPadding < nPaddingToEnd...");
                    Logger.LogError("\t-sLabel:  %s", sLabel);
                    return sPreviousLine.trim();
                }
                if (sLine.contains(sTargetVarName)) {
                    sLine = sLine.replace(sTargetVarName, (""+nTargetHeaderValue));
                }
                if (!bLabel) {
                    //todo:: may have to add support here for ignoring label text
                    if (sLine.contains("}")) { //conditional brackets
                        if (bIfElseBlock) {
                            if (pReader.ready()) {
                                String sNextLine = pReader.readLine();
                                if (sNextLine.contains("else") && !sNextLine.contains("if")) {
                                    if (nPaddingToFind == nPadding) {
                                        bIfElseBlock = false;
                                    }
                                }
                            }
                        }
                        else if (nPaddingToFind >= 0 && nPaddingToFind == nPadding) {
                            //Logger.LogAdmin("Found switch-case-block conclusion for header (%d)", nTargetHeaderValue);
                            //Logger.LogAdmin("\tfunction:  -%s", sPreviousLine.trim());
                            return sPreviousLine.trim();
                        }
                    }
                    if (bIfElseBlock) {
                        //todo:: maybe re-think this logic
                        continue;
                    }
                    if (sLine.contains("::") || sLine.contains("*")) { //function name
                        continue;
                    }
                    if (sLine.contains("{")) { //conditional block openings
                        continue;
                    }
                    if (sLine.contains("LABEL") && nPadding == 0) { //label definitions
                        continue;
                    }
                    if (sLine.contains("switch")) { //switch definitions
                        //todo:: see if there are any instances where you have to check for an expression inside the switch
                        continue;
                    }
                    if (!bCaseBlock) {
                        if (sLine.contains("case ")) {
                            String sLinePadded = sLine;
                            if (nPadding == 0) {
                                sLinePadded = " . " + sLine;
                            }
                            String s = sLinePadded.replace("case ", "@@@").replace(":", "@@@");
                            String[] aLine = s.split("@@@");
                            String sValue = aLine[1].substring(0, aLine[1].length() - 1);
                            int nValue = Integer.parseInt(sValue);
                            if (nValue == nTargetHeaderValue) {
                                bCaseBlock = true;
                                continue;
                            }
                        } else if (sLine.contains("default:")) {
                            bCaseBlock = true;
                            continue;
                        }
                    } else if (bCaseBlock && sLine.contains("break;")) {
                        //Logger.LogAdmin("Found switch-case-block conclusion for header (%d)", nTargetHeaderValue);
                        //Logger.LogAdmin("\tfunction:  -%s", sPreviousLine.trim());
                        return sPreviousLine.trim();
                    }
                    if (sLine.contains("if (") && sLine.contains(sTargetVarName)) {
                        //have the script engine evaluate this and return true or false
                        String sLineEval = sLine.replace("(", "@@@").replace(")", "@@@").split("@@@")[1];
                        //Logger.LogAdmin("sLineEval created - %s", sLineEval);
                        try {
                            boolean bEval = (Boolean) pEngineEval.eval(sLineEval);
                            if (bEval) {
                                nPaddingToFind = nPadding;
                                //Logger.LogReport("[evaluation-report] bEval returning `true` for the expression - %s", sLineEval);
                            } else if (nPaddingToFind >= 0) {
                                bIfElseBlock = true;
                            }
                        } catch (ScriptException e) {
                            Logger.LogError("Script Exception thrown when trying to parse eval-line: %s", sLineEval);
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    if (sLine.contains(sLabel) && nPadding == 0) {
                        bLabel = false;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.LogError("-Program terminating abnormally from an IOException...");
            ex.printStackTrace();
        }
        return sPreviousLine.trim();
    }
}
