// Fichier: RMIConcurrent.java
// Interface des methodes devant etre asynchrones entre les clients.

package remoteInterfaces;

import java.rmi.*;

public interface RMIConcurrent extends Remote
{
    public void Acces1() throws RemoteException;
    public void Acces2() throws RemoteException;
}
