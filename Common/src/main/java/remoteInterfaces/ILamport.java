package remoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import utils.Message;

public interface ILamport extends Remote {
    void test() throws RemoteException;
    void demande() throws InterruptedException;
    Message receive(Message message) throws RemoteException;
    void end(int newSharedValue);
}
