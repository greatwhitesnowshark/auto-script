/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet;

import script.MessageHistory;
import template.AbstractTemplate;

/**
 *
 * @author Sharky
 * @param <T> script-type
 */
public abstract class PacketWriteRequest<T extends AbstractTemplate> extends PacketWrapperNull {

    public T pTemplate;
    public MessageHistory pHistory;
    public int dwField, nStrPaddingIndex;
    
    public PacketWriteRequest(int nHeader) {
        super(nHeader);
    }
}
