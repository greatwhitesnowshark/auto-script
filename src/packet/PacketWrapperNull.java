/*
 * To change this license opcode, choose License Headers in Project Properties.
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
public class PacketWrapperNull extends PacketWrapper {

    public PacketWrapperNull(int nHeader) {
        super(nHeader);
    }

    @Override
    public boolean IsScriptResetNotPersist() { return false; }

    @Override
    public ScriptModifier CreateNewScriptTemplate() {
        return null;
    }

    @Override
    public ScriptModifier CreateNewScriptTemplateResetNotPersist() { return null; }

    @Override
    public ScriptModifier CreateScriptTemplateCopy() {
        return null;
    }

    @Override
    public ScriptModifier SetScriptUserInputResult() { return null; }

    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() { return null; }

}
