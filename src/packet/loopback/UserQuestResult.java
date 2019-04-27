/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import java.util.LinkedList;
import message.QuestResultType;
import packet.LoopbackCode;
import packet.PacketWriteRequest;
import script.Script;
import script.ScriptModifier;
import script.ScriptWriteRequest;

/**
 *
 * @author Five
 */
public class UserQuestResult extends PacketWriteRequest {
    
    private final boolean bNavigation, bQuestName;
    private final byte nResult;
    private final int nQuestID, dwNpcTemplateID, nNextQuestID;
    private final int[] aTimerQuestID, aTimeRemaining;
    
    public UserQuestResult(byte nResult, int nQuestID, int dwNpcTemplateID, int nNextQuestID, boolean bNavigation, boolean bQuestName, int[] aTimerQuestID, int[] aTimeRemaining) {
        super(LoopbackCode.UserQuestResult.nCode);
        this.nResult = nResult;
        this.nQuestID = nQuestID;
        this.dwNpcTemplateID = dwNpcTemplateID;
        this.nNextQuestID = nNextQuestID;
        this.bNavigation = bNavigation;
        this.bQuestName = bQuestName;
        this.aTimerQuestID = aTimerQuestID;
        this.aTimeRemaining = aTimeRemaining;
    }

    @Override
    public ScriptModifier CreateScriptModifier() {
        return null;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnEnd() {
        return null;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnInput() {
        return null;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnMerge() {
        ScriptModifier pScriptModifier = (Script pScriptCopy) -> {
            if (pScriptCopy.pTemplate != null) {
                dwField = pScriptCopy.dwField;
                pTemplate = pScriptCopy.pTemplate;
                nStrPaddingIndex = pScriptCopy.GetStrPaddingIndex();
            }
        };
        return pScriptModifier;
    }
    
    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        if (pTemplate != null) {
            String sOutput = "", sTimerQuestID = "";
            switch (nResult) {
                    case QuestResultType.Success:
                        sOutput = "self.OnUserQuestResult(QuestResultType.Success, " + nQuestID + ", " + dwNpcTemplateID + ", " + nNextQuestID + ", " + bNavigation + ");";
                        break;
                    case QuestResultType.FailedInventory:
                        sOutput = "self.OnUserQuestResult(QuestResultType.FailedInventory, " + nQuestID + ", " + bQuestName + ");";
                        break;
                    case QuestResultType.StartQuestTimer:
                    case QuestResultType.StartTimeKeepQuestTimer:
                        String aTimerRemain = "aTimeRemain = [";
                        sTimerQuestID = "sTimerQuestID = [";
                        for (int i = 0; i < aTimerQuestID.length; i++) {
                            if (i > 0) {
                                sTimerQuestID += ", ";
                                aTimerRemain += ", ";
                            }
                            sTimerQuestID += ("" + aTimerQuestID[i]);
                            aTimerRemain += ("" + aTimeRemaining[i]);
                        }
                        sTimerQuestID += "];\r\n";
                        aTimerRemain += "];\r\n";
                        String sRootText = "self.OnUserQuestResult(" + (nResult == QuestResultType.StartQuestTimer ? "QuestResultType.StartQuestTimer" : "QuestResultType.StartTimeKeepQuestTimer");
                        if (sTimerQuestID.equals("sTimerQuestID = [];\r\n") && aTimerRemain.equals("aTimerRemain = [];\r\n")) {
                            sRootText += ");";
                        } else {
                            if (!sTimerQuestID.equals("sTimerQuestID = [];\r\n")) {
                                sOutput += sTimerQuestID;
                                sRootText += ", sTimerQuestID";
                            } else {
                                sRootText += ", null";
                            }
                            if (!aTimerRemain.equals("aTimerRemain = [];\r\n")) {
                                sOutput += aTimerRemain;
                                sRootText += ", aTimerRemain);";
                            } else {
                                sRootText += ", null);";
                            }
                        }
                        sOutput += sRootText;
                        break;
                    case QuestResultType.EndQuestTimer:
                    case QuestResultType.EndTimeKeepQuestTimer:
                        sTimerQuestID = "sTimerQuestID = [";
                        for (int i = 0; i < aTimerQuestID.length; i++) {
                            if (i > 0) {
                                sTimerQuestID += ", ";
                            }
                            sTimerQuestID += ("" + aTimerQuestID[i]);
                        }
                        sTimerQuestID += "];\r\n";
                        if (!sTimerQuestID.equals("sTimerQuestID = [];\r\n")) {
                            sRootText = "self.OnUserQuestResult(" + (nResult == QuestResultType.EndQuestTimer ? "QuestResultType.EndQuestTimer" : "QuestResultType.EndTimeKeepQuestTimer") + ", sTimerQuestID);";
                            sOutput = sTimerQuestID + sRootText;
                        } else {
                            sOutput = "self.OnUserQuestResult(" + (nResult == QuestResultType.EndQuestTimer ? "QuestResultType.EndQuestTimer" : "QuestResultType.EndTimeKeepQuestTimer") + ", null);";
                        }
                        break;
                    case QuestResultType.FailedTimeOver:
                    case QuestResultType.ResetQuestTimer:
                        sOutput = "self.OnUserQuestResult(" + (nResult == QuestResultType.FailedTimeOver ? "QuestResultType.FailedTimeOver" : "QuestResultType.ResetQuestTimer") + ", " + nQuestID + ");";
                        break;
            }
            return new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return null;
    }
}
