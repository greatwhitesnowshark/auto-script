/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.client;

import game.network.InPacket;
import game.scripting.ScriptSysFunc;
import packet.opcode.ClientCode;
import packet.PacketWrapperNull;
import script.Script;
import script.ScriptModifier;
import script.ScriptTemplateMap;
import script.ScriptWriteRequest;
import template.QuestEndTemplate;
import template.QuestStartTemplate;
import template.QuestTemplate;
import scriptmaker.ScriptMakerConfig;
import util.Logger;

/**
 *
 * @author Sharky
 */
public class UserQuestRequest extends PacketWrapperNull {
    
    public final int nQuestState, nQuestID;
    public final boolean bOpening, bComplete;
    
    public UserQuestRequest(InPacket iPacket) {
        super(ClientCode.UserQuestRequest.nCode);
        this.nQuestState = iPacket.DecodeByte();
        this.nQuestID = iPacket.DecodeInt();
        this.bOpening = nQuestState == ScriptSysFunc.QuestRequestType.OpeningScript;
        this.bComplete = nQuestState == ScriptSysFunc.QuestRequestType.CompleteScript;
        if (ScriptMakerConfig.OnPacketUserQuestRequestDebug) {
            if (this.bOpening || this.bComplete) {
                QuestTemplate pQuestTemplate = this.bOpening ? ScriptTemplateMap.GetQuestStartTemplate(this.nQuestID) : ScriptTemplateMap.GetQuestEndTemplate(this.nQuestID);
                if (pQuestTemplate != null) {
                    String sQuestName = pQuestTemplate.sQuestName;
                    Logger.LogReport("UserQuestRequest:  Request-Type: [%s],  Name: [%s]  Quest-ID: [%d]", (this.bOpening ? "OpeningScript" : "CompleteScript"), sQuestName, this.nQuestID);
                } else {
                    Logger.LogError("UserQuestRequest:-  no script template could be found for Quest-ID [%d],  (State: [%s])", this.nQuestID, (this.bOpening ? "OpeningScript" : "CompleteScript"));
                }
            }
        }
    }

    @Override
    public ScriptModifier SetScriptUserInputResult() {
        ScriptModifier pScriptModifier = (Script pScriptMod) -> {
            if (pScriptMod.pTemplate != null) {
                if (pScriptMod.GetNestedBlockHistory() != null) {
                    String sResult = bOpening ? "QuestMessageType.OpeningScript" : bComplete ? "QuestMessageType.CompleteScript" : "";
                    if (!sResult.isEmpty()) {
                        pScriptMod.GetNestedBlockHistory().SetNestedBlockResult(sResult);
                    }
                }
            }
        };
        return pScriptModifier;
    }
    
    @Override
    public ScriptModifier CreateNewScriptTemplate() {
        ScriptModifier pScriptModifier = (Script pScript) -> {
            if (bOpening) {
                QuestStartTemplate pQuestTemplate = ScriptTemplateMap.GetQuestStartTemplate(nQuestID);
                if (pQuestTemplate != null) {
                    pQuestTemplate.nQuestState = nQuestState;
                    pScript.CreateNewTemplate(null);
                    pScript.CreateNewTemplate(new ScriptWriteRequest(pScript.dwField, pQuestTemplate));
                }
            } else if (bComplete) {
                QuestEndTemplate pQuestTemplate = ScriptTemplateMap.GetQuestEndTemplate(nQuestID);
                if (pQuestTemplate != null) {
                    pQuestTemplate.nQuestState = nQuestState;
                    pScript.CreateNewTemplate(new ScriptWriteRequest(pScript.dwField, pQuestTemplate), true);
                }
            }
        };
        return pScriptModifier;
    }
}
