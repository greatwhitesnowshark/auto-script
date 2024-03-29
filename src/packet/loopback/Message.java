/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import base.network.InPacket;
import game.user.quest.QuestMan;
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
public class Message extends PacketWriteRequest {
    
    public int nType, nQuestID, nQuestRecordStatus;
    public long tTimestamp;
    public boolean bAutoComplete;
    public String sRecord;
    
    public Message(InPacket iPacket) {
        super(LoopbackCode.Message.nCode);
        this.nType = iPacket.DecodeByte();
        if (nType == 1) {//QuestRecord
            this.nQuestID = iPacket.DecodeInt();
            this.nQuestRecordStatus = iPacket.DecodeByte();
            this.sRecord = "";
            this.bAutoComplete = false;
            this.tTimestamp = 0;
            switch (nQuestRecordStatus) {
                case QuestMan.None:
                    this.bAutoComplete = iPacket.DecodeBool();
                    break;
                case QuestMan.Perform:
                    this.sRecord = iPacket.DecodeString();
                    break;
                case QuestMan.Complete:
                    this.tTimestamp = iPacket.DecodeLong();
                    break;
            }
        }
    }

    @Override
    public ScriptModifier CreateScriptTemplateCopy() {
        ScriptModifier pScriptModifier = (Script pScriptCopy) -> {
            if (pScriptCopy.pTemplate != null && pScriptCopy.pTemplate.IsQuestTemplate()) {
                if (pScriptCopy.pTemplate.dwTemplateID == nQuestID) {
                    dwField = pScriptCopy.dwField;
                    pTemplate = pScriptCopy.pTemplate;
                    pHistory = pScriptCopy.pHistory;
                    nStrPaddingIndex = pScriptCopy.CurrentLinePadding();
                }
            }
        };
        return pScriptModifier;
    }

    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        ScriptWriteRequest pWriteRequest = null;
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
            pWriteRequest = new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return pWriteRequest;
    }
}
