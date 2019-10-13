package python.handle;

import util.StringUtil;

/**
 *
 * @author Sharky
 */
public abstract class AbstractHandler {

    public int GetLinePaddingByNumChar(String sScriptLine) {
        int nCharPad = StringUtil.CountStringPaddingChar(sScriptLine);
        int nTabPad = StringUtil.CountStringPaddingTab(sScriptLine);
        return nCharPad > nTabPad * 4 ? nCharPad : nTabPad * 4;
    }

    public boolean IsSkippedLine(String sScriptLine) {
        return sScriptLine.contains("sm.dispose") || sScriptLine.contains("sm.diposse") || sScriptLine.contains("sm.disose");
    }

    public boolean IsCloseArrayBracket(String sScriptLine) {
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

    public boolean IsOpenArrayBracket(String sScriptLine) {
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

    public boolean IsCorrectPaddingForClosingBracket(String sScriptLine, int nBlockPadding) {
        if (sScriptLine.contains("}") && GetLinePaddingByNumChar(sScriptLine) == nBlockPadding) {
            return false;
        }
        return nBlockPadding >= 0 && GetLinePaddingByNumChar(sScriptLine) <= nBlockPadding;
    }

    public boolean IsClosingBracketInsertNeeded(String sScriptLine) {
        return sScriptLine.contains("{") && (sScriptLine.contains("if") || sScriptLine.contains("else") || sScriptLine.contains("while") || sScriptLine.contains("for") || sScriptLine.contains("function"));
    }

    public boolean IsLineSplitForMergeWithNextLine(String sScriptLine) {
        char c = sScriptLine.trim().length() > 1 ? sScriptLine.trim().charAt(sScriptLine.trim().length() - 1) : '_',
                b = sScriptLine.trim().length() > 2 ? sScriptLine.trim().charAt(sScriptLine.trim().length() - 2) : '_',
                a = sScriptLine.trim().length() > 3 ? sScriptLine.trim().charAt(sScriptLine.trim().length() - 3) : '_';
        if (c == '+' || c == '"' || (c == ')' && (b == '(' || (b == ')' && a == '(')))) {
            return (sScriptLine.contains("sm.") && (sScriptLine.contains("(\"") || sScriptLine.contains(", \""))) || sScriptLine.contains("= \"");
        }
        return false;
    }

    public String ConvertComments(String sScriptLine, boolean bArray) {
        if (!sScriptLine.isEmpty() && sScriptLine.contains("#")) {
            boolean bOnlyComment = false;
            if (sScriptLine.trim().charAt(0) == '#') {
                bOnlyComment = true;
                if (GetLinePaddingByNumChar(sScriptLine) <= 2) {
                    sScriptLine = sScriptLine.trim();
                }
            }
            String sLine = "";
            String[] aLineSplitInterleaveQuotes = sScriptLine.contains("\"") ? sScriptLine.split("\"") : new String[]{};
            if (aLineSplitInterleaveQuotes.length > 0) {
                boolean bAddEndQuote = sScriptLine.charAt(sScriptLine.length() - 1) == '\"', bFoundComment = false;
                for (int i = 0; i < aLineSplitInterleaveQuotes.length; i++) {
                    String sLineSegment = aLineSplitInterleaveQuotes[i];
                    if (i % 2 == 0 && !bFoundComment) {
                        if (sLineSegment.contains("#")) {
                            String sLineNoComments = sLineSegment.substring(0, sLineSegment.indexOf("#"));
                            String sLineComments = sLineSegment.substring(sLineSegment.indexOf("#"));
                            sLineComments = sLineComments.replace("#", "//");
                            if (!sLineNoComments.trim().isEmpty()) {
                                sLineNoComments = StringUtil.TrimWhitespaceFromEnd(sLineNoComments);
                                sLineNoComments += !bOnlyComment && !bArray && !sLineNoComments.contains("{") && !sLineComments.contains(":") ? "; " : " ";
                            }
                            sLineSegment = sLineNoComments + sLineComments;
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
            } else {
                String sLineNoComments = sScriptLine.substring(0, sScriptLine.indexOf("#"));
                String sLineComments = sScriptLine.substring(sScriptLine.indexOf("#"));
                sLineComments = sLineComments.replace("#", "//");
                if (!sLineNoComments.trim().isEmpty()) {
                    sLineNoComments = StringUtil.TrimWhitespaceFromEnd(sLineNoComments);
                    sLineNoComments += !bOnlyComment && !bArray && !sLineNoComments.contains("{") && !sLineComments.contains(":") ? "; " : " ";
                }
                sLine = sLineNoComments + sLineComments;
            }
            sScriptLine = sLine;
        }
        return sScriptLine;
    }

    public String ConvertIfElseStatements(String sScriptLine) {
        String sComment = "";
        if (sScriptLine.contains(":")) {
            if (sScriptLine.contains("//")) {
                sComment = sScriptLine.substring(sScriptLine.indexOf("//"));
                sScriptLine = sScriptLine.substring(0, sScriptLine.indexOf("//"));
            }
            if (sScriptLine.contains("\"")) {
                boolean bFunction = sScriptLine.contains("(") && sScriptLine.contains(")");
                boolean bForEachLoop = sScriptLine.contains(" in ");
                String[] aScriptLine = sScriptLine.split("\"");
                sScriptLine = "";
                for (int i = 0; i < aScriptLine.length; i++) {
                    if (i == 0 || i % 2 == 0) {
                        if (aScriptLine[i].contains("while ") && !aScriptLine[i].contains("while (")) {
                            aScriptLine[i] = aScriptLine[i].replace("while ", "while (");
                        } else if (aScriptLine[i].contains("for ") && bForEachLoop) {
                            aScriptLine[i] = aScriptLine[i].replace("for ", "for each (");
                        } else if (aScriptLine[i].contains("def ") && bFunction) {
                            aScriptLine[i] = aScriptLine[i].replace("def ", "function ");
                        } else if (aScriptLine[i].contains("elif")) {
                            aScriptLine[i] = aScriptLine[i].replace("elif", "} else if (");
                        } else if (aScriptLine[i].contains("if ")) {
                            aScriptLine[i] = aScriptLine[i].replace("if ", "if (");
                        } else if (aScriptLine[i].contains("else:")) {
                            aScriptLine[i] = aScriptLine[i].replace("else:", "} else:");
                        }
                        if (aScriptLine[i].contains(":")) {
                            aScriptLine[i] = aScriptLine[i].replace(":", sScriptLine.contains("else:") || sScriptLine.contains("function") ? " {" : ") {");
                        }
                    }
                    sScriptLine += aScriptLine[i];
                    if (i != aScriptLine.length - 1) {
                        sScriptLine += "\"";
                    }
                }
            } else {
                if (sScriptLine.contains("while ") && !sScriptLine.contains("while (")) {
                    sScriptLine = sScriptLine.replace("while ", "while (");
                } else if (sScriptLine.contains("for ") && sScriptLine.contains(" in ")) {
                    sScriptLine = sScriptLine.replace("for ", "for each (");
                } else if (sScriptLine.contains("def ") && sScriptLine.contains("(") && sScriptLine.contains(")")) {
                    sScriptLine = sScriptLine.replace("def ", "function ");
                } else if (sScriptLine.contains("elif")) {
                    sScriptLine = sScriptLine.replace("elif", "} else if (");
                } else if (sScriptLine.contains("if ") && sScriptLine.contains(":")) {
                    sScriptLine = sScriptLine.replace("if ", "if (");
                } else if (sScriptLine.contains("else:")) {
                    sScriptLine = sScriptLine.replace("else:", "} else:");
                }
                sScriptLine = sScriptLine.replace(":", sScriptLine.contains("else:") || sScriptLine.contains("function") ? " {" : ") {");
            }
        }
        if (sScriptLine.contains(" ( ")) {
            sScriptLine = sScriptLine.replace(" ( ", " (");
        }
        sScriptLine += sComment;
        return sScriptLine;
    }

    public String ConvertSemicolon(String sScriptLine, boolean bArray) {
        if (!sScriptLine.isEmpty() && !bArray) {
            if (!sScriptLine.contains("//") && sScriptLine.trim().charAt(sScriptLine.trim().length() - 1) != '{') {
                sScriptLine = StringUtil.TrimWhitespaceFromEnd(sScriptLine);
                sScriptLine += ";";
            }
        }
        return sScriptLine;
    }

    public String ConvertMergeWithNextLine(String sScriptLine, String sAppend) {
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

    public String ConvertAskMenu(String sScriptLine) {
        if (sScriptLine.contains("Say(") && sScriptLine.contains("#L")) {
            sScriptLine = sScriptLine.replace("Say(", "AskMenu(");
            if (sScriptLine.contains(", true)")) {
                sScriptLine = sScriptLine.replace(", true)", ")");
            }
        }
        return sScriptLine;
    }

    public String ConvertNestedAskYesNo(String sScriptLine) {
        if (sScriptLine.contains("if (self.AskYesNo") && sScriptLine.contains(")) {")) {
            sScriptLine = sScriptLine.replace(")) {", ") == 1) {");
        }
        return sScriptLine;
    }

    public String GetIteratorInsertVarForLoop(String sScriptLine) {
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

    public String GetIteratorIncrementInsertForLoop(String sScriptLine) {
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

    public String ToPaddedString(String sText, int nPadding) {
        StringBuilder pBuilder = new StringBuilder();
        for (int i = 0; i < nPadding; i++) {
            pBuilder.append(" ");
        }
        pBuilder.append(sText);
        return pBuilder.toString();
    }

    public abstract String Convert(String sScriptLine);

}
