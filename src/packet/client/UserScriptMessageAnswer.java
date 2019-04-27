/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.client;

import java.util.LinkedList;
import message.MessageType;
import packet.ClientCode;
import packet.Packet;
import script.MessageHistory;
import script.NestedBlockHistory;
import script.Script;
import script.ScriptModifier;
import script.ScriptWriteRequest;
import util.Logger;

/**
 *
 * @author Five
 */
public class UserScriptMessageAnswer extends Packet {
    
    private final int nMsgTypeInput, nModeInput, nSelectionInput;
    private ScriptWriteRequest pWriteRequestOverride = null;
    
    public UserScriptMessageAnswer(int nMsgTypeInput, int nModeInput, int nSelectionInput) {
        super(ClientCode.UserScriptMessageAnswer.nCode);
        this.nMsgTypeInput = nMsgTypeInput;
        this.nModeInput = nModeInput;
        this.nSelectionInput = nSelectionInput;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnEnd() {
        return null;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnInput() {
        ScriptModifier pScriptModifier = (Script pScriptMod) -> {
            if (nMsgTypeInput != MessageType.AskInGameDirection) {
                if (nModeInput >= 0) {
                    MessageHistory pMessageHistory = pScriptMod.GetMessageHistory();
                    NestedBlockHistory pNestedBlockHistory = pScriptMod.GetNestedBlockHistory();
                    if (pMessageHistory != null && pNestedBlockHistory != null) {
                        if (pMessageHistory.GetNestedBlockOutput().size() > 0) {
                            if (pMessageHistory.GetOutput().contains("nRet") || pMessageHistory.GetOutput().contains("nSel")) {
                                int nResult = pMessageHistory.GetOutput().contains("nSel") ? nSelectionInput : nModeInput;
                                pScriptMod.GetNestedBlockHistory().SetNestedBlockResult(nResult);
                                Logger.LogAdmin("SetNestedBlockResult(" + nResult + ") processed for pScriptMod.sScriptName = " + pScriptMod.sScriptName);
                            }
                        }
                    }
                }
            } else {
                pWriteRequestOverride = new ScriptWriteRequest(pScriptMod.dwField, "self.Wait();", pScriptMod.pTemplate, new LinkedList<>(), pScriptMod.GetStrPaddingIndex());
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
        return pWriteRequestOverride;
    }
    
    @Override
    public ScriptModifier CreateScriptModifier() {
        return null;
    }
}
