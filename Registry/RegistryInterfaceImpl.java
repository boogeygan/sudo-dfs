import java.util.List;
import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject;
import java.io.IOException;
import java.rmi.RemoteException;
/**
 * @author gagan
 *
 */
public class RegistryInterfaceImpl extends UnicastRemoteObject implements RegistryInterface {

	List<String> servers=null;
	String masterServer=null;
	static final long serialVersionUID = 1L;
	
	protected RegistryInterfaceImpl () throws RemoteException{
		super();
	}

	@Override
	public boolean registerServer(String name) throws IOException, RemoteException
	{

		if(servers==null)
		{
			servers=new ArrayList<String>();
			System.out.println("Length of the server array list: "+servers.size());
			servers.add(name);
			System.out.println("Length of the server array list: "+servers.size());
			masterServer=new String(name);
			return true;
		}
		else
		{
			servers.add(name);
			System.out.println("Length of the server array list: "+servers.size());
			
		}
		System.out.println("Server Names: ");
	
		if(servers!=null)
		for(int i=0;i<servers.size();i++)
			System.out.println(servers.get(i));
		else
			System.out.println("No servres yet!");
		return false;

	}
	@Override
	public String[] getFileServers() throws IOException, RemoteException {
	System.out.println("in here returning the servers...");
		String retServers[]=new String[servers.size()];
		for(int i=0;i<retServers.length;i++)
			retServers[i]=new String(servers.get(i));
		return retServers;
	}

}
