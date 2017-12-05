package remoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAppCom extends Remote {
    public void test() throws RemoteException;
}
