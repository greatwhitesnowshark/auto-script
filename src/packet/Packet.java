/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet;

import script.ScriptModifier;
import script.ScriptWriteRequest;

/**
 *
 * @author Sharky
 */
public abstract class Packet {
    
    private final int nHeader;
    public abstract ScriptModifier CreateScriptModifier();
    public abstract ScriptModifier CreateScriptModifierOnEnd();
    public abstract ScriptModifier CreateScriptModifierOnInput();
    public abstract ScriptModifier CreateScriptModifierOnMerge();
    public abstract ScriptWriteRequest CreateScriptWriteRequest();
    
    public Packet(int nHeader) {
        this.nHeader = nHeader;
    }
    
    public int GetHeader() {
        return this.nHeader;
    }
}
