/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.client;

import packet.ClientCode;
import packet.Packet;
import script.Script;
import script.ScriptModifier;
import script.ScriptTemplateMap;
import script.ScriptWriteRequest;
import template.QuestEndTemplate;
import template.QuestStartTemplate;
import util.Logger;

/**
 *
 * @author Five
 */
public class UserQuestRequest extends Packet {
    
    private final int nQuestState, nQuestID;
    private final boolean bOpening, bComplete;
    
    public UserQuestRequest(int nQuestState, int nQuestID, boolean bOpening, boolean bComplete) {
        super(ClientCode.UserQuestRequest.nCode);
        this.nQuestState = nQuestState;
        this.nQuestID = nQuestID;
        this.bOpening = bOpening;
        this.bComplete = bComplete;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnEnd() {
        return null;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnInput() {
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
    public ScriptModifier CreateScriptModifierOnMerge() {
        return null;
    }

    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        return null;
    }
    
    @Override
    public ScriptModifier CreateScriptModifier() {
        ScriptModifier pScriptModifier = (Script pScript) -> {
            if (bOpening) {
                QuestStartTemplate pQuestTemplate = ScriptTemplateMap.GetQuestStartTemplate(nQuestID);
                if (pQuestTemplate != null) {
                    Logger.LogError("pQuestStartTemplate found - " + pQuestTemplate.sQuestName + " / " + pQuestTemplate.sScript);
                    pQuestTemplate.nQuestState = nQuestState;
                    pScript.CreateNewTemplate(null);
                    pScript.CreateNewTemplate(new ScriptWriteRequest(pScript.dwField, pQuestTemplate));
                }
            } else {
                if (bComplete) {
                    QuestEndTemplate pQuestTemplate = ScriptTemplateMap.GetQuestEndTemplate(nQuestID);
                    if (pQuestTemplate != null) {
                        Logger.LogError("pQuestEndTemplate found - " + pQuestTemplate.sQuestName + " / " + pQuestTemplate.sScript);
                        pQuestTemplate.nQuestState = nQuestState;
                        pScript.CreateNewTemplate(new ScriptWriteRequest(pScript.dwField, pQuestTemplate), true);
                    }
                }
            }
        };
        return pScriptModifier;
    }
}
