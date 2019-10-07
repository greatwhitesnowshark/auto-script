package python;

import util.StringUtil;

/**
 *
 * @author Sharky
 */
public class StringModifier {

    public static final int GetLinePaddingByNumChar(String sScriptLine, boolean bSet) {
        int nCharPad = StringUtil.CountStringPaddingChar(sScriptLine);
        int nTabPad = StringUtil.CountStringPaddingTab(sScriptLine);
        return nCharPad > nTabPad * 4 ? nCharPad : nTabPad * 4;
    }

    public static final boolean IsSkippedLine(String sScriptLine) {
        return sScriptLine.contains("sm.dispose") || sScriptLine.contains("sm.diposse") || sScriptLine.contains("sm.disose");
    }

    public static final boolean IsCloseArrayBracket(String sScriptLine) {
        String sTrim = sScriptLine.contains("#") ? sScriptLine.substring(0, sScriptLine.indexOf("#")).trim() : sScriptLine.trim();
        if (!sTrim.isEmpty() && sTrim.length() > 0 && !sTrim.contains("[")) {
            char cLastChar = sTrim.charAt(sTrim.length() - 1);
            if (cLastChar == ']') {
                return true;
            }
            return (sTrim.length() == 2 && sTrim.equals("],")) || (sTrim.length() == 1 && sTrim.equals("]"));
        }
        return false;
    }

    public static final boolean IsOpenArrayBracket(String sScriptLine) {
        String sTrim = sScriptLine.contains("#") ? sScriptLine.substring(0, sScriptLine.indexOf("#")).trim() : sScriptLine.trim();
        if (!sTrim.isEmpty() && sTrim.length() > 0 && !sTrim.contains("]")) {
            char cLastChar = sTrim.charAt(sTrim.length() - 1);
            if (cLastChar == '[') {
                return true;
            }
            return sTrim.length() > 3 && sTrim.substring(sTrim.length() - 3).equals("= [");
        }
        return false;
    }

    public static final boolean IsCorrectPaddingForClosingBracket(String sScriptLine, int nBlockPadding) {
        if (sScriptLine.contains("}") && GetLinePaddingByNumChar(sScriptLine, false) == nBlockPadding) {
            return false;
        }
        return nBlockPadding >= 0 && GetLinePaddingByNumChar(sScriptLine, false) <= nBlockPadding;
    }

    public static final boolean IsClosingBracketInsertNeeded(String sScriptLine) {
        return sScriptLine.contains("{") && (sScriptLine.contains("if") || sScriptLine.contains("else") || sScriptLine.contains("while"));
    }

    public static final boolean IsLineSplitForMergeWithNextLine(String sScriptLine) {
        char c = sScriptLine.trim().length() > 1 ? sScriptLine.trim().charAt(sScriptLine.trim().length() - 1) : '_',
                b = sScriptLine.trim().length() > 2 ? sScriptLine.trim().charAt(sScriptLine.trim().length() - 2) : '_',
                a = sScriptLine.trim().length() > 3 ? sScriptLine.trim().charAt(sScriptLine.trim().length() - 3) : '_';
        if (c == '+' || c == '"' || (c == ')' && (b == '(' || (b == ')' && a == '(')))) {
            return (sScriptLine.contains("sm.") && (sScriptLine.contains("(\"") || sScriptLine.contains(", \""))) || sScriptLine.contains("= \"");
        }
        return false;
    }

