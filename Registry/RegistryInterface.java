import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistryInterface extends Remote {
	public boolean registerServer(String name)throws IOException, RemoteException;
	public String[] getFileServers()throws IOException, RemoteException;
}
