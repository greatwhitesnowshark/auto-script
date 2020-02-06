package python.handle;

import base.util.Pointer;
import util.StringMatch;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sharky
 */
public class MistranslatedText extends AbstractHandler {

    public static Pointer<Integer> nCount = new Pointer<>(0);
    public static MistranslatedText pInstance = new MistranslatedText();
    public static Map<String, String> mReplace = new LinkedHashMap<>(); //all text-to-text replacements

    @Override
    public String Convert(String sScriptLine) {
        try {
            StringMatch pStringMatch;
            for (String sKey : mReplace.keySet()) {
                pStringMatch = new StringMatch(sScriptLine, sKey, "", true, true, true);
                if (pStringMatch.GetMatches() > 0) {
                    pStringMatch.SetReplaceText(mReplace.get(sKey));
                    if (sKey.equals("\");")) {
                        int nQtCount = 0;
                        for (char c : sScriptLine.toCharArray()) {
                            if (c == '"') {
                                nQtCount++;
                            }
                        }
                        if (nQtCount % 2 != 0) {
                            sScriptLine = sScriptLine.replace(sKey, mReplace.get(sKey));
                            //nCount.setElement(nCount.element() + 1);
                        } else {
                            break;
                        }
                    } else if (sKey.equals("parseInt(")) {
                        if (!sScriptLine.contains(">>") && !sScriptLine.contains("<") && !sScriptLine.contains(" & ") && !sScriptLine.contains(" | ") && !sScriptLine.contains("^") && !sScriptLine.contains("|=") && !sScriptLine.contains("&=")) {
                            sScriptLine = sScriptLine.replace(sKey, mReplace.get(sKey));
                            //nCount.setElement(nCount.element() + 1);
                        } else {
                            break;
                        }
                    } else if (sKey.equals("var ")) {
                        int nIndex = sScriptLine.trim().indexOf("var ");
                        String s = sScriptLine.substring(sScriptLine.indexOf("var "));
                        if (s.contains(" ")) {
                            String[] as = s.split(" ");
                            if (as.length > 1) {
                                String split = as[1].trim();
                                if (split.contains(".")) {
                                    sScriptLine = sScriptLine.replace(sKey, "");
                                    //nCount.setElement(nCount.element() + 1);
                                    break;
                                }
                            }
                        }
                        if (nIndex > 0) {
                            if (sScriptLine.trim().charAt(nIndex - 1) != ' ') {
                                if (!sScriptLine.trim().contains("for (var ")) {
                                    base.util.Logger.Println("");
                                    base.util.Logger.Println("sScriptLine = " + sScriptLine.trim());
                                    base.util.Logger.Println("character before \"var \" is:  [%s]", sScriptLine.trim().charAt(nIndex - 1));
                                    base.util.Logger.LogReport("Not replacing this instance of var.");
                                    break;
                                }
                            }
                        }
                        sScriptLine = sScriptLine.replace(sKey, "");
                        //nCount.setElement(nCount.element() + 1);
                    } else if (sKey.contains("stopEvent")) {
                        sScriptLine = sScriptLine.replace("self.stopEvents();", "");
                        //nCount.setElement(nCount.element() + 1);
                    } else {
                        if (sKey.equals("self.RegisterTransferFieldInstance(")) {
                            if (sScriptLine.substring(sScriptLine.indexOf(sKey)).contains(", true")) {
                                sScriptLine = sScriptLine.replace(", true", "");
                            }
                        } else {
                            sScriptLine = pStringMatch.ReplaceAll();
                        }
                        //nCount.setElement(nCount.element() + 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Logger.Println("Total number of mistranslated texts: " + nCount.element());
            //Logger.Println("Done with ConvertMistranslatedText()");
        }
        return sScriptLine;
    }


    static {
        mReplace.put("SpeakerTypeID.FlipImageSpeakerTypeID.ScenarioIlluChat", "SpeakerTypeID.FlipImage");
        mReplace.put("{;", "{");
        mReplace.put("}\");", "}");
        mReplace.put("{\");", "{");
        mReplace.put(".var ", ".");
        mReplace.put("game.user.stat.CharacterTemporaryStat", "base.user.stat.CharacterTemporaryStat");
        mReplace.put("self.GetUser().getLevel", "self.UserGetLevel");
        mReplace.put("self.GetUser().getJob()", "self.UserGetJob()");
        mReplace.put("str (", "(");
        mReplace.put("game.user.skill.accessor.JobAccessor", "base.user.skill.accessor.JobAccessor");
        mReplace.put("self.openShop", "self.UserOpenShop");
        mReplace.put("Math.rand()", "Math.random()");
        mReplace.put(", ]", "]");
        mReplace.put(" \\;", " +");
        mReplace.put("self.RegisterTransferFieldInstance(", "self.UserRegisterInstanceField(");
        mReplace.put("self.self.", "self.");
        mReplace.put("parseInt(", "Number(");
        mReplace.put("OnSetInGameDirectionMode(false)", "OnSetInGameDirectionMode(false, false)");
        mReplace.put("OnSetInGameDirectionMode(true)", "OnSetInGameDirectionMode(true, false)");
        mReplace.put("self.SendOk(", "self.Say(");
        mReplace.put("pUser.warp(", "pUser.OnTransferField(");
        mReplace.put("var ", "");
        mReplace.put("self.stopEvents(", "");
        mReplace.put("chr.", "self.");
        mReplace.put("spawnReactor(", "FieldCreateReactor(");
        mReplace.put("FieldEffectBlindEffect", "FieldEffectBlind");
        mReplace.put("EffectOffLayer", "EffectOffLayer");
        mReplace.put("\");", ");");
    }
}
