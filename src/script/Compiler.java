/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import packet.PacketWrapper;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import packet.opcode.ClientCode;
import packet.opcode.LoopbackCode;
import template.AbstractTemplate;
import template.FieldTemplate;
import template.NpcTemplate;
import template.PortalTemplate;
import template.QuestEndTemplate;
import template.QuestStartTemplate;
import template.ReactorTemplate;

/**
 *
 * @author Sharky
 */
public class Compiler {

    private static final Map<Integer, Integer> mPacketParsedCount = new LinkedHashMap<>();
    private static final Map<Class<? extends AbstractTemplate>, LinkedList<String>> mScriptCompiledCount = new LinkedHashMap<>();
    private static Script pScript = new Script();
    private static Lock pLock = new ReentrantLock();

    public static void Compile(PacketWrapper pPacket) {
        pLock.lock();
        try {

            //Handles creating a new template before the write request
            ScriptModifier pScriptModifier = pPacket.CreateNewScriptTemplate();
            if (pScriptModifier != null) {
                pScriptModifier.SetScript(pScript); //This will set/reset the script's template, determining where to write to

                //Collects the total # and file names of the scripts compiled
                if (pScript.pTemplate != null) {
                    if (!mScriptCompiledCount.get(pScript.pTemplate.getClass()).contains(pScript.sFileName)) {
                        mScriptCompiledCount.get(pScript.pTemplate.getClass()).add(pScript.sFileName);
                    }
                }
            }

            //Tries to give pScript's template to the PacketWrapper
            pScriptModifier = pPacket.CreateScriptTemplateCopy();
            if (pScriptModifier != null) {
                pScriptModifier.SetScript(pScript);
            }

            //Tries to insert user-input arguments to pScript's nested-block-history, or executes an independent write request
            pScriptModifier = pPacket.SetScriptUserInputResult();
            if (pScriptModifier != null) {
                pScriptModifier.SetScript(pScript);
            }

            //Handles creating the write request and inserts output into the compiled script file
            ScriptWriteRequest pScriptWriteRequest = pPacket.CreateScriptWriteRequest();
            if (pScriptWriteRequest != null && pScriptWriteRequest.GetTemplate() != null) {
                pScript.ProcessWriteRequest(pScriptWriteRequest);
            }

            //Handles creating a new script template to be used after this write request is finished executing
            if (pPacket.IsScriptResetNotPersist()) {
                pScript.CreateNewTemplate();

                //Queues up the next predicted script, typically a field script
                pScriptModifier = pPacket.CreateNewScriptTemplateResetNotPersist();
                if (pScriptModifier != null) {
                    pScriptModifier.SetScript(pScript);
                }
            }

        } finally {
            int nCount = mPacketParsedCount.get(pPacket.nCode) != null ? (mPacketParsedCount.get(pPacket.nCode) + 1) : 1;
            mPacketParsedCount.put(pPacket.nCode, nCount);
            pLock.unlock();
        }
    }
    
    /**
     * Todo:: clean this crap output.
     * @return A super ugly looking representation of what was parsed and created.
     */
    public static String GetOutputLog() {
        String sOutputLog;
        sOutputLog = " - Packets processed: \r\n";
        for (int nHeader : mPacketParsedCount.keySet()) {
            LoopbackCode pLoopback = LoopbackCode.GetLoopback(nHeader);
            if (pLoopback != null) {
                sOutputLog += "     - [LP] " + pLoopback.name() + " (" + nHeader + ").... " + mPacketParsedCount.get(nHeader) + " packets processed \r\n";
            } else {
                ClientCode pClient = ClientCode.GetClient(nHeader);
                if (pClient != null) {
                    sOutputLog += "     - [CP] " + pClient.name() + " (" + nHeader + ").... " + mPacketParsedCount.get(nHeader) + " packets processed \r\n";
                }
            }
        }
        sOutputLog += " - Scripts created: \r\n";
        for (Class<? extends AbstractTemplate> pTemplate : mScriptCompiledCount.keySet()) {
            sOutputLog += "     - " + pTemplate.getSimpleName() + " scripts.... \r\n";
            for (String sScript : mScriptCompiledCount.get(pTemplate)) {
                sOutputLog += "           - " + (sScript.contains(".js") ? sScript : (sScript + ".js")) + " \r\n";
            }
        }
        return sOutputLog;
    }

    public static Script GetScript() {
        return pScript;
    }
    
    
    static 
    {
        mScriptCompiledCount.put(FieldTemplate.class, new LinkedList<>());
        mScriptCompiledCount.put(NpcTemplate.class, new LinkedList<>());
        mScriptCompiledCount.put(PortalTemplate.class, new LinkedList<>());
        mScriptCompiledCount.put(ReactorTemplate.class, new LinkedList<>());
        mScriptCompiledCount.put(QuestStartTemplate.class, new LinkedList<>());
        mScriptCompiledCount.put(QuestEndTemplate.class, new LinkedList<>());
        
        for (LoopbackCode pCode : LoopbackCode.values()) {
            mPacketParsedCount.put(pCode.nCode, 0);
        }
        
        for (ClientCode pCode : ClientCode.values()) {
            mPacketParsedCount.put(pCode.nCode, 0);
        }
    }
}
