// Fichier: RMIConcurrentServeur.java
// Implementation de l'interface des methodes devant etre asynchrones
// entre les clients.

import remoteInterfaces.RMIConcurrent;

import java.rmi.*;
import java.rmi.server.*;

public class RMIConcurrentServeur extends UnicastRemoteObject
        implements RMIConcurrent
{
    public RMIConcurrentServeur() throws RemoteException
    {
        super();
    }

    synchronized public void Acces1() throws RemoteException
    {
        // Signaler qu'un processus est dedans
        System.out.println("Serveur -- dedans Acces1");
        for (int i = 0; i < 100000; i++)
            for (int j = 0; j < 10000; j++);
        System.out.println("Serveur -- sortie Acces1");
    }

    synchronized public void Acces2() throws RemoteException
    {
        // Signaler qu'un processus est dedans
        System.out.println("Serveur -- dedans Acces2");
        for (int i = 0; i < 100000; i++)
            for (int j = 0; j < 10000; j++);
        System.out.println("Serveur -- sortie Acces2");
    }

    public static void main(String args[])
    {
        System.setSecurityManager(new RMISecurityManager());
        try {
            String serveurNom = "RMIConcurrent";
            RMIConcurrent serveur = new RMIConcurrentServeur();
            Naming.rebind(serveurNom,serveur);
            System.out.println("Serveur " + serveurNom + " pret");
        } catch (Exception e) {
            System.out.println("Exception a l'enregistrement: " + e);
        }
    }
}