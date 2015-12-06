import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.util.concurrent.TimeUnit;


public class FileServer implements Remote
{
	private boolean isMaster;
	private String name;

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static void main(String[] args) throws NumberFormatException, NotBoundException, IOException, AlreadyBoundException,InterruptedException 
	{
		TimeUnit.SECONDS.sleep(2);
		Date time=new Date();
		FileServer obj=new FileServer();
		obj.setName("Server_"+time.getTime());
		System.out.println("Server Name is: "+obj.getName());
		System.out.println("Locating Registry...");
		
		Registry registry=LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));

		ReadWriteInterfaceImpl service=new ReadWriteInterfaceImpl();
		registry.bind(obj.getName(),service);
		
		System.out.println("Looking up Registry for RegistryInterfaceImpl");
		

		RegistryInterface regServer=(RegistryInterface)registry.lookup("RegistryInterfaceImpl");

		System.out.println("Registering on RegistryInterface...");
		obj.setMaster(regServer.registerServer(obj.getName()));
		System.out.println("isMaster?? : "+obj.isMaster());
	}
}
