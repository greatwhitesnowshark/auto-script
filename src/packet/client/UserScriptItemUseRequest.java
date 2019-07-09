package packet.client;

import game.network.InPacket;
import packet.PacketWrapperNull;
import packet.opcode.ClientCode;

/**
 *
 * @author Sharky
 */

public class UserScriptItemUseRequest extends PacketWrapperNull {

    public UserScriptItemUseRequest(InPacket iPacket) {
        super(ClientCode.UserScriptItemUseRequest.nCode);
    }

    @Override
    public boolean IsScriptResetNotPersist() {
        return true;
    }
}
