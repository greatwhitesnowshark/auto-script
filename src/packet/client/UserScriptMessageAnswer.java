/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.client;

import base.network.InPacket;
import game.scripting.ScriptMan;
import game.scripting.ScriptMan.MessageType;
import java.util.LinkedList;
import packet.opcode.ClientCode;
import packet.PacketWriteRequest;
import script.MessageHistory;
import script.NestedBlockHistory;
import script.Script;
import script.ScriptModifier;
import script.ScriptWriteRequest;
import scriptmaker.Config;
import util.Logger;

/**
 *
 * @author Sharky
 */
public class UserScriptMessageAnswer extends PacketWriteRequest {
    
    public int nMsgTypeInput, nModeInput, nSelectionInput;
    public boolean bResetNotPersist;
    
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
    public ScriptModifier SetScriptUserInputResult() {
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
                                if (Config.bMessageHistoryPrevLog) {
                                    Logger.LogAdmin("SetNestedBlockResult(" + nResult + ") processed for pScriptMod.sScriptName = " + pScriptMod.sScriptName);
                                }
                            }
                        }
                    }
                } else {
                    this.bResetNotPersist = true;
                }
            } else {
                if (!pHistory.GetOutput().contains("self.Wait();")) {
                    pScriptMod.ProcessWriteRequest(new ScriptWriteRequest(pScriptMod.dwField, "self.Wait();", pScriptMod.pTemplate, new LinkedList<>(), pScriptMod.CurrentLinePadding()));
                }
            }
        };
        return pScriptModifier;
    }

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
    public boolean IsScriptResetNotPersist() {
        return bResetNotPersist;
    }
}
