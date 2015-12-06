import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistryServer {
	public static void main(String[] args) throws Exception
	{
		Registry reg=LocateRegistry.createRegistry(Integer.parseInt(args[1]));
		RegistryInterfaceImpl obj=new RegistryInterfaceImpl();
		reg.bind("RegistryInterfaceImpl",obj);

		System.out.println("\n\nServer started....\n\n");

		System.out.println("Remote Object bound to the name 'RegistryInterfaceImpl' and is ready for use... at port number\n"+Integer.parseInt(args[1]));

	}
}
