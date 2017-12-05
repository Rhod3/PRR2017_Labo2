import remoteInterfaces.IAppCom;
import remoteInterfaces.ILamport;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AppCom extends UnicastRemoteObject implements IAppCom {

    int value = 0;
    ILamport lamport;

    protected AppCom() throws RemoteException {

    }

    public void test() {
        System.out.println("Test AppCom");
    }

    public int getValue() throws RemoteException {
        System.out.println("Value got by App : " + value);
        return value;
    }

    public void setValue(int value) throws RemoteException {
        System.out.println("Value set to : " + value);
        this.value = value;
    }

    public void getCriticalSectionExclusion() throws RemoteException {

    }

    public void releaseCriticalSectionExclusion() throws RemoteException {

    }
}
