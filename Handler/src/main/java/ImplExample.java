import remoteInterfaces.Hello;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ImplExample extends UnicastRemoteObject implements Hello {

    protected ImplExample() throws RemoteException {
        super();
    }

    public void printMsg() throws RemoteException {
        System.out.println("This is an example RMI program");
    }
}
