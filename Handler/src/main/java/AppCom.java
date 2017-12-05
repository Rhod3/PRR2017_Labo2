import remoteInterfaces.IAppCom;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AppCom extends UnicastRemoteObject implements IAppCom {

    protected AppCom() throws RemoteException {}

    public void test() {
        System.out.println("Test AppCom");
    }
}
