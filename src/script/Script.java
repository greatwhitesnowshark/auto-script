/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import message.MessageType;
import message.QuestMessageType;
import template.AbstractTemplate;
import template.NpcTemplate;
import template.PortalTemplate;
import template.QuestEndTemplate;
import template.QuestStartTemplate;
import template.ReactorTemplate;
import template.FieldTemplate;
import util.Config;
import util.Logger;

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
    private final ReentrantLock pLock = new ReentrantLock();

    public MessageHistory GetMessageHistory() {
        return pHistory;
    }
    
    public NestedBlockHistory GetNestedBlockHistory() {
        return aHistoryNestedBlock.size() > 0 ? aHistoryNestedBlock.get(aHistoryNestedBlock.size() - 1) : null;
    }
    
    public int GetStrPaddingIndex() {
        int nPad = aHistoryNestedBlock.size();
        if (pTemplate != null && (pTemplate.IsQuestTemplate() || pTemplate.IsFieldTemplate() || pTemplate.IsNpcTemplate())) {
            nPad++;
        }
        return nPad;
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
                if (sFieldName == null || sFieldName.isEmpty()) {
                    if (pWriteRequest.GetField() > 0) {
                        dwField = pWriteRequest.GetField();
                        sFieldName = ScriptTemplateMap.GetFieldName(dwField);
                    }
                }
                if (sFileName == null || sFileName.isEmpty()) {
                    sFileName = pWriteRequest.GetTemplate().sDirPath + pWriteRequest.GetTemplate().sScript;
                }
                boolean bFileExists = false;
                if (pWriteRequest.GetTemplate() != null) {
                    bFileExists = new File(sFileName).exists();
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
                    nPad = 1;
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
                    for (String sType : QuestMessageType.sType) {
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
                    nPad = 1;
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
                    nPad = 1;
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
                    pWriteRequestHistory = new ScriptWriteRequest(pWriteRequest.GetField(), sOutputHistory, pWriteRequest.GetTemplate(), lConditionalText, nPad);
                    SetScript(pWriteRequestHistory);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void ProcessWriteRequest(ScriptWriteRequest<? extends AbstractTemplate> pWriteRequest) {
        pLock.lock();
        try {
            if (pWriteRequest != null) {
                
                //Get existing template
                if (pWriteRequest.GetTemplate() != null) {
                    if (!(new File(pWriteRequest.GetFileName()).exists())) {
                        CreateNewTemplate(null);
                        CreateNewTemplate(pWriteRequest);
                    }
                }
                
                //Get Message History 
                MessageHistory pMessageHistory;
                if ((pMessageHistory = GetMessageHistory()) == null) {
                    return;
                }
                NestedBlockHistory pNestedBlockHistory = GetNestedBlockHistory();
                
                //Get logger text (Debug)
                String sMessageHistory = pMessageHistory.GetOutput();
                while (sMessageHistory.substring(0, 1).equals("\t")) {
                    sMessageHistory = sMessageHistory.substring(1);
                }
                sMessageHistory = sMessageHistory.substring(0, sMessageHistory.length() > 40 ? 40 : sMessageHistory.length());
                
                //Merge with existing script
                List<String> lScriptLines = new LinkedList<>();
                boolean bFoundHistory = false;
                boolean bFoundNestedBlockHistory = GetNestedBlockHistory() == null;
                boolean bMergedOrDuplicate = false;
                String sOutputWithDebug = pWriteRequest.GetOutput() + (" //(history) " + sMessageHistory + "");
                try (BufferedReader pReader = new BufferedReader(new FileReader(sFileName))) {
                    while (pReader.ready()) {
                        String sLine = pReader.readLine();
                        if (!bFoundHistory) {
                            if (sLine.trim().replaceAll("\t", "").contains(pMessageHistory.GetOutput().trim().replaceAll("\t", ""))
                                    || pMessageHistory.GetOutput().trim().replaceAll("\t", "").contains(sLine.trim().replaceAll("\t", ""))) {
                                bFoundHistory = true;
                            }
                            lScriptLines.add(sLine);
                            continue;
                        }
                        if (!bFoundNestedBlockHistory && pNestedBlockHistory != null) {
                            if (pNestedBlockHistory.IsNestedBlockFound(sLine)) {
                                Logger.LogError("Found Nested Block History sTargetText value for file - [sFileName] " + sFileName);
                                Logger.LogAdmin("          - sOutput = " + pWriteRequest.GetOutput());
                                Logger.LogAdmin("          - sTargetText = " + pNestedBlockHistory.sTargetText);
                                Logger.LogAdmin("          - sLine = " + sLine);
                                bFoundNestedBlockHistory = true;
                            }
                            lScriptLines.add(sLine);
                            continue;
                        }
                        if (!bMergedOrDuplicate) {
                            if (sLine.trim().replaceAll("\t", "").contains(pWriteRequest.GetOutput().trim().replaceAll("\t", "")) 
                                    || pMessageHistory.GetOutput().trim().replaceAll("\t", "").contains(sLine.trim().replaceAll("\t", ""))) {
                                lScriptLines.add(sLine);
                                bMergedOrDuplicate = true;
                                continue;
                            }
                            if (pNestedBlockHistory != null && sLine.contains("}")) {
                                Logger.LogError("End of Nested Block History found for file (inserting BEFORE sLine) - [sFileName] " + sFileName);
                                Logger.LogAdmin("          - sOutput = " + pWriteRequest.GetOutput());
                                Logger.LogAdmin("          - sLine = " + sLine);
                                lScriptLines.add(Config.MessageHistoryDebug ? sOutputWithDebug : pWriteRequest.GetOutput());
                                for (String s : pWriteRequest.GetNestedBlockOutput()) {
                                    lScriptLines.add(s);
                                }
                                lScriptLines.add(sLine);
                                bMergedOrDuplicate = true;
                            } else if (pNestedBlockHistory == null && !pReader.ready()) {
                                Logger.LogError("End of Nested Block History found for file (inserting AFTER sLine) - [sFileName] " + sFileName);
                                Logger.LogAdmin("          - sLine = " + sLine);
                                Logger.LogAdmin("          - sOutput = " + pWriteRequest.GetOutput());
                                lScriptLines.add(sLine);
                                lScriptLines.add(Config.MessageHistoryDebug ? sOutputWithDebug : pWriteRequest.GetOutput());
                                for (String s : pWriteRequest.GetNestedBlockOutput()) {
                                    lScriptLines.add(s);
                                }
                            } else {
                                lScriptLines.add(sLine);
                            }
                            continue;
                        }
                        lScriptLines.add(sLine);
                    }
                } catch (FileNotFoundException ignore) { 
                    Logger.LogError("Did not create a template file for -");
                    Logger.LogAdmin("          - pWriteRequest.GetOutput() [" + pWriteRequest.GetOutput() + "]");
                    Logger.LogAdmin("          - " + pTemplate.sScript + " [sScript] / " + pTemplate.sDirPath + " [sDirPath] / " + pTemplate.dwTemplateID + " [dwTemplateID]");
                    ignore.printStackTrace();
                } finally {
                    Logger.LogError("Final values for script - [sFileName] " + sFileName);
                    Logger.LogAdmin("          - bFoundHistory = " + bFoundHistory);
                    Logger.LogAdmin("          - bFoundNestedBlockHistory = " + bFoundNestedBlockHistory);
                    Logger.LogAdmin("          - (target-output) = " + (pNestedBlockHistory != null ? pNestedBlockHistory.sTargetText : "none"));
                    Logger.LogAdmin("          - bMergedOrDuplicate = " + bMergedOrDuplicate);
                }
                
                //Write everything to the script
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
        
        /*String sLine = pReader.readLine();
                        if (!bWriteRequestComplete) {
                            if (!bFoundNestedBlock) {
                                if (aHistoryNestedBlock.size() > nNestedBlockCount) {
                                    NestedBlockHistory pNestedBlock = aHistoryNestedBlock.get(nNestedBlockCount);
                                    if (pNestedBlock.IsNestedBlockFound(sLine)) {
                                        nNestedBlockCount++;
                                        if (nNestedBlockCount == aHistoryNestedBlock.size()) {
                                            Logger.LogError("Found nested block - ");
                                            Logger.LogAdmin("        - sLine:  " + sLine + "");
                                            Logger.LogAdmin("        - sOutput: " + pWriteRequest.GetOutput() + "");
                                            Logger.LogAdmin("        - Script: [" + pWriteRequest.GetTemplate().sScript + "]");
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
                                    bWriteRequestComplete = true;//duplicate line found
                                    lScriptLines.add(sLine);
                                    continue;
                                }
                                if (aHistoryNestedBlock.size() > 0) {
                                    bFoundEndOfBlock = sLine.contains("}");
                                    if (bFoundEndOfBlock) {
                                        pWriteRequest.SetOutput((Config.MessageHistoryDebug ? (pWriteRequest.GetOutput() + "//(previous-line) " + sMessageHistory) : pWriteRequest.GetOutput()));
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
                                        pWriteRequest.SetOutput((Config.MessageHistoryDebug ? (pWriteRequest.GetOutput() + "//(previous-line) " + sMessageHistory) : pWriteRequest.GetOutput()));
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
                    if (!bWriteRequestComplete) { //force complete, this shouldn't normally (ever) occur
                        pWriteRequest.SetOutput((Config.MessageHistoryDebug ? (pWriteRequest.GetOutput() + " //(previous-line) " + sMessageHistory) : pWriteRequest.GetOutput()));
                        lScriptLines.add(pWriteRequest.GetOutput());
                        if (pWriteRequest.GetNestedBlockOutput().size() > 0) {
                            pWriteRequest.GetNestedBlockOutput().stream().forEach((s) -> {
                                lScriptLines.add(s);
                            });
                        }
                    }*/
    
    public void SetScript(ScriptWriteRequest pWriteRequest) {
        this.dwField = pWriteRequest.GetField();
        this.pTemplate = pWriteRequest.GetTemplate();
        this.sScriptName = pWriteRequest.GetTemplate().sScript;
        String sName = pWriteRequest.GetTemplate().sDirPath + sScriptName;
        this.sFileName = sName.contains(".js") ? sName : sName + ".js";
        this.sFieldName = ScriptTemplateMap.GetFieldName(pWriteRequest.GetField());
        SetScriptHistory(pWriteRequest);
    }
    
    public void SetScriptHistory(ScriptWriteRequest pWriteRequest) {
        MessageHistory pMessageHistory = new MessageHistory(pWriteRequest);
        if (pMessageHistory.GetNestedBlockOutput().size() > 0) {
            NestedBlockHistory pNestedBlockHistory = new NestedBlockHistory(pWriteRequest.GetField(), pMessageHistory.GetOutput(), pMessageHistory.GetNestedBlockOutput());
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
}
