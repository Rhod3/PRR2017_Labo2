package remoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAppCom extends Remote {
    void test() throws RemoteException;
    int getValue() throws RemoteException;
    void setValue(int value) throws RemoteException;
    void getCriticalSectionExclusion() throws RemoteException;
    void releaseCriticalSectionExclusion() throws RemoteException;
}
