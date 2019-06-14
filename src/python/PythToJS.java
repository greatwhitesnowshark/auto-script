/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package python;

import com.sun.media.jfxmedia.logging.Logger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import util.StringUtil;

/**
 *
 * @author Five
 */
public class PythToJS {
    
    public static final Map<String, LinkedList<String>> mScriptLines = Collections.synchronizedMap(new HashMap<>());
    public static final Map<String, String> mReplace = new LinkedHashMap<>();
    public static final Map<String, String> mReplaceRegex = new LinkedHashMap<>();
    public static final Map<String, Integer> mParamArgsAdd = new LinkedHashMap<>();
    public static final Map<String, Integer> mParamArgsRemove = new LinkedHashMap<>();
    public static final Map<Integer, String> mParamArgsAppend = new LinkedHashMap<>();
    public static final Map<String, String> mDebugLines = new HashMap<>();
    public static final List<Integer> aParamArgs = new LinkedList<>();
    public static final int User = 1, NoESC = 2;
        

    public static void main(String[] args) {
        try {
            String sDirectory = "C:\\Users\\Chris\\Desktop\\Swordie\\scripts";
            Files.walk(Paths.get(sDirectory)).forEach((pFile) -> {
                if (!pFile.toFile().isDirectory()) {
                    try {
                        ConvertPythonScript(pFile.toFile());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Logger.logMsg(Logger.INFO, "Processing directory [" + pFile.getFileName() + "]....");
                }
            });
            
            sDirectory = "C:\\Users\\Chris\\Desktop\\Swordie\\scripts_javascript\\";
            for (String sFileName : mScriptLines.keySet()) {
                try (BufferedWriter pWriter = new BufferedWriter(new FileWriter(sDirectory + sFileName))) {
                    LinkedList<String> aScriptLines = mScriptLines.get(sFileName);
                    for (String sLine : aScriptLines) {
                        pWriter.write(sLine); 
                        pWriter.newLine();
                    }
                }
                Logger.logMsg(Logger.INFO, "Created new script - `" + sFileName + "`");
            }
            
            if (!mDebugLines.isEmpty()) {
                Object[] aSortedKey = mDebugLines.keySet().toArray();
                Arrays.sort(aSortedKey);
                System.out.println("These are the [" + aSortedKey.length + "] methods that are currently unaccounted for: \r\n");
                for (Object sDebug : aSortedKey) {
                    System.out.println("\t- " + sDebug + " //" + mDebugLines.get((String) sDebug));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static final void ConvertPythonScript(File pFile) throws IOException {
        //Task 1.  Convert all script lines from their python indentation and block them appropriately
        boolean bConcatNextLine = false;
        int nCloseBracketPad = -1;
        LinkedList<String> aScriptLines = new LinkedList<>();
        try (BufferedReader pReader = new BufferedReader(new FileReader(pFile))) {
            mParamArgsAppend.clear();
            mParamArgsRemove.clear();
            while (pReader.ready()) {
                String sLine = pReader.readLine();
                boolean bComment = false, bParamArg = false;
                if (!sLine.trim().isEmpty()) {
                    //comments
                    if (sLine.charAt(0) == '#') {
                        sLine = ("//" + sLine.substring(1));
                        bComment = true;
                    } else if (sLine.contains(" # ")) {
                        sLine = sLine.replace(" # ", "; // ");
                        bComment = true;
                    } else if (sLine.contains("sm.dispose()")) {
                        //sLine = ("//" + sLine);
                        continue;
                    } else {
                        if (bConcatNextLine) {
                            bConcatNextLine = false;
                            if (sLine.indexOf('"') >= 0 && sLine.indexOf('"') + 1 < sLine.length()) {
                                String sPrevLine = aScriptLines.remove(aScriptLines.size() - 1);
                                String sTrimmedLine = sLine.substring(sLine.indexOf('"') + 1);
                                sPrevLine = sPrevLine.substring(0, sPrevLine.length() - 1);
                                String sResult = (sPrevLine + sTrimmedLine);
                                //check for additional line concats
                                if (sResult.contains("sm.") && sResult.contains("(\"")) {
                                    if (!sLine.contains("\")")) {
                                        bConcatNextLine = true;
                                    }
                                }
                                if (sResult.charAt(sResult.length() - 1) == ')') {
                                    sResult += ";";
                                }
                                //modify say functions based on bparam options
                                for (String sKey : mParamArgsAdd.keySet()) {
                                    if (sResult.contains(sKey)) {
                                        int nFlag = mParamArgsAdd.get(sKey);
                                        if (!aParamArgs.contains(nFlag)) {
                                            aParamArgs.add(nFlag);
                                            bParamArg = true;
                                        }
                                    }
                                }
                                //convert functions/syntax
                                for (String sKey : mReplace.keySet()) {
                                    while (sResult.contains(sKey)) {
                                        sResult = sResult.replace(sKey, mReplace.get(sKey));
                                    }
                                }
                                //convert reg-ex expressions
                                for (String sKey : mReplaceRegex.keySet()) {
                                    if (sResult.contains(sKey)) {
                                        sResult = sResult.replaceAll(sKey, mReplaceRegex.get(sKey));
                                    }
                                }
                                if (!bParamArg) {
                                    aScriptLines.add(sResult);
                                }
                                continue;
                            }
                        }
                        if (!bConcatNextLine && !bParamArg) {
                            //add closing bracket
                            if (nCloseBracketPad >= 0) {
                                if (StringUtil.CountStringPaddingChar(sLine) == nCloseBracketPad || !pReader.ready()) {
                                    aScriptLines.add(ToPaddedString("}", nCloseBracketPad));
                                    nCloseBracketPad = -1;
                                }
                            }
                        }
                    }
                    //conditional blocks
                    if (sLine.contains("elif")) {
                        aScriptLines.remove(aScriptLines.size() - 1);
                        sLine = sLine.replace("elif", "} else if (");
                    } else if (sLine.contains("if ")) {
                        sLine = sLine.replace("if ", "if (");
                    } else if (sLine.contains("else:")) {
                        aScriptLines.remove(aScriptLines.size() - 1);
                        sLine = sLine.replace("else:", "} else:");
                    }
                    //curly bracket for :
                    if (!bComment && sLine.contains(":") && !sLine.contains("case")) {
                        sLine = sLine.replace(":", (sLine.contains("else:") ? " {" : ") {"));
                        nCloseBracketPad = StringUtil.CountStringPaddingChar(sLine);
                        //aScriptLines.add(sLine);
                        //continue;
                    }
                    //check for line concat
                    if (sLine.contains("sm.send") && sLine.contains("(\"")) {
                        if (!sLine.contains("\")")) {
                            bConcatNextLine = true;
                        }
                    }
                    //add semicolon to finish a statement
                    if (!bConcatNextLine && !bComment && !sLine.contains("{")) {
                        sLine += ";";
                    }
                    //modify say functions based on bparam options
                    for (String sKey : mParamArgsAdd.keySet()) {
                        if (sLine.contains(sKey)) {
                            int nFlag = mParamArgsAdd.get(sKey);
                            if (!aParamArgs.contains(nFlag)) {
                                aParamArgs.add(nFlag);
                                bParamArg = true;
                            }
                        }
                    }
                    final boolean bAppend = sLine.contains("sm.send");
                    //convert functions/syntax
                    for (String sKey : mReplace.keySet()) {
                        while (sLine.contains(sKey)) {
                            sLine = sLine.replace(sKey, mReplace.get(sKey));
                        }
                    }
                    //convert reg-ex expressions
                    for (String sKey : mReplaceRegex.keySet()) {
                        if (sLine.contains(sKey)) {
                            sLine = sLine.replaceAll(sKey, mReplaceRegex.get(sKey));
                        }
                    }
                    if (bAppend) {
                        String sAppend = "";
                        for (int nFlag : aParamArgs) {
                            sAppend += mParamArgsAppend.get(nFlag);
                        }
                        if (!sAppend.isEmpty()) {
                            sLine = sLine.substring(0, sLine.indexOf("(")) + sAppend + sLine.substring(sLine.indexOf("("));
                        }
                    }
                    //compile list of functions still need to be converted
                    if (sLine.contains("self.") && Character.isLowerCase(sLine.charAt(sLine.indexOf(".") + 1)) && sLine.contains("(")) {
                        String sTrimmedLine = sLine.substring(sLine.indexOf("self."));
                        if (sTrimmedLine.contains("(")) {
                            String sDebug = sTrimmedLine.substring(0, sTrimmedLine.indexOf("("));
                            if (!mDebugLines.keySet().contains(sDebug)) {
                                mDebugLines.put(sDebug, pFile.getName());
                            }
                        }
                    }
                    //add result to queue
                    if (!bParamArg) {
                        aScriptLines.add(sLine);
                        //check for any additional functions that need to be added after line has been inserted
                        if (sLine.contains("InGameDirectionEvent.Delay")) {
                            int nPadding = StringUtil.CountStringPaddingChar(sLine);
                            sLine = StringUtil.AddStringPaddingChar("self.Wait();", nPadding);
                            aScriptLines.add(sLine);
                        }
                    }
                }
            }
        }
        if (!aScriptLines.isEmpty()) {
            String sPrefix = pFile.getCanonicalPath().contains("npc") ? "npc\\"
                    :   pFile.getCanonicalPath().contains("quest") ? "quest\\"
                    :   pFile.getCanonicalPath().contains("portal") ? "portal\\"
                    :   pFile.getCanonicalPath().contains("reactor") ? "reactor\\"
                    :   pFile.getCanonicalPath().contains("field") ? "field\\"
                    :   pFile.getCanonicalPath().contains("item") ? "item\\"
                    :   "invalid\\";
            String sName = pFile.getName().contains(".py") ? pFile.getName().replace(".py", ".js")
                    :   !pFile.getName().contains(".") ? pFile.getName().concat(".js")
                    :   pFile.getName();
            mScriptLines.put((sPrefix + sName), aScriptLines);
        }
    }
    
    public static String ToPaddedString(String sText, int nPadding) {
        StringBuilder pBuilder = new StringBuilder();
        for (int i = 0; i < nPadding; i++) {
            pBuilder.append(" ");
        }
        pBuilder.append(sText);
        return pBuilder.toString();
    }
    
    static {
        mReplace.put("False", "false");
        mReplace.put("True", "true");
        mReplace.put("str(", "(");
        mReplace.put("sm.", "self.");
        mReplace.put("sendSay", "Say");
        mReplace.put("sendNext", "SayNext");
        
        mReplace.put("addDamageSkin", "UserSaveDamageSkin");
        mReplace.put("avatarLookSet(", "OnUserInGameDirectionEvent(InGameDirectionEvent.AvatarLookSet, ");
        mReplace.put("avatarOriented", "EffectAvatarOriented");
        mReplace.put("addPopupSay", "UserAddPopupSay");
        mReplace.put("addQRValue", "QuestRecordSet");
        mReplace.put("balloonMsg", "UserBalloonMsg");
        mReplace.put("blind", "FieldEffectBlind");
        mReplace.put("bgmVolume", "FieldEffectBGMVolumeOnly");
        mReplace.put("createQuestWithQRValue", "QuestRecordExSet");
        mReplace.put("forcedAction(", "OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedAction, ");
        mReplace.put("forcedFlip(", "OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedFlip, ");
        mReplace.put("lockInGameUI", "OnSetInGameDirectionMode");
        mReplace.put("moveCamera(", "OnUserInGameDirectionEvent(InGameDirectionEvent.CameraMove, ");
        mReplace.put("offLayer", "EffectOffLayer");
        mReplace.put("onLayer", "EffectOnLayer");
        mReplace.put("playSound", "EffectSound");
        mReplace.put("removeAdditionalEffect(", "OnUserInGameDirectionEvent(InGameDirectionEvent.RemoveAdditionalEffect");
        mReplace.put("removeOverlapScreen", "FieldEffectRemoveOverlapDetail");
        mReplace.put("reservedEffectRepeat", "EffectReservedRepeat");
        mReplace.put("sendDelay(", "OnUserInGameDirectionEvent(InGameDirectionEvent.Delay, ");
        mReplace.put("showNpcSpecialActionByTemplateId", "OnNpcSpecialAction");
        mReplace.put("showEffect(", "OnUserInGameDirectionEvent(InGameDirectionEvent.EffectPlay, ");
        mReplace.put("showFadeTransition", "FieldEffectOverlapDetail");
        mReplace.put("spawnNpc", "FieldSummonNpc");
        mReplace.put("warp", "RegisterTransferField");
        mReplace.put("zoomCamera(", "OnUserInGameDirectionEvent(InGameDirectionEvent.CameraZoom, ");
        
        mReplaceRegex.put("addAP(\\d+)", "UserIncAP(\\d+, true)");
        mReplaceRegex.put("addLevel(\\d+)", "UserIncLevel(\\d+, true)");
        mReplaceRegex.put("addMaxHP(\\d+)", "UserIncMHP(\\d+, true)");
        mReplaceRegex.put("addMaxMP(\\d+)", "UserIncMMP(\\d+, true)");
        mReplaceRegex.put("addSP(\\d+)", "UserIncSP(\\d+, true)");
        mReplaceRegex.put("addSp(\\d+)", "UserIncSP(\\d+, true)");
        mReplaceRegex.put("changeBGM(\"\\w+\", \\d+, \\d+", "FieldEffectChangeBGM(\"\\w+\"");
        
        mParamArgsAdd.put("boxChatPlayerAsSpeaker", User);
        mParamArgsAdd.put("addEscapeButton", NoESC);
        
        mParamArgsAppend.put(User, "User");
        mParamArgsAppend.put(NoESC, "NoESC");
    }
}
