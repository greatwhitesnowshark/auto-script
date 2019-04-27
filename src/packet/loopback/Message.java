/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import java.util.LinkedList;
import packet.LoopbackCode;
import packet.PacketWriteRequest;
import script.Script;
import script.ScriptModifier;
import script.ScriptWriteRequest;

/**
 *
 * @author Sharky
 */
public class Message extends PacketWriteRequest {
    
    private final int nType, nQuestID, nQuestRecordStatus;
    private final long tTimestamp;
    private final boolean bAutoComplete;
    private final String sRecord;
    
    public Message(int nType, int nQuestID, int nQuestRecordStatus, String sRecord, boolean bAutoComplete, long tTimestamp) {
        super(LoopbackCode.Message.nCode);
        this.nType = nType;
        this.nQuestID = nQuestID;
        this.nQuestRecordStatus = nQuestRecordStatus;
        this.sRecord = sRecord;
        this.bAutoComplete = bAutoComplete;
        this.tTimestamp = tTimestamp;
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
            if (pScriptCopy.pTemplate != null && pScriptCopy.pTemplate.IsQuestTemplate()) {
                if (pScriptCopy.pTemplate.dwTemplateID == nQuestID) {
                    dwField = pScriptCopy.dwField;
                    pTemplate = pScriptCopy.pTemplate;
                    nStrPaddingIndex = pScriptCopy.GetStrPaddingIndex();
                }
            }
        };
        return pScriptModifier;
    }

    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        if (pTemplate != null) {
            String sOutput = "self.";
            switch (nQuestRecordStatus) {
                case 0://None
                    sOutput += ("QuestRecordSetState(" + nQuestID + ", QuestRecord.None);");
                    break;
                case 1://Perform
                    if (!sRecord.isEmpty()) {
                        sOutput += ("QuestRecordSetInfo(" + nQuestID + ", \"" + sRecord + "\"); \r\n");
                        sOutput += "self.";
                    }
                    sOutput += ("QuestRecordSetState(" + nQuestID + ", QuestRecord.Perform);");
                    break;
                case 2://Complete
                    sOutput += ("QuestRecordSetState(" + nQuestID + ", QuestRecord.Complete);");
                    break;
            }
            return new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return null;
    }
    
}
