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
 * @author Five
 */
public class UserSetInGameDirectionMode extends PacketWriteRequest {
    
    private final boolean bInGameDirectionMode, bBlackFrame, bForceMouseOver, bShowUI;
    
    public UserSetInGameDirectionMode(boolean bInGameDirectionMode, boolean bBlackFrame, boolean bForceMouseOver, boolean bShowUI) {
        super(LoopbackCode.UserSetInGameDirectionMode.nCode);
        this.bInGameDirectionMode = bInGameDirectionMode;
        this.bBlackFrame = bBlackFrame;
        this.bForceMouseOver = bForceMouseOver;
        this.bShowUI = bShowUI;
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
            String sOutput = ("self.OnSetInGameDirectionMode(" + (bInGameDirectionMode ? "true" : "false") + ", " + (bBlackFrame ? "true" : "false") + ", " + (bForceMouseOver ? "true" : "false") + ", " + (bShowUI ? "true" : "false") + ");");
            return new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return null;
    }
}
