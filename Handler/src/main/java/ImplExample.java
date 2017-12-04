import remoteInterfaces.Hello;

import java.rmi.RemoteException;

public class ImplExample implements Hello {

    public void printMsg() throws RemoteException {
        System.out.println("This is an example RMI program");
    }
}
