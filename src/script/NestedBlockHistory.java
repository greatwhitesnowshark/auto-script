/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

import java.util.List;

/**
 *
 * @author Five
 */
public class NestedBlockHistory {
    
    public int nResult;
    public String sText, sTargetText, sResult; //previous chat line, line w/ corresponding block result
    public List<String> lConditionalText;

    public NestedBlockHistory(int nResult, String sText, List<String> lConditionalText) {
        this.sText = sText;
        this.lConditionalText = lConditionalText;
        this.sTargetText = "";
    }
    
    public boolean IsTargetTextSet() {
        return !sTargetText.isEmpty();
    }

    public boolean IsNestedBlockFound(String sLine) {
        return sLine.trim().replaceAll("\t", "").contains(this.sTargetText.trim().replaceAll("\t", ""));
    }
    
    public void SetNestedBlockResult(int nResult) {
        this.nResult = nResult;
        lConditionalText.stream().filter((sConditional) -> sConditional.contains("== " + nResult) || (sConditional.contains("case " + nResult + ":"))).forEach((sConditional) -> {
            this.sTargetText = sConditional;
        });
    }
    
    public void SetNestedBlockResult(String sResult) {
        this.sResult = sResult;
        lConditionalText.stream().filter((sConditional) -> sConditional.contains("== " + sResult) || (sConditional.contains("case " + sResult + ":"))).forEach((sConditional) -> {
            this.sTargetText = sConditional;
        });
    }
}
