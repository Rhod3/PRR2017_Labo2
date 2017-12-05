package remoteInterfaces;

import java.rmi.Remote;
import utils.Message;

public interface ILamport extends Remote {
    public void receive(Message message);
}
