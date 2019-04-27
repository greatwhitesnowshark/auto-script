/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import java.util.LinkedList;
import message.InGameDirectionEventType;
import packet.LoopbackCode;
import packet.PacketWriteRequest;
import script.Script;
import script.ScriptModifier;
import script.ScriptWriteRequest;

/**
 *
 * @author Five
 */
public class UserInGameDirectionEvent extends PacketWriteRequest {
    
    private boolean bNotOrigin = false, bBack = false, bVanshee = false, bMonologueEnd = false, bStayModal = false;
    private byte nUIType = 0;
    private short nAlign = 0;
    private int nType;
    private int nAction, tDuration, tDelay, x, y, v17, dwNpcID, nForcedInput, nAct, nRequestCount, nTime, nPixelPerSec, nScale, nTimePos,
            nFaceItemID, nUpdateSpeedTime, nDecTic, nDir, nSpeed, nForcedFlip;
    private int[] aItem = new int[0];
    private String sType, sEffectUOL = "", sPattern = "", sMonologue = "";
    
    public UserInGameDirectionEvent(int nType) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = nType;
        this.sType = InGameDirectionEventType.GetName(nType);
    }
    
    public UserInGameDirectionEvent(int nType, boolean bVanshee) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = nType;
        this.sType = InGameDirectionEventType.GetName(nType);
        switch (nType) {
            case InGameDirectionEventType.VansheeMode:
                this.bVanshee = bVanshee;
                break;
        }
    }
    
    public UserInGameDirectionEvent(int nType, byte nUIType) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = nType;
        this.sType = InGameDirectionEventType.GetName(nType);
        switch (nType) {
            case InGameDirectionEventType.InputUI:
                this.nUIType = nUIType;
                break;
        }
    }
    
    public UserInGameDirectionEvent(int nType, int nVal) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = nType;
        this.sType = InGameDirectionEventType.GetName(nType);
        switch (nType) {
            case InGameDirectionEventType.Delay:
                this.tDelay = nVal;
                break;
            case InGameDirectionEventType.ForcedInput:
                this.nForcedInput = nVal;
                break;
            case InGameDirectionEventType.CameraOnCharacter:
                this.dwNpcID = nVal;
                break;
            case InGameDirectionEventType.FaceOff:
                this.nFaceItemID = nVal;
                break;
            case InGameDirectionEventType.ForcedFlip:
                this.nForcedFlip = nVal;
                break;
        }
    }
    
    public UserInGameDirectionEvent(int nType, int nVal, int nVal2) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = nType;
        this.sType = InGameDirectionEventType.GetName(nType);
        switch (nType) {
            case InGameDirectionEventType.ForcedAction:
                this.nAction = nVal;
                this.tDuration = nVal2;
                break;
            case InGameDirectionEventType.ForcedMove:
                this.nDir = nVal;
                this.nSpeed = nVal2;
                break;
        }
    }
    
    public UserInGameDirectionEvent(int nType, boolean bBack, int nPixelPerSec) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = nType;
        this.sType = InGameDirectionEventType.GetName(nType);
        switch (nType) {
            case InGameDirectionEventType.CameraMove:
                this.bBack = bBack;
                this.nPixelPerSec = nPixelPerSec;
                break;
        }
    }
    
    public UserInGameDirectionEvent(int nType, boolean bBack, int nPixelPerSec, int x, int y) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = nType;
        this.sType = InGameDirectionEventType.GetName(nType);
        switch (nType) {
            case InGameDirectionEventType.CameraMove:
                this.bBack = bBack;
                this.nPixelPerSec = nPixelPerSec;
                this.x = x;
                this.y = y;
                break;
        }
    }
    
    public UserInGameDirectionEvent(int nType, int nTime, int nScale, int nTimePos, int x, int y) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = nType;
        this.sType = InGameDirectionEventType.GetName(nType);
        switch (nType) {
            case InGameDirectionEventType.CameraZoom:
                this.nTime = nTime;
                this.nScale = nScale;
                this.nTimePos = nTimePos;
                this.x = x;
                this.y = y;
                break;
        }
    }
    
    public UserInGameDirectionEvent(int nType, String sMonologue, boolean bMonologueEnd) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = nType;
        this.sType = InGameDirectionEventType.GetName(nType);
        switch (nType) {
            case InGameDirectionEventType.Monologue:
                this.sMonologue = sMonologue;
                this.bMonologueEnd = bMonologueEnd;
                break;
        }
    }
    
    public UserInGameDirectionEvent(int nType, String sMonologue, boolean bStayModal, short nAlign, int nUpdateSpeedTime, int nDecTic) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = nType;
        this.sType = InGameDirectionEventType.GetName(nType);
        switch (nType) {
            case InGameDirectionEventType.MonologueScroll:
                this.sMonologue = sMonologue;
                this.bStayModal = bStayModal;
                this.nAlign = nAlign;
                this.nUpdateSpeedTime = nUpdateSpeedTime;
                this.nDecTic = nDecTic;
                break;
        }
    }
    
    public UserInGameDirectionEvent(int nType, String sPattern, int nAct, int nRequestCount, int nTime) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = nType;
        this.sType = InGameDirectionEventType.GetName(nType);
        switch (nType) {
            case InGameDirectionEventType.PatternInputRequest:
                this.sPattern = sPattern;
                this.nAct = nAct;
                this.nRequestCount = nRequestCount;
                this.nTime = nTime;
                break;
        }
    }
    
    public UserInGameDirectionEvent(int nType, String sEffectUOL, int tDuration, int x, int y, int v17, int dwNpcID, boolean bNotOrigin) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = nType;
        this.sType = InGameDirectionEventType.GetName(nType);
        switch (nType) {
            case InGameDirectionEventType.EffectPlay:
                this.sEffectUOL = sEffectUOL;
                this.tDuration = tDuration;
                this.x = x;
                this.y = y;
                this.v17 = v17;
                this.dwNpcID = dwNpcID;
                this.bNotOrigin = bNotOrigin;
                break;
        }
    }
    
    public UserInGameDirectionEvent(int nType, int[] aItem) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = nType;
        this.sType = InGameDirectionEventType.GetName(nType);
        switch (nType) {
            case InGameDirectionEventType.AvatarLookSet:
                this.aItem = aItem;
                break;
        }
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
            String sOutput = "self.OnUserInGameDirectionEventType(InGameDirectionEventType." + sType + ", ";
            switch (nType) {
                case InGameDirectionEventType.ForcedAction:
                    sOutput += (nAction + ", " + tDuration + ");");
                    break;
                case InGameDirectionEventType.Delay:
                    sOutput += (tDelay + ");");
                    break;
                case InGameDirectionEventType.EffectPlay:
                    sOutput += ("\"" + sEffectUOL + "\", " + tDuration + ", " + x + ", " + y + ", " + v17 + ", " + dwNpcID + ", " + (bNotOrigin ? "true" : "false") + ");");
                    break;
                case InGameDirectionEventType.ForcedInput:
                    sOutput += (nForcedInput + ");");
                    break;
                case InGameDirectionEventType.PatternInputRequest:
                    sOutput += ("\"" + sPattern + "\", " + nAct + ", " + nRequestCount + ", " + nTime + ");");
                    break;
                case InGameDirectionEventType.CameraMove:
                    if (!bBack) {
                        sOutput += ("false, " + nPixelPerSec + ", " + x + ", " + y + ");");
                    } else {
                        sOutput += ("true, " + nPixelPerSec + ");");
                    }
                    break;
                case InGameDirectionEventType.CameraOnCharacter:
                    sOutput += (dwNpcID + ");");
                    break;
                case InGameDirectionEventType.CameraZoom:
                    sOutput += (nTime + ", " + nScale + ", " + nTimePos + ", " + x + ", " + y + ");");
                    break;
                case InGameDirectionEventType.CameraReleaseFromUserPoint:
                case InGameDirectionEventType.RemoveAdditionalEffect:
                case InGameDirectionEventType.CloseUI:
                    sOutput += (");");
                    sOutput = sOutput.replace(", );", ");");
                    break;
                case InGameDirectionEventType.VansheeMode:
                    sOutput += ((bVanshee ? "true);" : "false);"));
                    break;
                case InGameDirectionEventType.FaceOff:
                    sOutput += (nFaceItemID + ");");
                    break;
                case InGameDirectionEventType.Monologue:
                    sOutput += ("\"" + sMonologue + "\", " + (bMonologueEnd ? "true" : "false") + ");");
                    break;
                case InGameDirectionEventType.MonologueScroll:
                    sOutput += ("\"" + sMonologue + "\", " + (bStayModal ? "true" : "false") + ", " + nAlign + ", " + nUpdateSpeedTime + ", " + nDecTic + ");");
                    break;
                case InGameDirectionEventType.AvatarLookSet:
                    for (int i = 0; i < aItem.length; i++) {
                        sOutput += aItem[i];
                        sOutput += (i != (aItem.length - 1)) ? ", " : ");";
                    }
                    break;
                case InGameDirectionEventType.ForcedMove:
                    sOutput += (nDir + ", " + nSpeed + ");");
                    break;
                case InGameDirectionEventType.ForcedFlip:
                    sOutput += (nForcedFlip + ");");
                    break;
                case InGameDirectionEventType.InputUI:
                    sOutput += (nUIType + ");");
                    break;
            }
            return new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return null;
    }
}
