package remoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import utils.Message;

public interface ILamport extends Remote {
    public  void test() throws RemoteException;
    public void receive(Message message) throws RemoteException;
}
