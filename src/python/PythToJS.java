/*
 * To change this license opcode, choose License Headers in Project Properties.
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
 * @author Sharky - Really more of "swordie-python" to JS
 */
public class PythToJS {
    
    public static final Map<String, LinkedList<String>> mScriptLines = Collections.synchronizedMap(new HashMap<>());
    public static final Map<String, String> mReplace = new LinkedHashMap<>();
    public static final Map<String, String> mFuncArgsAppend = new LinkedHashMap<>();
    public static final Map<String, String> mChatTypeAppend = new LinkedHashMap<>();
    public static final Map<String, String> mDebugFuncName = new HashMap<>();
    public static final Map<String, String> mFuncAppend = new HashMap<>();
    public static final List<String> aParamArgs = new LinkedList<>();
        

    public static void main(String[] args) {
        try {
            String sDirectory = "C:\\Users\\Chris\\Desktop\\Swordie\\scripts";
            Files.walk(Paths.get(sDirectory)).forEach((pFile) -> {
                if (!pFile.toFile().isDirectory()) {
                    try {
                        ConvertPythToJS(pFile.toFile());
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
            
            if (!mDebugFuncName.isEmpty()) {
                Object[] aSortedKey = mDebugFuncName.keySet().toArray();
                Arrays.sort(aSortedKey);
                System.out.println("These are the [" + aSortedKey.length + "] methods that are currently unaccounted for: \r\n");
                for (Object sDebug : aSortedKey) {
                    System.out.println("\t- " + sDebug + " //" + mDebugFuncName.get((String) sDebug));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static final String ConvertComments(String sScriptLine) {
        if (!sScriptLine.isEmpty()) {
            if (sScriptLine.trim().charAt(0) == '#') {
                sScriptLine = sScriptLine.replace("#", "// ");
            } else if (sScriptLine.contains(" # ")) {
                sScriptLine = sScriptLine.replace(" # ", (!sScriptLine.contains("{") && !sScriptLine.contains(":") ? "; // " : " // "));
            }
            if (GetBlockPaddingChar(sScriptLine, false) < 4) {
                sScriptLine = sScriptLine.trim();
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
    
    public static final String ConvertFuncArgsAppend(String sScriptLine) {
        for (String sLine : mFuncArgsAppend.keySet()) {
            if (sScriptLine.contains(sLine)) {
                if (sScriptLine.contains(")") && !sScriptLine.contains(mFuncArgsAppend.get(sLine))) {
                    sScriptLine = sScriptLine.replace(")", mFuncArgsAppend.get(sLine) + ")");
                    break;
                }
            }
        }
        return sScriptLine;
    }
    
    public static final String ConvertReplaceFunc(String sScriptLine) {
        for (String sLine : mReplace.keySet()) {
            if (sScriptLine.contains(sLine)) {
                sScriptLine = sScriptLine.replace(sLine, mReplace.get(sLine));
            }
        }
        return sScriptLine;
    }
    
    public static final String ConvertChatTypeAppend(String sScriptLine) {
        if (sScriptLine.contains("self.Say") || sScriptLine.contains("self.Ask")) {
            if (!aParamArgs.isEmpty() && sScriptLine.contains("(")) {
                String sAppend = "";
                for (String s : aParamArgs) {
                    sAppend += s;
                }
                sScriptLine = sScriptLine.replace("(", (sAppend + "("));
            }
        }
        return sScriptLine;
    }
    
    public static final String ConvertSemicolon(String sScriptLine) {
        if (!sScriptLine.contains("//") && sScriptLine.trim().charAt(sScriptLine.trim().length() -1) != '{') {
            sScriptLine += ";";
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
    
    public static final String GetFuncAppend(String sScriptLine) {
        for (String sKey : mFuncAppend.keySet()) {
            if (sScriptLine.contains(sKey)) {
                return mFuncAppend.get(sKey);
            }
        }
        return "";
    }
    
    public static final boolean IsSkippedLine(String sScriptLine) {
        if (sScriptLine.contains("sm.dispose") || sScriptLine.contains("sm.diposse") || sScriptLine.contains("sm.disose")) {
            return true;
        } else {
            for (String sKey : mChatTypeAppend.keySet()) {
                if (sScriptLine.contains(sKey)) {
                    String sAppend = mChatTypeAppend.get(sKey);
                    if (!aParamArgs.contains(sAppend)) {
                        aParamArgs.add(sAppend);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public static final boolean IsClosingBracketInsert(String sScriptLine, int nBlockPadding) {
        if (sScriptLine.contains("}") && GetBlockPaddingChar(sScriptLine, false) == nBlockPadding) {
            return false;
        }
        return nBlockPadding >= 0 && GetBlockPaddingChar(sScriptLine, false) <= nBlockPadding;
    }
    
    public static final boolean IsClosingBracketNeeded(String sScriptLine) {
        return sScriptLine.contains("{") && (sScriptLine.contains("if") || sScriptLine.contains("else"));
    }
    
    public static final boolean IsLineSplit(String sScriptLine) {
        char c = sScriptLine.trim().length() > 1 ? sScriptLine.trim().charAt(sScriptLine.trim().length() - 1) : '_', 
             b = sScriptLine.trim().length() > 2 ? sScriptLine.trim().charAt(sScriptLine.trim().length() - 2) : '_', 
             a = sScriptLine.trim().length() > 3 ? sScriptLine.trim().charAt(sScriptLine.trim().length() - 3) : '_';
        if (c == '+' || c == '"' || (c == ')' && (b == '(' || (b == ')' && a == '(')))) {
            return (sScriptLine.contains("sm.") && (sScriptLine.contains("(\"") || sScriptLine.contains(", \""))) || sScriptLine.contains("= \"");
        }
        return false;
    }
    
    public static final int GetBlockPaddingChar(String sScriptLine, boolean bSet) {
        int nCharPad = StringUtil.CountStringPaddingChar(sScriptLine);
        int nTabPad = StringUtil.CountStringPaddingTab(sScriptLine);
        return nCharPad > nTabPad * 4 ? nCharPad : nTabPad * 4;
    }
    
    public static final void LogDebugInfo(String sScriptLine, String sFileName) {
        if (sScriptLine.contains("self.") && Character.isLowerCase(sScriptLine.charAt(sScriptLine.indexOf(".") + 1)) && sScriptLine.contains("(")) {
            String sDebug = sScriptLine.substring(sScriptLine.indexOf("self."));
            if (sDebug.contains("(")) {
                String sDebugFuncName = sDebug.substring(0, sDebug.indexOf("("));
                if (!mDebugFuncName.keySet().contains(sDebugFuncName)) {
                    mDebugFuncName.put(sDebugFuncName, sFileName);
                }
            }
        }
    }
    
    public static final void ConvertPythToJS(File pFile) throws IOException {
        LinkedList<String> aScriptLines = new LinkedList<>();
        try (BufferedReader pReader = new BufferedReader(new FileReader(pFile))) {
            aParamArgs.clear();
            int nBlockPadding = -1;
            while (pReader.ready()) {
                String sScriptLine = pReader.readLine();
                if (!sScriptLine.trim().isEmpty()) {
                    if (!IsSkippedLine(sScriptLine)) {
                        sScriptLine = ConvertComments(sScriptLine);
                        sScriptLine = ConvertIfElseStatements(sScriptLine);
                        if (IsClosingBracketInsert(sScriptLine, nBlockPadding)) {
                            while (nBlockPadding > GetBlockPaddingChar(sScriptLine, false)) {
                                aScriptLines.add(ToPaddedString("}", nBlockPadding));
                                nBlockPadding -= 4;
                            }
                            nBlockPadding = -1;
                        }
                        if (IsClosingBracketNeeded(sScriptLine)) {
                            nBlockPadding = GetBlockPaddingChar(sScriptLine, false);
                        }
                        while (IsLineSplit(sScriptLine) && !sScriptLine.contains("\")") && pReader.ready()) {
                            String sAppend = pReader.readLine().trim();
                            sAppend = ConvertComments(sAppend);
                            if (sAppend.length() > 1 && sAppend.contains(("\""))) {
                                sScriptLine = ConvertMergeWithNextLine(sScriptLine, sAppend);
                            } else {
                                break;
                            }
                        }
                        sScriptLine = ConvertFuncArgsAppend(sScriptLine);
                        sScriptLine = ConvertReplaceFunc(sScriptLine);
                        sScriptLine = ConvertChatTypeAppend(sScriptLine);
                        sScriptLine = ConvertSemicolon(sScriptLine);
                        aScriptLines.add(sScriptLine);
                        String sFuncAppend = GetFuncAppend(sScriptLine);
                        if (!sFuncAppend.isEmpty()) {
                            int nPadding = GetBlockPaddingChar(sScriptLine, false);
                            aScriptLines.add(ToPaddedString(sFuncAppend, nPadding));
                        }
                        LogDebugInfo(sScriptLine, pFile.getName());
                    }
                }
            }
            while (nBlockPadding >= 0) {
                aScriptLines.add(ToPaddedString("}", nBlockPadding));
                nBlockPadding -= 4;
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
        
        mReplace.put("addAP", "UserIncAP");
        mReplace.put("addDamageSkin", "UserSaveDamageSkin");
        mReplace.put("addLevel", "UserIncLevel");
        mReplace.put("addMaxHP", "UserIncMHP");
        mReplace.put("addMaxMP", "UserIncMMP");
        mReplace.put("addSP", "UserIncSP");
        mReplace.put("addSp", "UserIncSP");
        mReplace.put("avatarLookSet(", "OnUserInGameDirectionEvent(InGameDirectionEvent.AvatarLookSet, ");
        mReplace.put("avatarOriented", "EffectAvatarOriented");
        mReplace.put("addPopupSay", "UserAddPopupSay");
        mReplace.put("addPopUpSay", "UserAddPopupSay");
        mReplace.put("addQRValue", "QuestRecordSet");
        mReplace.put("balloonMsg", "UserBalloonMsg");
        mReplace.put("blind", "FieldEffectBlind");
        mReplace.put("bgmVolume", "FieldEffectBGMVolumeOnly");
        mReplace.put("canHold", "InventoryIsSlotFreeItemID");
        mReplace.put("changeBGM", "FieldEffectChangeBGM");
        mReplace.put("changeChannelAndWarp", "RegisterTransferFieldChannel"); //review these to determine if these should be instanced locations
        mReplace.put("changeCharacterLook", "TryChangeHairSkinOrFace");
        mReplace.put("changeFoothold", "SendDynamicObjUrusSync");
        mReplace.put("chat", "UserScriptMessage");
        mReplace.put("chatBlue", "UserScriptMessage");
        mReplace.put("chatRed", "UserScriptMessage");
        mReplace.put("chatScript", "UserScriptProgressMessage");
        mReplace.put("checkAllianceName", "IsAllianceNameFree");
        mReplace.put("checkParty", "UserIsPartyReadyCheck");
        mReplace.put("closeUI", "UserCloseUI");
        mReplace.put("completeQuest", "QuestRecordSetComplete");
        mReplace.put("completeQuestNoCheck", "QuestRecordSetComplete");
        mReplace.put("completeQuestNoRewards", "QuestRecordSetComplete");
        mReplace.put("consumeItem", "consumeItem");//todo::
        mReplace.put("createAlliance", "createAlliance");//todo::
        mReplace.put("createClock", "FieldClock");
        mReplace.put("createFallingCatcher", "CreateFieldFallingCatcher");
        mReplace.put("createFieldTextEffect", "OnUserTextEffect");
        mReplace.put("createStopWatch", "FieldClockStopwatch");
        mReplace.put("createQuestWithQRValue", "QuestRecordExSet");
        mReplace.put("curNodeEventEnd", "OnInGameCurNodeEventEnd");
        mReplace.put("deductMesos", "UserDeductMoney");
        mReplace.put("deleteQuest", "QuestRecordRemove"); //todo:: the first parameter argument for User pUser be removed.
        mReplace.put("doEventAndSendDelay", "doEventAndSendDelay"); //todo:: this is horse shit, needs to be addressed manually
        mReplace.put("dropItem", "FieldDropItem");
        mReplace.put("faceOff(", "OnUserInGameDirectionEvent(InGameDirectionEvent.FaceOff, ");
        mReplace.put("fadeInOut", "OnUserFadeInOutEffect");
        mReplace.put("False", "false");
        mReplace.put("forcedAction(", "OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedAction, ");
        mReplace.put("forcedFlip(", "OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedFlip, ");
        mReplace.put("forcedInput(", "OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedInput, ");
        mReplace.put("forcedMove(", "OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedMove, ");
        mReplace.put("gainItem", "InventoryItemExchange");
        mReplace.put("getChr", "GetUser");
        mReplace.put("getDay", "GetDay");
        mReplace.put("getDropInRect", "getDropInRect"); //todo:: probably remove occurrences of this as we process drops from 
        mReplace.put("getFieldID", "UserGetFieldID");
        mReplace.put("getMPExpByMobId", "getMPExpByMobId"); //todo:: probably remove occurrences of this because its MP
        mReplace.put("getMesos", "UserGetMoney");
        mReplace.put("getMonsterParkCount", "getMonsterParkCount"); //todo:: probably remove occurrences of this because its MP
        mReplace.put("getNpcScriptInfo", "getNpcScriptInfo"); //todo:: remove this stupid shit
        mReplace.put("getParty", "getParty"); //todo:: rewrite these to use our party-event handles
        mReplace.put("getPartyMembersInSameField", "UserGetPartyMemberFieldCount");
        mReplace.put("getQRValue", "QuestRecordExGet");
        mReplace.put("getQuantityOfItem", "InventoryGetItemCount");
        mReplace.put("getRandomIntBelow", "GetRand");
        mReplace.put("getReactorQuantity", "FieldGetReactorCount");
        mReplace.put("getReactorState", "FieldGetReactorState");
        mReplace.put("getReturnField", "GetReturnFieldID");
        mReplace.put("getSkillByItem", "getSkillByItemID"); //todo:: verify where this should come from
        mReplace.put("getUnionCoin", "getUnionCoin"); //todo:: remove all occurrences
        mReplace.put("getUnionLevel", "getUnionLevel"); //todo:: remove all occurrences
        mReplace.put("getnOptionByCTS", "getnOptionByCTS"); //todo:: modify this argument given
        mReplace.put("giveAndEquip", "giveAndEquip"); //todo:: remove all occurrences (probably if only for beginner tutorials)
        mReplace.put("giveCTS", "giveCTS"); //todo:: modify this argument given
        mReplace.put("giveExp", "UserIncEXP"); 
        mReplace.put("giveItem", "InventoryItemExchange");
        mReplace.put("giveMesos", "UserIncMoney");
        mReplace.put("giveSkill", "UserLearnSkill");
        mReplace.put("golluxPortalOpen", "FieldGolluxPortalEnable");
        mReplace.put("hasCTS", "hasCTS"); //todo:: modify this argument given
        mReplace.put("hasHadQuest", "IsQuestActive");
        mReplace.put("hasItem", "InventoryIsHoldingItemCount");
        mReplace.put("hasMobsInField", "FieldMobExists");
        mReplace.put("hasQuest", "IsQuestInProgress");
        mReplace.put("hasQuestCompleted", "IsQuestComplete");
        mReplace.put("hasQuestWithValue", "QuestRecordExExists");
        mReplace.put("hasSkill", "UserSkillIsLearned");
        mReplace.put("hasTutor", "hasTutor"); //todo:: figure out wtf tutor is
        mReplace.put("heal", "UserHealMax");
        mReplace.put("hideNpcByTemplateId", "FieldNpcViewOrHide");
        mReplace.put("hideUser(", "OnUserInGameDirectionEvent(InGameDirectionEvent.VansheeMode, ");
        mReplace.put("hireTutor", "UserHireTutor");
        mReplace.put("increaseReactorState", "FieldSetReactorState");
        mReplace.put("isAbleToLevelUpMakingSkill", "isAbleToLevelUpMakingSkill"); //todo::
        mReplace.put("isEquipped", "InventoryIsEquippedItem");
        mReplace.put("isFinishedEscort", "isFinishedEscort"); //todo:: remove usages of this, looks custom
        mReplace.put("isPartyLeader", "UserIsPartyBoss");
        mReplace.put("jobAdvance", "jobAdvance"); //todo:: remove occurrences of this
        mReplace.put("killMobs", "FieldRemoveAllMob");
        mReplace.put("killmobs", "FieldRemoveAllMob");
        mReplace.put("levelUntil", "UserIncLevelSet");
        mReplace.put("localEmotion", "UserLocalEmotion");
        mReplace.put("lockInGameUI", "OnSetInGameDirectionMode");
        mReplace.put("moveCameraBack(", "OnUserInGameDirectionEvent(InGameDirectionEvent.CameraMove, true, ");
        mReplace.put("moveLayer", "EffectOnOffLayer");
        mReplace.put("moveNpcByTemplateId", "OnForceMoveByScript"); //todo:: verify that the arguments passed correspond
        mReplace.put("moveParticleEff", "UserMoveParticleEff");
        mReplace.put("offSpineScreen", "");

        mReplace.put("moveCamera(", "OnUserInGameDirectionEvent(InGameDirectionEvent.CameraMove, ");
        mReplace.put("offLayer", "EffectOffLayer");
        mReplace.put("onLayer", "EffectOnLayer");
        mReplace.put("playSound", "EffectSound");
        mReplace.put("removeAdditionalEffect(", "OnUserInGameDirectionEvent(InGameDirectionEvent.RemoveAdditionalEffect");
        mReplace.put("removeOverlapScreen", "FieldEffectRemoveOverlapDetail");
        mReplace.put("reservedEffectRepeat", "EffectReservedRepeat");
        mReplace.put("sendDelay(", "OnUserInGameDirectionEvent(InGameDirectionEvent.Delay, ");
        mReplace.put("sendSay", "Say");
        mReplace.put("sendNext", "SayNext");
        mReplace.put("showNpcSpecialActionByTemplateId", "OnNpcSpecialAction");
        mReplace.put("showEffect(", "OnUserInGameDirectionEvent(InGameDirectionEvent.EffectPlay, ");
        mReplace.put("showFadeTransition", "FieldEffectOverlapDetail");
        mReplace.put("sm.", "self.");
        mReplace.put("spawnNpc", "FieldSummonNpc");
        mReplace.put("str(", "(");
        mReplace.put("True", "true");
        mReplace.put("warp", "RegisterTransferField");
        mReplace.put("zoomCamera(", "OnUserInGameDirectionEvent(InGameDirectionEvent.CameraZoom, ");
        
        mChatTypeAppend.put("oxChatPlayerAsSpeaker", "User");
        mChatTypeAppend.put("flipBoxChat", "Flip");
        mChatTypeAppend.put("flipDialogue", "flipDialogue"); //todo:: this one looks stupid
        mChatTypeAppend.put("flipDialoguePlayerAsSpeaker", "flipDialoguePlayerAsSpeaker"); //todo:: this one also looks stupid
        mChatTypeAppend.put("flipNpcByTemplateId", "flipNpcByTemplateId"); //todo:: looks mega stupid
        mChatTypeAppend.put("flipSpeaker", "flipSpeaker"); //todo:: looks ultra stupid
        mChatTypeAppend.put("NoEscape", "NoESC");
        mChatTypeAppend.put("addEscapeButton", "NoESC");
        
        mFuncArgsAppend.put("addAP", ", true");
        mFuncArgsAppend.put("addLevel", ", true");
        mFuncArgsAppend.put("addMaxHP", ", true");
        mFuncArgsAppend.put("addMaxMP", ", true");
        mFuncArgsAppend.put("addSP", ", true");
        mFuncArgsAppend.put("addSp", ", true");
        mFuncArgsAppend.put("giveExp", ", true");
        mFuncArgsAppend.put("giveMesos", ", true");
        
        mFuncAppend.put("InGameDirectionEvent.Delay", "self.Wait();");
    }
}
