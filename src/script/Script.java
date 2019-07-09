/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

import game.scripting.ScriptMan.MessageType;
import game.scripting.ScriptSysFunc.QuestRequestType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import template.AbstractTemplate;
import template.NpcTemplate;
import template.PortalTemplate;
import template.QuestEndTemplate;
import template.QuestStartTemplate;
import template.ReactorTemplate;
import template.FieldTemplate;
import scriptmaker.ScriptMakerConfig;
import util.Logger;
import util.StringUtil;

/**
 *
 * @author Sharky
 */
public class Script {
    
    public int dwField;
    public int nMessageTypeInput = -1, nModeInput = 1, nSelectionInput = -1; 
    public long liPetCashSN = -1; 
    public String sScriptName, sFileName, sFieldName; 
    public String srInputResult = ""; 
    public AbstractTemplate pTemplate;
    public MessageHistory pHistory;
    public List<NestedBlockHistory> aHistoryNestedBlock = new LinkedList<>();
    public List<Integer> aSessionQuestIDs = new ArrayList<>();
    private final ReentrantLock pLock = new ReentrantLock();

    public void CreateNewTemplate() {
        CreateNewTemplate(null, true);
    }
    
    public void CreateNewTemplate(ScriptWriteRequest pWriteRequest) {
        CreateNewTemplate(pWriteRequest, false);
    }
    
