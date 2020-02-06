package python.handle;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import static python.output.OutputLogger.aFilesSkip;

/**
 *
 * @author Sharky
 */
public class SyntaxModifier {

    public static Map<String, String> mFunctionPythonKeywordReplace = new LinkedHashMap<>(); //all python keyword-to-text replacements
    public static Map<String, LinkedList<String[]>> mPythonKeywordToFileLine = new LinkedHashMap<>(); //records file and line information about unknown syntax

    public static String ConvertPythonSingleKeyword(String sKeyword, String sLineSegment, String sScriptLine) {
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
                    if (mFunctionPythonKeywordReplace.get(sKeyword).equals(sKeyword)) {
                        break;
                    }
                    sLineSegment = sLineSegment.replace(sKeyword, mFunctionPythonKeywordReplace.get(sKeyword));
                }
                break;
        }
        if (!sKeywordSetDebug.isEmpty() && sKeyword.equals(sKeywordSetDebug) && !sLineCompare.equals(sLineSegment)) {
            util.Logger.LogReport("[keyword '" + sKeyword + "' debug]");
            util.Logger.LogReport("\tsLineCompare: " + sLineCompare);
            util.Logger.LogReport("\tsLineSegment: " + sLineSegment + "\r\n");
        }
        return sLineSegment;
    }

    public static String ConvertPythonKeywordLogInfo(String sLineSegment, String sScriptLine, String sFileName, int nLineNumber) {
        for (String sKeyword : mFunctionPythonKeywordReplace.keySet()) {
            if ((sLineSegment.contains(sKeyword) || (sKeyword.contains("(") && sLineSegment.contains(" " + sKeyword))) && !(sLineSegment.contains("(\"") || sLineSegment.contains("\")"))) {
                int nIdxKeyword = sLineSegment.indexOf(sKeyword);
                if (nIdxKeyword > 0 && (sLineSegment.charAt(nIdxKeyword-1) != ' ' && sLineSegment.charAt(nIdxKeyword-1) != '(' && sLineSegment.charAt(nIdxKeyword-1) != '\t')) {
                    String sLineSegmentSuffix = sLineSegment.substring(nIdxKeyword + sKeyword.length());
                    if (sLineSegment.length() > nIdxKeyword + sKeyword.length() && (sLineSegmentSuffix.contains(" " + sKeyword) || sLineSegmentSuffix.contains("(" + sKeyword))) {
                        util.Logger.LogError("[MANUAL-FIX SYNTAX OCCURRENCE] [%s]  %s", sKeyword, sScriptLine);
                        util.Logger.LogError("at File:  %s", sFileName);
                    }
                    continue;
                }
                sLineSegment = ConvertPythonSingleKeyword(sKeyword, sLineSegment, sScriptLine);
                if (sLineSegment.contains(sKeyword)) {
                    if (!mPythonKeywordToFileLine.containsKey(sKeyword)) {
                        mPythonKeywordToFileLine.put(sKeyword, new LinkedList<>());
                    }
                    LinkedList<String[]> lFileInfo = mPythonKeywordToFileLine.get(sKeyword);
                    if (!lFileInfo.contains(new String[]{sFileName, sScriptLine + " [at line " + nLineNumber + "]"})) {
                        lFileInfo.add(new String[]{sFileName, sScriptLine + " [at line " + nLineNumber + "]"});
                    }
                    mPythonKeywordToFileLine.put(sKeyword, lFileInfo);
                    if (!aFilesSkip.contains(sFileName.substring(0, sFileName.indexOf(".")))) {
                        aFilesSkip.add(sFileName.substring(0, sFileName.indexOf(".")));
                    }
                }
            }
        }
        return sLineSegment;
    }

    public static String ConvertPythonKeyword(String sScriptLine, String sFileName, int nLineNumber) {
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
        return sLine;
    }


    static {

        mFunctionPythonKeywordReplace.put("None", "");//not done
        mFunctionPythonKeywordReplace.put("and", "&&");
        mFunctionPythonKeywordReplace.put(" or ", " || ");
        mFunctionPythonKeywordReplace.put("assert", "assert");//not done
        mFunctionPythonKeywordReplace.put("class", "class");//not done
        mFunctionPythonKeywordReplace.put("def", "");
        mFunctionPythonKeywordReplace.put("del", "del");//not done
        mFunctionPythonKeywordReplace.put("except", "except");//not done
        mFunctionPythonKeywordReplace.put("finally", "finally");//not done
        //mFunctionPythonKeywordReplace.put("from", "from");//not done
        mFunctionPythonKeywordReplace.put("global", "global");//not done
        mFunctionPythonKeywordReplace.put("import", "import");//not done
        mFunctionPythonKeywordReplace.put("lambda", "lambda");//not done //add to skipped
        mFunctionPythonKeywordReplace.put("len(", "len(");//not done
        mFunctionPythonKeywordReplace.put("list(", "list(");//not done //add to skipped
        mFunctionPythonKeywordReplace.put("map(", "map(");//not done //add to skipped
        mFunctionPythonKeywordReplace.put("nonlocal", "nonlocal");//not done
        mFunctionPythonKeywordReplace.put("not", "");
        mFunctionPythonKeywordReplace.put("pass", "pass");//not done
        mFunctionPythonKeywordReplace.put("raise", "raise");//not done
        mFunctionPythonKeywordReplace.put("try", "try");//not done
        mFunctionPythonKeywordReplace.put("with", "with");//not done
        mFunctionPythonKeywordReplace.put("yield", "yield");//not done

    }
}
