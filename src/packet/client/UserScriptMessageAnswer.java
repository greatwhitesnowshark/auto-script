/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.client;

import game.network.InPacket;
import game.scripting.ScriptMan;
import game.scripting.ScriptMan.MessageType;
import java.util.LinkedList;
import packet.ClientCode;
import packet.PacketWriteRequest;
import script.MessageHistory;
import script.NestedBlockHistory;
import script.Script;
import script.ScriptModifier;
import script.ScriptWriteRequest;
import util.Logger;

/**
 *
 * @author Sharky
 */
public class UserScriptMessageAnswer extends PacketWriteRequest {
    
    public int nMsgTypeInput, nModeInput, nSelectionInput;
    
    public UserScriptMessageAnswer(InPacket iPacket) {
        super(ClientCode.UserScriptMessageAnswer.nCode);
        this.nMsgTypeInput = iPacket.DecodeByte();
        if (this.nMsgTypeInput != ScriptMan.MessageType.AskInGameDirection) {
            this.nModeInput = iPacket.DecodeByte();
            if (iPacket.GetBufferLength() >= 2) {
                if (iPacket.GetBufferLength() >= 4 && this.nModeInput == 1) {
                    this.nSelectionInput = iPacket.DecodeInt();
                }
            }
        }
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
                                if (util.Config.MessageHistoryDebug) {
                                    Logger.LogAdmin("SetNestedBlockResult(" + nResult + ") processed for pScriptMod.sScriptName = " + pScriptMod.sScriptName);
                                }
                            }
                        }
                    }
                }
            } else {
                if (!pHistory.GetOutput().contains("self.Wait();")) {
                    pScriptMod.ProcessWriteRequest(new ScriptWriteRequest(pScriptMod.dwField, "self.Wait();", pScriptMod.pTemplate, new LinkedList<>(), pScriptMod.GetStrPaddingIndex()));
                }
            }
        };
        return pScriptModifier;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnMerge() {
        ScriptModifier pScriptModifier = (Script pScriptCopy) -> {
            if (pScriptCopy.pTemplate != null) {
                dwField = pScriptCopy.dwField;
                pHistory = pScriptCopy.pHistory;
                pTemplate = pScriptCopy.pTemplate;
                nStrPaddingIndex = pScriptCopy.GetStrPaddingIndex();
            }
        };
        return pScriptModifier;
    }
}