    public void CreateNewTemplate(ScriptWriteRequest pWriteRequest, boolean bResetTemplate) {
        try {
            if (pWriteRequest == null || bResetTemplate) {
                this.nMessageTypeInput = -1;
                this.nModeInput = 1;
                this.nSelectionInput = -1;
                this.liPetCashSN = -1;
                this.sScriptName = "";
                this.sFileName = "";
                this.srInputResult = "";
                this.pTemplate = null;
                this.pHistory = null;
                this.aHistoryNestedBlock.clear();
                if (pWriteRequest == null) {
                    return;
                }
            }
            if (pWriteRequest.GetTemplate() != null) {
                ScriptWriteRequest pWriteRequestHistory;
                String sOutputHistory = "";
                List<String> lConditionalText = new LinkedList<>();
                if (sFileName == null || sFileName.isEmpty()) {
                    sFileName = pWriteRequest.GetTemplate().sDirPath + pWriteRequest.GetTemplate().sScript;
                }
                boolean bFileExists = false;
                if (pWriteRequest.GetTemplate() != null) {
                    bFileExists = Files.exists(Paths.get(pWriteRequest.GetFileName()));
                }
                sFileName = pWriteRequest.GetFileName();
                int nPad = 0;
                if (pWriteRequest.GetTemplate() instanceof NpcTemplate) {
                    NpcTemplate pNpcTemplate = (NpcTemplate) pWriteRequest.GetTemplate();
                    nMessageTypeInput = MessageType.Say;
                    sOutputHistory = "dwField = pField.dwField; \r\n";
                    lConditionalText = new LinkedList<>();
                    lConditionalText.add("switch(dwField) \r\n{");
                    lConditionalText.add("\tcase " + dwField + ": { // " + ScriptTemplateMap.GetFieldName(dwField));
                    lConditionalText.add("\t}");
                    lConditionalText.add("\tbreak;");
                    lConditionalText.add("}");
                    if (!bFileExists) {
                        try (BufferedWriter pWriter = new BufferedWriter(new FileWriter(sFileName))) {
                            pWriter.write("/**"); pWriter.newLine();
                            pWriter.write(" *"); pWriter.newLine();
                            pWriter.write(" * @author Auto-Scripter"); pWriter.newLine();
                            pWriter.write(" * @npc " + ScriptTemplateMap.GetNpcName(pNpcTemplate.dwTemplateID) + " (" + pNpcTemplate.dwTemplateID + ")"); pWriter.newLine();
                            pWriter.write(" */"); pWriter.newLine();
                            pWriter.write(sOutputHistory); pWriter.newLine();
                            for (String s : lConditionalText) {
                                pWriter.write(s); pWriter.newLine();
                            }
                        }
                    }
                    nPad = 0;
                } else if (pWriteRequest.GetTemplate() instanceof QuestStartTemplate || pWriteRequest.GetTemplate() instanceof QuestEndTemplate) {
                    QuestStartTemplate pQuestStartTemplate = pWriteRequest.GetTemplate() instanceof QuestStartTemplate ? (QuestStartTemplate) pWriteRequest.GetTemplate() : null;
                    QuestEndTemplate pQuestEndTemplate = pWriteRequest.GetTemplate() instanceof QuestEndTemplate ? (QuestEndTemplate) pWriteRequest.GetTemplate() : null;
                    boolean bStart = pQuestStartTemplate != null;
                    int dwQuestID = pQuestStartTemplate != null ? pQuestStartTemplate.dwTemplateID : pQuestEndTemplate != null ? pQuestEndTemplate.dwTemplateID : pTemplate.dwTemplateID;
                    int dwNpcID = pQuestStartTemplate != null ? pQuestStartTemplate.dwNpcTemplateID : pQuestEndTemplate != null ? pQuestEndTemplate.dwNpcTemplateID : 0;
                    String sQuestName = pQuestStartTemplate != null ? pQuestStartTemplate.sQuestName : pQuestEndTemplate != null ? pQuestEndTemplate.sQuestName : "";
                    nMessageTypeInput = MessageType.Say;
                    sOutputHistory = ("nQRStatus = self.GetQuestRequestState(" + dwQuestID + "); \r\n");
                    lConditionalText = new LinkedList<>();
                    lConditionalText.add("switch(nQRStatus) \r\n{");
                    for (Field pField : QuestRequestType.class.getDeclaredFields()) {
                        String sType = pField.getName();
                        lConditionalText.add("\tcase QuestRequest." + sType + ": {");
                        lConditionalText.add("\t}");
                        lConditionalText.add("\tbreak; \r\n");
                    }
                    lConditionalText.add("}");
                    if (!bFileExists) {
                        try (BufferedWriter pWriter = new BufferedWriter(new FileWriter(sFileName))) {
                            pWriter.write("/**"); pWriter.newLine();
                            pWriter.write(" *"); pWriter.newLine();
                            pWriter.write(" * @author Auto-Scripter"); pWriter.newLine();
                            pWriter.write(" * @quest " + sQuestName + " (" + dwQuestID + ")"); pWriter.newLine();
                            pWriter.write(" * @npc " + ScriptTemplateMap.GetNpcName(dwQuestID) + " (" + dwNpcID + ")"); pWriter.newLine();
                            pWriter.write(" */"); pWriter.newLine();
                            pWriter.write(sOutputHistory); pWriter.newLine();
                            for (String s : lConditionalText) {
                                pWriter.write(s); pWriter.newLine();
                            }
                        }
                    }
                    nPad = 0;
                } else if (pWriteRequest.GetTemplate() instanceof PortalTemplate) {
                    PortalTemplate pPortalTemplate = (PortalTemplate) pWriteRequest.GetTemplate();
                    sOutputHistory = " */";
                    lConditionalText = new LinkedList<>();
                    if (!bFileExists) {
                        try (BufferedWriter pWriter = new BufferedWriter(new FileWriter(sFileName))) {
                            pWriter.write("/**"); pWriter.newLine();
                            pWriter.write(" *"); pWriter.newLine();
                            pWriter.write(" * @author Auto-Scripter"); pWriter.newLine();
                            pWriter.write(" * @portal " + pPortalTemplate.sPortalName); pWriter.newLine();
                            pWriter.write(" * @field " + sFieldName + " (" + dwField + ")"); pWriter.newLine();
                            pWriter.write(sOutputHistory); pWriter.newLine();
                        } 
                    }
                } else if (pWriteRequest.GetTemplate() instanceof FieldTemplate) {
                    sOutputHistory = "";
                    lConditionalText = new LinkedList<>();
                    FieldTemplate pFieldTemplate = (FieldTemplate) pWriteRequest.GetTemplate();
                    if (pFieldTemplate != null) {
                        sOutputHistory = "dwField = pField.dwField; \r\n";
                        lConditionalText = new LinkedList<>();
                        lConditionalText.add("switch(dwField) \r\n{");
                        lConditionalText.add("\tcase " + dwField + ": { // " + ScriptTemplateMap.GetFieldName(dwField));
                        lConditionalText.add("\t}");
                        lConditionalText.add("\tbreak;");
                        lConditionalText.add("}");
                        if (!bFileExists) {
                            try (BufferedWriter pWriter = new BufferedWriter(new FileWriter(sFileName))) {
                                pWriter.write("/**"); pWriter.newLine();
                                pWriter.write(" *"); pWriter.newLine();
                                pWriter.write(" * @author Auto-Scripter"); pWriter.newLine();
                                pWriter.write(" * @field " + sFieldName + " (" + dwField + ")"); pWriter.newLine();
                                pWriter.write(" * @script-type " + pFieldTemplate.sType); pWriter.newLine();
                                pWriter.write(" */"); pWriter.newLine();
                                pWriter.write(sOutputHistory); pWriter.newLine();
                                for (String s : lConditionalText) {
                                    pWriter.write(s); pWriter.newLine();
                                }
                            }
                        }
                    }
                    nPad = 0;
                } else if (pWriteRequest.GetTemplate() instanceof ReactorTemplate) {
                    ReactorTemplate pReactorTemplate = (ReactorTemplate) pWriteRequest.GetTemplate();
                    sOutputHistory = " */";
                    lConditionalText = new LinkedList<>();
                    if (!bFileExists) {
                        try (BufferedWriter pWriter = new BufferedWriter(new FileWriter(sFileName))) {
                            pWriter.write("/**"); pWriter.newLine();
                            pWriter.write(" *"); pWriter.newLine();
                            pWriter.write(" * @author Auto-Scripter"); pWriter.newLine();
                            pWriter.write(" * @field " + sFieldName + " (" + dwField + ")"); pWriter.newLine();
                            pWriter.write(" * @reactor " + pReactorTemplate.sReactorName + " (" + pReactorTemplate.dwTemplateID + ")"); pWriter.newLine();
                            pWriter.write(sOutputHistory); pWriter.newLine();
                        }
                    }
                }
                if (!sOutputHistory.isEmpty()) {
                    pWriteRequestHistory = new ScriptWriteRequest(pWriteRequest.GetFieldID(), sOutputHistory, pWriteRequest.GetTemplate(), lConditionalText, nPad);
                    SetScript(pWriteRequestHistory);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AbstractTemplate GetTemplate() { return pTemplate; }
    
    public MessageHistory GetMessageHistory() {
        return pHistory;
    }
    
    public NestedBlockHistory GetNestedBlockHistory() {
        return aHistoryNestedBlock.size() > 0 ? aHistoryNestedBlock.get(aHistoryNestedBlock.size() - 1) : null;
    }
    
    public int CurrentLinePadding() {
        int nPad = aHistoryNestedBlock.size();
        if (pTemplate != null && (pTemplate.IsQuestTemplate() || pTemplate.IsFieldTemplate() || pTemplate.IsNpcTemplate())) {
            nPad++;
        }
        return nPad;
    }
    
    public void ProcessWriteRequest(ScriptWriteRequest<? extends AbstractTemplate> pWriteRequest) {
        pLock.lock();
        try {
            if (pWriteRequest != null) {
                //Get existing template
                if (pWriteRequest.GetTemplate() != null && !Files.exists(Paths.get(pWriteRequest.GetFileName()))) {
                    CreateNewTemplate(pWriteRequest);
                }
                //Get Message History 
                MessageHistory pMessageHistory;
                if ((pMessageHistory = GetMessageHistory()) == null) {
                    Logger.LogError("ProcessWriteRequest failed due to no previous message history detected.... pWriteRequest.GetOutput()[`%s`]", pWriteRequest.GetOutput());
                    return;
                }
                //Get logger text (Debug)
                String sMessageHistory = pMessageHistory.GetOutput();
                while (sMessageHistory.substring(0, 1).equals("\t")) {
                    sMessageHistory = sMessageHistory.substring(1);
                }
                sMessageHistory = sMessageHistory.substring(sMessageHistory.length() >= 72 ? 32 : 0, sMessageHistory.length() >= 72 ? 71 : sMessageHistory.length());
                //Compile script lines
                List<String> lScriptLines = new LinkedList<>();
                String sLine = "", sPreviousLine;
                boolean bWriteRequestComplete = false, bFoundNestedBlock = false, bFoundEndOfBlock = false;
                int nNestedBlockCount = 0;
                try (BufferedReader pReader = new BufferedReader(new FileReader(sFileName))) {
                    while (pReader.ready()) {
                        sPreviousLine = sLine;
                        sLine = pReader.readLine();
                        if (!bWriteRequestComplete) {
                            if (!bFoundNestedBlock) {
                                if (aHistoryNestedBlock.size() > nNestedBlockCount) {
                                    NestedBlockHistory pNestedBlock = aHistoryNestedBlock.get(nNestedBlockCount);
                                    if (pNestedBlock.IsNestedBlockFound(sLine)) {
                                        nNestedBlockCount++;
                                        if (nNestedBlockCount == aHistoryNestedBlock.size()) {
                                            if (ScriptMakerConfig.LastMessageHistoryDebug) {
                                                Logger.LogError("[TRUE] nNestedBlockCount == aHistoryNestedBlock.size() [" + nNestedBlockCount + "]");
                                                Logger.LogError("Found nested block - ");
                                                Logger.LogAdmin("        - sLine:  " + sLine + ", CountStringPadding = " + StringUtil.CountStringPaddingTab(sLine));
                                                Logger.LogAdmin("        - pNestedBlock.sTargetText = " + pNestedBlock.sTargetText + ", CountStringPadding = " + StringUtil.CountStringPaddingTab(pNestedBlock.sTargetText));
                                                Logger.LogAdmin("        - sOutput: " + pWriteRequest.GetOutput() + "");
                                                Logger.LogAdmin("        - Script: [" + pWriteRequest.GetTemplate().sScript + "]");
                                            }
                                            bFoundNestedBlock = true;
                                        }
                                    }
                                    lScriptLines.add(sLine);
                                    continue;
                                } else {
                                    bFoundNestedBlock = true; //found the last occurrence and array is emptied
                                }
                            }
                            if (bFoundNestedBlock) {
                                if ((sLine.contains(pWriteRequest.GetOutput()) || pWriteRequest.GetOutput().contains(sLine))) {
                                    if (!sLine.contains("self.Wait();") || (sPreviousLine.trim().replaceAll("\t", "").contains(pHistory.GetOutput().trim().replaceAll("\t", "")))) {
                                        bWriteRequestComplete = true;//duplicate line found
                                        lScriptLines.add(sLine);
                                        continue;
                                    }
                                }
                                if (aHistoryNestedBlock.size() > 0) {
                                    bFoundEndOfBlock = sLine.contains("}");
                                    if (bFoundEndOfBlock) {
                                        pWriteRequest.SetOutput((ScriptMakerConfig.LastMessageHistoryDebug ? (pWriteRequest.GetOutput() + "//(previous-line) " + sMessageHistory) : pWriteRequest.GetOutput()));
                                        lScriptLines.add(pWriteRequest.GetOutput());
                                        if (pWriteRequest.GetNestedBlockOutput().size() > 0) {
                                            pWriteRequest.GetNestedBlockOutput().stream().forEach((s) -> {
                                                lScriptLines.add(s);
                                            });
                                        }
                                        bWriteRequestComplete = true;
                                        lScriptLines.add(sLine);
                                    } else {
                                        lScriptLines.add(sLine);
                                    }
                                } else {
                                    if (!pReader.ready()) {
                                        lScriptLines.add(sLine);
                                        pWriteRequest.SetOutput((ScriptMakerConfig.LastMessageHistoryDebug ? (pWriteRequest.GetOutput() + "//(previous-line) " + sMessageHistory) : pWriteRequest.GetOutput()));
                                        lScriptLines.add(pWriteRequest.GetOutput());
                                        if (pWriteRequest.GetNestedBlockOutput().size() > 0) {
                                            pWriteRequest.GetNestedBlockOutput().stream().forEach((s) -> {
                                                lScriptLines.add(s);
                                            });
                                        }
                                        bWriteRequestComplete = true;
                                    } else {
                                        lScriptLines.add(sLine);
                                    }
                                }
                            }
                        } else {
                            lScriptLines.add(sLine);
                        }
                    }
                } catch (Exception exx) {
                    exx.printStackTrace();
                    Logger.LogError("EXX exception thrown in Script.java"); //never happens
                }
                if (!bWriteRequestComplete) { 
                    //Pop the last line "written" in the queue, add the missing Target-Conditional we couldn't find and replace the last line back to the end
                    sLine = lScriptLines.remove(lScriptLines.size() - 1);
                    pWriteRequest.SetOutput((ScriptMakerConfig.LastMessageHistoryDebug ? (pWriteRequest.GetOutput() + " //(previous-line) " + sMessageHistory) : pWriteRequest.GetOutput()));
                    if (GetNestedBlockHistory().sTargetText.contains("case ") && GetNestedBlockHistory().sTargetText.contains(":")) {
                        lScriptLines.add("\r\n" + GetNestedBlockHistory().sTargetText);
                    } else {
                        lScriptLines.add(GetNestedBlockHistory().sTargetText);
                    }
                    lScriptLines.add(pWriteRequest.GetOutput());
                    if (pWriteRequest.GetNestedBlockOutput().size() > 0) {
                        pWriteRequest.GetNestedBlockOutput().stream().forEach((s) -> {
                            lScriptLines.add(s);
                        });
                    }
                    String sPadding = "";
                    int nPad = StringUtil.CountStringPaddingTab(GetNestedBlockHistory().sTargetText);
                    for (int i = 0; i < nPad; i++) {
                        sPadding += "\t";
                    }
                    lScriptLines.add((sPadding + "}"));
                    if (GetNestedBlockHistory().sTargetText.contains("case ")) {
                        lScriptLines.add((sPadding + "break;"));
                    }
                    lScriptLines.add(sLine);
                }
                //Write everything back to the script
                try (BufferedWriter pWriter = new BufferedWriter(new FileWriter(sFileName))) {
                    for (String sOutputLine : lScriptLines) {
                        pWriter.write(sOutputLine); pWriter.newLine();
                    }
                }
                //Set this script's template to match that of the last requested template
                SetScriptHistory(pWriteRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pLock.unlock();
        }
    }
    
    public void SetScript(ScriptWriteRequest pWriteRequest) {
        this.dwField = pWriteRequest.GetFieldID();
        this.pTemplate = pWriteRequest.GetTemplate();
        this.sScriptName = pWriteRequest.GetTemplate().sScript;
        String sName = pWriteRequest.GetTemplate().sDirPath + sScriptName;
        this.sFileName = sName.contains(".js") ? sName : sName + ".js";
        this.sFieldName = ScriptTemplateMap.GetFieldName(pWriteRequest.GetFieldID());
        SetScriptHistory(pWriteRequest);
    }
    
    public void SetScriptHistory(ScriptWriteRequest pWriteRequest) {
        MessageHistory pMessageHistory = new MessageHistory(pWriteRequest);
        if (pMessageHistory.GetNestedBlockOutput().size() > 0) {
            NestedBlockHistory pNestedBlockHistory = new NestedBlockHistory(pWriteRequest.GetFieldID(), pMessageHistory.GetOutput(), pMessageHistory.GetNestedBlockOutput());
            if (pWriteRequest.GetTemplate().IsQuestTemplate()) {
                pNestedBlockHistory.SetNestedBlockResult((pWriteRequest.GetTemplate().IsQuestStartTemplate() ? "QuestRequest.OpeningScript" : "QuestRequest.CompleteScript"));
                aHistoryNestedBlock.add(pNestedBlockHistory);
            } else if (pWriteRequest.GetTemplate().IsNpcTemplate() || pWriteRequest.GetTemplate().IsFieldTemplate()) {
                pNestedBlockHistory.SetNestedBlockResult(dwField);
                aHistoryNestedBlock.add(pNestedBlockHistory);
            }
        }
        pHistory = pMessageHistory;
    }

    public void ClearSessionQuestID() {
        this.aSessionQuestIDs.clear();
    }
}
