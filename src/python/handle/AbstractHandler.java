package python.handle;

import util.StringMatch;
import util.StringUtil;

import java.util.LinkedHashMap;
import java.util.function.Predicate;

/**
 *
 * @author Sharky
 */
public abstract class AbstractHandler {

    public boolean IsSkippedLine(String sScriptLine) {
        String[] aSkippedContext = new String[] {
                "sm.dispose",
                "sm.diposse",
                "sm.disose",
                "self.QuestRecordExSet(18418,"
        };
        StringMatch pMatch = new StringMatch(sScriptLine, "", "", true, true, true);
        for (String s : aSkippedContext) {
            pMatch.SetFindText(s);
            if (pMatch.GetMatches() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean IsCloseArrayBracket(String sScriptLine, boolean bAssociative) {
        StringMatch pMatch = new StringMatch(sScriptLine, "", "", true, true, true);
        return pMatch.EndsWith("]") || (pMatch.EndsWith("}") && bAssociative);
    }

    public boolean IsOpenArrayBracket(String sScriptLine) {
        StringMatch pMatch = new StringMatch(sScriptLine, "", "", true, true, true);
        return pMatch.EndsWith("= [") || pMatch.EndsWith("= {");
    }

    public boolean IsCorrectPaddingForClosingBracket(String sScriptLine, int nBlockPadding) {
        if (sScriptLine.contains("}") && StringUtil.GetLinePadding(sScriptLine) == nBlockPadding) {
            return false;
        }
        return nBlockPadding >= 0 && StringUtil.GetLinePadding(sScriptLine) <= nBlockPadding;
    }

    public boolean IsClosingBracketInsertNeeded(String sScriptLine) {
        StringMatch pMatch = new StringMatch(sScriptLine, "{", "", true, true, true, (p) -> {
            String[] a = new String[] {
                    "if",
                    "else",
                    "while",
                    "for",
                    "function"
            };
            for (String s : a) {
                if (StringMatch.Match(p.GetString(), true, true, true, s)) {
                    return true;
                }
            }
            return false;
        });
        return pMatch.GetMatches() > 0;
    }

    public String GetRemoveScriptLineKey(String sScriptLine) {
        String[] aRemoveScript = new String[] {
                "self.setBossCooldown",
                "BossConstants",
                "BossCooldown",
                "chr.getAvatarData",
                "lambda",
                "list",
                "import",
                "map",
                "swordie",
                "Swordie",
                "sjonnie",
                "Sjonnie",
                "asura",
                "Asura"
        };
        for (String s : aRemoveScript) {
            StringMatch pMatch = new StringMatch(sScriptLine, s, "", false, !s.equals("map") && !s.equals("list"), true);
            if (pMatch.GetMatches() > 0) {
                return s;
            }
        }
        return "";
    }

    public String ConvertComments(String sScriptLine, final boolean bArray) {
        if (!sScriptLine.isEmpty() && sScriptLine.contains("#")) {
            if (sScriptLine.trim().charAt(0) == '#') {
                if (StringUtil.GetLinePadding(sScriptLine) <= 2) {
                    sScriptLine = sScriptLine.trim();
                }
            }
            StringMatch pMatch = new StringMatch(sScriptLine, "#", "//", false, true, false);
            sScriptLine = pMatch.ReplaceAll();
        }
        return sScriptLine;
    }

    public String ConvertCleanComments(String sScriptLine) { //todo:: this cleaning is only because one of my methods fucks up
        char[] aLineChars = sScriptLine.trim().toCharArray();
        if (aLineChars.length >= 2) {
            if (aLineChars[0] == '/' && aLineChars[1] == '/' && !sScriptLine.contains(".")) {
                if (aLineChars[aLineChars.length - 2] == ')' && aLineChars[aLineChars.length - 1] == ';') {
                    sScriptLine = sScriptLine.substring(0, sScriptLine.indexOf(");"));
                    //Logger.Println(sLine + "  -(" + pFile.getFileName().toString() + ")");
                }
            }
        }
        return sScriptLine;
    }

    public String ConvertForEachLoop(String sScriptLine) {
        if (sScriptLine != null && sScriptLine.contains("range") && sScriptLine.contains("for") && sScriptLine.contains("each") && sScriptLine.contains("in")) {
            String s = sScriptLine, sArray, sVariable;
            if (s.contains("range (")) {
                s.replace("range (", "range(");
            }
            sArray = s.split("range")[1];
            sArray = sArray.substring(sArray.indexOf("(") + 1, sArray.lastIndexOf(")"));
            String[] a = s.split("in")[0].split(" ");
            sVariable = a[a.length - 1].trim();
            if (sVariable.contains("(")) {
                sVariable = sVariable.substring(sVariable.indexOf("(") + 1);
            }
            s = s.replaceAll("(range\\(?.*)\\)", sArray);
            s = s.replace("each", "");
            s = s.replace(" in ", " = 0; " + sVariable + " < "); //todo:: need to parse the variable here
            s = new StringMatch(s, ")", "; " + sVariable + "++)", false, false, true).ReplaceLast();
            sScriptLine = s;
        }
        return sScriptLine;
    }

    public String ConvertCleanParenthesis(String sScriptLine) {
        StringMatch pMatch = new StringMatch(sScriptLine, "( ", "(", false, true, true);
        if (pMatch.GetMatches() > 0) {
            sScriptLine = pMatch.ReplaceAll();
        }
        return sScriptLine;
    }

    public String ConvertRemoveComments(String sScriptLine) {
        if (StringMatch.Match(sScriptLine, "//")) {
            sScriptLine = sScriptLine.substring(0, sScriptLine.indexOf("//")).stripTrailing();
        }
        return sScriptLine;
    }

    public String ConvertIfElseStatements(String sScriptLine) {
        String sContext = sScriptLine;

        if (StringMatch.Match(sScriptLine, false, true, true, ":")) {
            StringMatch pMatch = new StringMatch(sScriptLine, "", "", false, true, true);

            pMatch.Reset(sScriptLine, "while", "while (", (p) -> !StringMatch.Match(p.GetString(), "while ("));
            sScriptLine = pMatch.GetMatches() > 0 ? pMatch.ReplaceAll() : sScriptLine;

            pMatch.Reset(sScriptLine,"for", "for each(", (p) -> StringMatch.Match(p.GetString(), true, true, true, "in"));
            sScriptLine = pMatch.GetMatches() > 0 ? pMatch.ReplaceAll() : sScriptLine;

            pMatch.Reset(sScriptLine, "def", "function", (p) -> StringMatch.Match(p.GetString(), "(") && StringMatch.Match(p.GetString(), ")"));
            sScriptLine = pMatch.GetMatches() > 0 ? pMatch.ReplaceAll() : sScriptLine;

            pMatch.Reset(sScriptLine, "elif", "} else if (");
            sScriptLine = pMatch.GetMatches() > 0 ? pMatch.ReplaceAll() : sScriptLine;

            pMatch.Reset(sScriptLine, "if", "if (");
            sScriptLine = pMatch.GetMatches() > 0 ? pMatch.ReplaceAll() : sScriptLine;

            pMatch.Reset(sScriptLine,"else:", "} else:");
            sScriptLine = pMatch.GetMatches() > 0 ? pMatch.ReplaceAll() : sScriptLine;

            if (!sScriptLine.trim().equals(sContext.trim())) {
                pMatch.Reset(sScriptLine, ":", (StringMatch.Match(sScriptLine, "else:", "function") ? " {" : ") {"));
                sScriptLine = pMatch.GetMatches() > 0 ? pMatch.ReplaceAll() : sScriptLine;
            }
        }
        if (StringMatch.Match(sScriptLine, true, true, true, "( ")) {
            sScriptLine = StringMatch.ReplaceAll(sScriptLine, "( ", "(", true, true, true, null);
        }
        return sScriptLine;
    }

    public String ConvertSemicolon(String sScriptLine, boolean bArray) {
        if (!sScriptLine.isBlank() && !bArray) {
            StringMatch pMatch = new StringMatch(sScriptLine, "", "", false, true, false);
            String[] aBlockedEnd = {"(", "\"", "'", "[", "{", "+", "&", "*", "/", "|"};
            for (String sBlocked : aBlockedEnd) {
                if (pMatch.EndsWith(sBlocked)) {
                    return sScriptLine;
                }
            }
            sScriptLine = pMatch.Append("; ", (s) -> !s.isBlank());
        }
        return sScriptLine;
    }

    public boolean IsMergeWithNextLine(String sScriptLine, String sAppend) {
        if (!sScriptLine.isBlank() && !sAppend.isBlank()) {
            StringMatch pScriptLineMatch = new StringMatch(sScriptLine, "", "", false, true, true);
            StringMatch pAppendMatch = new StringMatch(sAppend, "", "", false, true, true);
            if (pAppendMatch.StartsWith("+")) {
                return pAppendMatch.StartsWith("+ \"") &&
                        (pScriptLineMatch.EndsWith("\"") || pScriptLineMatch.EndsWith(")"));
            } else if (pAppendMatch.StartsWith("\"")) {
                return pScriptLineMatch.EndsWith("(") || pScriptLineMatch.EndsWith("+") || pScriptLineMatch.EndsWith("\"");
            } else if (pAppendMatch.StartsWith("(")) {
                return pScriptLineMatch.EndsWith("+");
            }
        }
        return false;
    }

    public String ConvertMergeWithNextLine(String sScriptLine, String sAppend) {
        StringMatch pMatch = new StringMatch(sScriptLine, "", "", false, false, false);
        StringMatch pAppend = new StringMatch(sAppend, "", "", false, false, false);
        boolean bMerge = false;
        if (pAppend.StartsWith("+")) {
            if (pAppend.StartsWith("+ \"")) {
                if (pMatch.EndsWith("\"")) {
                    pAppend.SetFindText("+ \"");
                    pMatch.SetFindText("\"");
                    bMerge = true;
                } else if (pMatch.EndsWith(")")) {
                    pMatch.SetFindText("\" +");
                    bMerge = true;
                }
            }
        } else if (pAppend.StartsWith("\"")) {
            if (pMatch.EndsWith("+")) {
                if (pMatch.EndsWith("\" +")) {
                    pMatch.SetFindText("\" +");
                    bMerge = true;
                } else if (pMatch.EndsWith(") +")) {
                    pMatch.SetFindText("\" +");
                    bMerge = true;
                }
            } else if (pMatch.EndsWith("\"")) {
                pMatch.SetFindText("\"");
                pAppend.SetFindText("\"");
                bMerge = true;
            } else if (pMatch.EndsWith("(")) {
                bMerge = true;
            }
        }
        return bMerge ? (pMatch.ReplaceLast() + pAppend.ReplaceFirst().trim()) : null;
    }

    public String ConvertAskMenu(String sScriptLine) {
        if (StringMatch.Match(sScriptLine, false, true, true, ".Say(")) {
            if (StringMatch.Match(sScriptLine, false, false, true, "#L")) {
                sScriptLine = StringMatch.ReplaceAll(sScriptLine, ".Say(", ".AskMenu(", false, true, true);
                if (StringMatch.Match(sScriptLine, false, true, true, ", true)")) {
                    sScriptLine = StringMatch.ReplaceAll(sScriptLine, ", true)", ")", false, true, true);
                }
            }
        }
        return sScriptLine;
    }

    public String ConvertNestedAskYesNo(String sScriptLine) {
        if (StringMatch.Match(sScriptLine, false, true, true, "if (self.AskYesNo")) {
            if (StringMatch.Match(sScriptLine, false, true, true, ")) {")) {
                sScriptLine = StringMatch.ReplaceAll(sScriptLine, ")) {", ") == 1) {", false, true, true);
            }
        }
        return sScriptLine;
    }

    public void LogMissedShitInfo(String sScriptLine, String sFileName) {
        /*if (StringMatch.Match(sScriptLine, false, true, true, ".")) {
            StringMatch pMatch = new StringMatch(sScriptLine, ".", "", false, true, true);
            if (pMatch.GetMatches() > 0) {
                String[] aSkipIfMatch = {"length", "type", "includes"};
                for (int i = 0; i < pMatch.GetMatches(); i++) {
                    StringMatch.Match pMatchItr = pMatch.GetMatchesList().get(i);
                    if (pMatchItr.GetString().length() >= pMatchItr.GetStartIndex() + pMatchItr.GetFindText().length()) {
                        if (Character.isLowerCase(pMatchItr.GetString().charAt(pMatchItr.GetEndIndex()))) {
                            String sText = pMatchItr.GetString().substring(pMatchItr.GetEndIndex());
                            StringMatch pMatchSkip = new StringMatch(sText, "", "", true, true, true);
                            if (!sText.isBlank()) {
                                String sMatch = "";
                                for (String sKey : aSkipIfMatch) {
                                    pMatchSkip.SetFindText(sKey);
                                    if (pMatchSkip.StartsWith(sKey)) {
                                        sMatch = sKey;
                                        break;
                                    }
                                }
                                if (sMatch.isBlank()) {
                                    util.Logger.LogReport("(" + sFileName + ") [" + sMatch + "] {" + pMatchItr.GetString().charAt(pMatchItr.GetEndIndex()) + "} / " + sScriptLine);
                                }
                            }
                        }
                    }
                }
            }
        }*/
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
                    sAdditionalLine += sOperation.contains(">") ? (sVariable + "--;") : sOperation.contains("<") ? (sVariable + "++;") : "";
                }
            }
        }
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
