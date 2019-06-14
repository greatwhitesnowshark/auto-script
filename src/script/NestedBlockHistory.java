/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

import java.util.List;
import util.StringUtil;

/**
 *
 * @author Sharky
 */
public class NestedBlockHistory {
    
    public int nResult;
    public String sText, sTargetText, sResult; 
    public List<String> lConditionalText;

    public NestedBlockHistory(int nResult, String sText, List<String> lConditionalText) {
        this.sText = sText;
        this.lConditionalText = lConditionalText;
        this.sTargetText = "";
    }
    
    public boolean IsNestedBlockFound(String sLine) {
        return StringUtil.CountStringPaddingTab(this.sTargetText) == StringUtil.CountStringPaddingTab(sLine) && sLine.trim().replaceAll("\t", "").contains(this.sTargetText.trim().replaceAll("\t", ""));
    }
    
    public void SetNestedBlockResult(int nResult) {
        this.nResult = nResult;
        for (String sConditional : lConditionalText) {
            if (sConditional.contains((" == " + nResult)) || sConditional.contains((nResult + ":"))) {
                this.sTargetText = sConditional;
                break;
            }
        }
    }
    
    public void SetNestedBlockResult(String sResult) {
        this.sResult = sResult;
        for (String sConditional : lConditionalText) {
            if (sConditional.contains((" == " + sResult)) || sConditional.contains((sResult + ":")) || sConditional.contains((".equals(\"" + sResult)) || sConditional.contains((".equalsIgnoreCase(\"" + sResult))) {
                this.sTargetText = sConditional;
                break;
            }
        }
    }
}
