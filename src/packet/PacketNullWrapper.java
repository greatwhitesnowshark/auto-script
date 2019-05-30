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
public class PacketNullWrapper extends PacketWrapper {

    public PacketNullWrapper(int nHeader) {
        super(nHeader);
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
        return null;
    }

    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        return null;
    }
    
}