    public static final String ConvertComments(String sScriptLine, boolean bArray) {
        boolean bFoundComment = false;
        if (!sScriptLine.isEmpty() && sScriptLine.contains("#")) {
            if (GetLinePaddingByNumChar(sScriptLine, false) < 4) {
                sScriptLine = sScriptLine.trim();
            }
            boolean bAddEndQuote = sScriptLine.charAt(sScriptLine.length() - 1) == '\"';
            String sLine = "";
            String[] aLineSplitInterleaveQuotes = sScriptLine.contains("\"") ? sScriptLine.split("\"") : new String[] {};
            if (aLineSplitInterleaveQuotes.length > 0) {
                for (int i = 0; i < aLineSplitInterleaveQuotes.length; i++) {
                    String sLineSegment = aLineSplitInterleaveQuotes[i];
                    String sSplit, sSplitEnd;
                    if (i % 2 == 0) {
                        if (sLineSegment.contains("#") && !bFoundComment) {
                            sSplit = StringUtil.TrimWhitespaceFromEnd(sLineSegment.substring(0, sLineSegment.indexOf("#")));
                            sSplitEnd = sLineSegment.substring(sLineSegment.indexOf("#")).trim();
                            sSplitEnd = sSplitEnd.replace("#", (!sSplit.isEmpty() && !bArray && !sSplit.contains("{") && !sSplit.contains(":")) ? "; // " : " // "); //todo:: watch this
                            sLineSegment = sSplit + sSplitEnd;
                            bFoundComment = true;
                        }
                    }
                    sLine += sLineSegment;
                    if (i != aLineSplitInterleaveQuotes.length - 1) {
                        sLine += "\"";
                    }
                }
                if (bAddEndQuote) {
                    sLine += "\"";
                }
                sScriptLine = sLine;
            } else {
                String sSplit = sScriptLine.substring(0, sScriptLine.indexOf("#"));
                sSplit = StringUtil.TrimWhitespaceFromEnd(sSplit);
                String sSplitEnd = sScriptLine.substring(sScriptLine.indexOf("#"));
                sSplitEnd = sSplitEnd.trim().replace("#", (!sSplit.isEmpty() && !bArray && !sSplit.contains("{") && !sSplit.contains(":")) ? "; // " : " // "); //todo:: watch this
                sScriptLine = sSplit + sSplitEnd;
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

    public static final String ConvertSemicolon(String sScriptLine, boolean bArray) {
        if (!sScriptLine.isEmpty() && !bArray) {
            if (!sScriptLine.contains("//") && sScriptLine.trim().charAt(sScriptLine.trim().length() - 1) != '{') {
                sScriptLine = StringUtil.TrimWhitespaceFromEnd(sScriptLine);
                sScriptLine += ";";
            }
            /*String sLineComments = sScriptLine.contains("//") ? sScriptLine.substring(sScriptLine.indexOf("//")) : "";
            String sLineTrimmedComments = sScriptLine.contains("//") ? sScriptLine.substring(0, sScriptLine.indexOf("//")) : sScriptLine;
            if (!sLineTrimmedComments.trim().isEmpty() && sLineTrimmedComments.trim().length() > 1) {
                char cLastChar = sLineTrimmedComments.trim().charAt(sLineTrimmedComments.trim().length() - 1);
                List<Character> aLastChar = Arrays.asList('{', '(', ']', '[', ',');
                if (!aLastChar.contains(cLastChar)) {
                    sLineTrimmedComments += ";";
                    sScriptLine = sLineTrimmedComments;
                    if (!sLineComments.isEmpty()) {
                        sScriptLine += sLineComments;
                    }
                }
            }*/
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

    public static final String GetIteratorInsertVarForLoop(String sScriptLine) {
        String sAdditionalLine = "";
        if (sScriptLine.contains("while (") && sScriptLine.contains(") {") && (sScriptLine.contains("<") || sScriptLine.contains(">"))) {
            String[] aSplit = sScriptLine.substring(sScriptLine.indexOf("while ("), sScriptLine.indexOf(") {")).split("while")[1].substring(2).split(" ");
            String sVariable = "", sOperation = "", sValueTo = "";
            if (aSplit.length == 3) {
                for (int i = 0; i < aSplit.length; i++) {
                    if (i == 0) {
                        sVariable = aSplit[i];
                    } else if (i == 1) {
                        sOperation = aSplit[i];
                    } else if (i == 2) {
                        sValueTo = aSplit[i];
                    }
                }
                if (!sVariable.isEmpty() && !sOperation.isEmpty() && !sValueTo.isEmpty()) {
                    sAdditionalLine = sVariable + " = 0;";
                }
            }
        }
        return sAdditionalLine;
    }

    public static final String GetIteratorIncrementInsertForLoop(String sScriptLine) {
        String sAdditionalLine = "";
        /*if (sScriptLine.contains("while (") && sScriptLine.contains(") {") && (sScriptLine.contains("<") || sScriptLine.contains(">"))) {
            String[] aSplit = sScriptLine.substring(sScriptLine.indexOf("while ("), sScriptLine.indexOf(") {")).split("while")[1].substring(2).split(" ");
            String sVariable = "", sOperation = "", sValueTo = "";
            if (aSplit.length == 3) {
                for (int i = 0; i < aSplit.length; i++) {
                    if (i == 0) {
                        sVariable = aSplit[i];
                    } else if (i == 1) {
                        sOperation = aSplit[i];
                    } else if (i == 2) {
                        sValueTo = aSplit[i];
                    }
                }
                if (!sVariable.isEmpty() && !sOperation.isEmpty() && !sValueTo.isEmpty()) {
                    sAdditionalLine += sOperation.contains(">") ? (sVariable + "--;") : sOperation.contains("<") ? (sVariable + "++;") : "";
                }
            }
        }*/
        return sAdditionalLine;
    }

    public static final String ToPaddedString(String sText, int nPadding) {
        StringBuilder pBuilder = new StringBuilder();
        for (int i = 0; i < nPadding; i++) {
            pBuilder.append(" ");
        }
        pBuilder.append(sText);
        return pBuilder.toString();
    }

}
