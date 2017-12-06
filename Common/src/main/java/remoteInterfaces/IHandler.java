package remoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import utils.Message;

public interface IHandler extends Remote {
    int getValue() throws RemoteException;
    void setValue(int value) throws RemoteException;
    Message receive(Message message) throws RemoteException;
}
