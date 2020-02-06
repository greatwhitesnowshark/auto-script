package python.handle;

import util.StringUtil;

import java.util.LinkedList;

/**
 * @author Sharky
 */
public class MiscStringPart extends AbstractHandler {
    
    public static MiscStringPart pInstance = new MiscStringPart();
    
    @Override
    public String Convert(String sScriptLine) {
        String sCommentsTrimmed = sScriptLine.contains("//") ? sScriptLine.substring(sScriptLine.indexOf("//")) : "";
        if (!sCommentsTrimmed.isBlank()) {
            sScriptLine = sScriptLine.substring(0, sScriptLine.indexOf("//"));
        }
        boolean bNpc = sScriptLine.contains("self.Ask") || sScriptLine.contains("self.Say");
        if (!bNpc && ((sScriptLine.contains("if (") || sScriptLine.contains("if(")) && sScriptLine.contains("{"))) {
            if (sScriptLine.contains("in ")) {
                String sLineConcat = "";
                LinkedList<String> aLineConcat = new LinkedList<>();
                String[] aLine = sScriptLine.split(" ");
                if (aLine.length > 4) {
                    String sVariable = "";
                    boolean bElseIf = sScriptLine.contains("} else if"), bIn = false, bWrapConditional = false;
                    for (int a = 0; a < aLine.length; a++) {
                        String sWord = aLine[a];
                        int nIndex = bElseIf && a >= 2 ? (a - 2) % 4 : !bElseIf ? a % 4 : -1;
                        if (nIndex >= 0) {

                            switch (nIndex) {

                                case 0: //if, &&
                                    if (a == aLine.length - 1 && sWord.contains("{")) {
                                        String s = aLineConcat.removeLast();
                                        if (s != null) {
                                            s += bWrapConditional ? "))" : ")";
                                            aLineConcat.add(s);
                                        }
                                    }
                                    aLineConcat.add(sWord);
                                    sVariable = "";
                                    bIn = false;
                                    break;

                                case 1: //variable
                                    sVariable = sWord;
                                    break;

                                case 2: //in statement
                                    if ((sWord.contains("in") || sWord.contains("!in")) && sWord.trim().length() <= 3) {
                                        bIn = true;
                                        sVariable = a == 1 && sVariable.charAt(0) == '(' ? sVariable.substring(1).trim() : sVariable;
                                    } else {
                                        aLineConcat.add(sVariable);
                                        aLineConcat.add(sWord);
                                        sVariable = "";
                                    }
                                    break;

                                case 3: //conditional
                                    if (bIn && !sVariable.isBlank()) {
                                        if (sWord.trim().length() > 2 && sWord.trim().substring(sWord.trim().length() - 2).equals("))")) {
                                            sWord = sWord.trim().substring(0, sWord.trim().length() - 1);
                                        }
                                        if (!sWord.contains("()") && !sWord.contains("Get")) {
                                            sWord = sWord.replace(")", "");
                                        }
                                        sVariable = (a == 3 || (bElseIf && a == 5)) && sVariable.charAt(0) == '(' ? sVariable.substring(1) : sVariable;
                                        if (sVariable.charAt(0) == '!') {
                                            sVariable = sVariable.substring(1);
                                            if (StringUtil.CountChar(sVariable, '(') - StringUtil.CountChar(sVariable, ')') == 1) {
                                                bWrapConditional = true;
                                                sVariable = sVariable.substring(1);
                                                sWord = "!(" + sWord;
                                            } else {
                                                sWord = "!" + sWord;
                                            }
                                        }
                                        sWord = a == 3 || (bElseIf && a == 5) ? "(" + sWord.trim() : sWord.trim();
                                        aLineConcat.add(sWord + ".includes(" + sVariable + ")");
                                    } else {
                                        aLineConcat.add(sWord);
                                    }
                                    break;
                            }

                        } else {
                            aLineConcat.add(sWord);
                        }
                    }
                } else {
                    for (String s : aLine) {
                        aLineConcat.add(s);
                    }
                }
                sLineConcat += String.join(" ", aLineConcat);
                sLineConcat += " ";
                if (!sScriptLine.trim().equals(sLineConcat.trim())) {
                    //Logger.Println("\r\nFile:         (" + pFile.getFileName().toString() + ")");
                    //Logger.Println("sScriptLine:        -" + sScriptLine.trim());
                    //Logger.Println("sLineConcat:  -" + sLineConcat.trim());
                    sScriptLine = sLineConcat;
                }
            }
        }
        if (!sCommentsTrimmed.isBlank()) {
            sScriptLine += sCommentsTrimmed;
        }
        return sScriptLine;
    }
}
