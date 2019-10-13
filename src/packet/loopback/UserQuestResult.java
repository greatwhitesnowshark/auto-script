    /*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import game.network.InPacket;
import game.scripting.ScriptSysFunc.QuestResultType;
import game.user.quest.QuestResult;
import java.util.LinkedList;
import packet.opcode.LoopbackCode;
import packet.PacketWriteRequest;
import script.Script;
import script.ScriptModifier;
import script.ScriptWriteRequest;

/**
 *
 * @author Sharky
 */
public class UserQuestResult extends PacketWriteRequest {
    
    public boolean bNavigation, bQuestName;
    public byte nResult;
    public int nQuestID, dwNpcTemplateID, nNextQuestID;
    public int[] aTimerQuestID, aTimeRemaining;
    
    public UserQuestResult(InPacket iPacket) {
        super(LoopbackCode.UserQuestResult.nCode);
        this.nResult = iPacket.DecodeByte();
        switch (nResult) {
            case QuestResult.Success:
                this.nQuestID = iPacket.DecodeInt();
                this.dwNpcTemplateID = iPacket.DecodeInt();
                this.nNextQuestID = iPacket.DecodeInt();
                this.bNavigation = iPacket.DecodeBool();
                break;
            case QuestResult.FailedInventory:
                this.nQuestID = iPacket.DecodeInt();
                this.bQuestName = iPacket.DecodeBool();
                break;
            case QuestResult.StartQuestTimer:
            case QuestResult.StartTimeKeepQuestTimer:
                short nSize = iPacket.DecodeShort();
                this.aTimeRemaining = new int[nSize];
                this.aTimerQuestID = new int[nSize];
                for (int i = 0; i < nSize; i++) {
                    this.aTimerQuestID[i] = iPacket.DecodeInt();
                    this.aTimeRemaining[i] = iPacket.DecodeInt();
                }
                break;
            case QuestResult.EndQuestTimer:
            case QuestResult.EndTimeKeepQuestTimer:
                nSize = iPacket.DecodeShort();
                this.aTimerQuestID = new int[nSize];
                for (int i = 0; i < nSize; i++) {
                    this.aTimerQuestID[i] = iPacket.DecodeInt();
                }
                break;
            case QuestResult.FailedTimeOver:
            case QuestResult.ResetQuestTimer:
                this.nQuestID = iPacket.DecodeInt();
                break;
        }
    }

    //todo:: Consider whether or not this makes sense. Because really, what we need to worry about are the ScriptMessages.. right?

    //todo:: create a CreateNewScriptTemplate() override here so that we can verify if:
    //todo:: 1) the pTemplate is a FieldScript-FieldScriptType or null, so that we can create a new Quest-Template
    //todo:: 2) We should be able to use the Result byte to infer which quest-file to write to 'start' or 'end'

    @Override
    public ScriptModifier CreateScriptTemplateCopy() {
        ScriptModifier pScriptModifier = (Script pScriptCopy) -> {
            if (pScriptCopy.pTemplate != null) {
                dwField = pScriptCopy.dwField;
                pHistory = pScriptCopy.pHistory;
                pTemplate = pScriptCopy.pTemplate;
                nStrPaddingIndex = pScriptCopy.CurrentLinePadding();
            }
        };
        return pScriptModifier;
    }
    
    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        if (pTemplate != null) {
            String sOutput = "", sTimerQuestID;
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
