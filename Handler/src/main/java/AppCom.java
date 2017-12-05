import remoteInterfaces.IAppCom;
import remoteInterfaces.ILamport;
import utils.Constants;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AppCom extends UnicastRemoteObject implements IAppCom {

    private int siteId;
    private int value = 0;

    public AppCom(int siteId) throws RemoteException, MalformedURLException, NotBoundException {
        this.siteId = siteId;
    }

    public void test() {
        System.out.println("Test AppCom");
    }

    public int getValue() throws RemoteException {
        System.out.println("Value got by App : " + value);
        return value;
    }

    public void setValue(int value) throws RemoteException {
        try {
            String urlLamport = Constants.LOCALHOST_RMI_URL + Constants.DEFAULT_PORT + "/Lamport" + siteId;
            ILamport lamport = (ILamport) Naming.lookup(urlLamport);

            lamport.demande();

            System.out.println("Value set to : " + value);
            this.value = value;

            lamport.end(value);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (NotBoundException e) {
            e.printStackTrace();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void getCriticalSectionExclusion() throws RemoteException {

    }

    public void releaseCriticalSectionExclusion() throws RemoteException {

    }
}
