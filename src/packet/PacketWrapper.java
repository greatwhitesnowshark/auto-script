/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet;

import game.network.InPacket;
import script.ScriptModifier;
import script.ScriptWriteRequest;

import java.io.IOException;

/**
 *
 * @author Sharky
 */
public abstract class PacketWrapper {
    
    public final int nCode;


    /**
     * This will find the appropriate values to set to a new Script template, reset pScript's values accordingly -
     * and will then execute pScript.CreateNewScript(...)
     *
     * @return A Script AbstractHandler that will execute CreateNewScript(...) on the given pScript object or,
     *         NULL - if it is going to use the current template attached to pScript
     */
    public abstract ScriptModifier CreateNewScriptTemplate();


    /**
     * @see CreateNewScriptTemplate() - After pScript's template values have been set:
     *
     * This will find and store the needed values from pScript's template into the PacketWriteRequest -
     * these values will be used by the PacketWriteRequest to create appropriate output to the script
     *
     * @return A Script AbstractHandler that will share information from pScript's template to this PacketWriteRequest object or,
     *         NULL - if pScript's template is newly created and/or this PacketWriteRequest already has the values needed for output
     */
    public abstract ScriptModifier CreateScriptTemplateCopy();


    /**
     * @see CreateScriptTemplateCopy() - After both pScript and the PacketWriteRequest object have matching templates w/ history:
     *
     * This will either:
     *      1) Insert result arguments into pScript's template history for conditional blocks (ex: `if (nSel == X) ...`), or
     *      2) Will execute an independent ScriptWriteRequest to pScript, in order to insert an action (ex: self.Wait() ...)
     *
     * @return A Script AbstractHandler that will insert conditional branch arguments to direct output control-flow for this PacketWriteRequest, or
     *         NULL - if the packet received has no arguments or actions to be inserted
     */
    public abstract ScriptModifier SetScriptUserInputResult();


    /**
     * This will create the output line(s) to be inserted into the designated file
     *
     * @return A ScriptWriteRequest containing the formed output line(s) to insert and history arguments to locate file index for position
     */
    public abstract ScriptWriteRequest CreateScriptWriteRequest();


    /**
     * @see IsScriptResetNotPersist() - Must be true for this to override pScript's template after the PacketWriteRequest is sent
     *
     * This will set pScript's template to the next assumed template's value - in the case of field scripts, it ensures that there
     * will be an active/valid template attached to pScript to output any monitored packet action directly after SetField
     *
     * @return A Script AbstractHandler that will reset/queue a new script template, but after this PacketWriteRequest execution is done, or
     *         NULL - if a new script should not be created after this PacketWriteRequest execution
     */
    public abstract ScriptModifier CreateNewScriptTemplateResetNotPersist();


    /**
     * @see CreateNewTemplateResetNotPersist() - If this returns true; a subsequent method to set the next script template
     *
     * This will reset a script template's values if the packet is determined to indicate the closing of a script
     *
     * @return True - if pScript's template should be reset to its default values after the next PacketWriteRequest is sent, or
     *         False - if pScript's template is to persist after the next PacketWriteRequest is sent
     */
    public abstract boolean IsScriptResetNotPersist();

    
    public PacketWrapper(int nCode) {
        this.nCode = nCode;
    }


    public interface OnPacket {
        PacketWrapper ReadPacket(InPacket iPacket) throws IOException;
    }
}
