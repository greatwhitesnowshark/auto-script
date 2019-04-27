/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet;

import template.AbstractTemplate;

/**
 *
 * @author Sharky
 * @param <T> script-type
 */
public abstract class PacketWriteRequest<T extends AbstractTemplate> extends Packet {
    
    public int dwField, nStrPaddingIndex;
    public T pTemplate;
    
    public PacketWriteRequest(int nHeader) {
        super(nHeader);
    }
    
    public int GetField() {
        return this.dwField;
    }
    
    public T GetTemplate() {
        return this.pTemplate;
    }
    
    public int GetStrPaddingIndex() {
        return this.nStrPaddingIndex;
    }
}
