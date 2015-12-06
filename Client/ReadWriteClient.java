import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class ReadWriteClient implements Runnable
{
	
	public static long chunkSize = 65536;
	private Thread t;
	private String threadName;
	public static String filename = null;
	public static long numberOfChunks;
	public static long numOfThreads = 0;
	public static Registry registry = null;
	public static String fileServers[] = null;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static ArrayList<Integer> failedServer = new ArrayList();
	
	public ReadWriteClient() {}
	
	ReadWriteClient( String name){
		threadName = name;
		System.out.println("Creating " +  threadName );
	}
	public void run() {
		System.out.println("Running " +  threadName );
		String strArray[] = threadName.split("_");
		int threadNumber = Integer.parseInt(strArray[1]);
		int serverIndex = threadNumber;
		if(failedServer.size() == numOfThreads)
		{
			System.out.println("All the servers are dead");
			System.exit(1);
		}
		while(failedServer.contains(serverIndex)){
			if (serverIndex == (numOfThreads-1))
				serverIndex =0;
			else
				serverIndex++;
		}
		try
		{
			long counter,counter1;
			File readFile = new File("output/" + filename);
			RandomAccessFile outputFile = new RandomAccessFile(readFile, "rw");
			
			ReadWriteInterface readWrite = (ReadWriteInterface)registry.lookup(fileServers[serverIndex]);
			synchronized (outputFile) {
				for(counter1 = threadNumber*chunkSize,counter=threadNumber;counter<numberOfChunks;counter+=numOfThreads,counter1=counter*chunkSize)
				{
					byte data[] = new byte[(int)chunkSize];
					data = readWrite.FileRead64K(filename, counter1);
					outputFile.seek(counter1);
					if(data != null)
						outputFile.write(data);
				}
			}
			outputFile.close();
		}
		catch (RemoteException e) {
			synchronized (failedServer) {
				failedServer.add(threadNumber);
			}
			System.out.println("remote Exception");
		}
		catch (Exception e)
		{
			System.out.println("Error");
			e.printStackTrace();
		}
		System.out.println("Thread " +  threadName + " exiting.");
	}

	public void start ()
	{
		System.out.println("Starting " +  threadName );
		if (t == null)
		{
			t = new Thread (this, threadName);
			t.start ();
		}
	}
	
	public static void main(String[] args) {
		try
		{
			if(args.length < 2)
			{
				System.err.println("Error");
				System.exit(1);
			}
			registry = LocateRegistry.getRegistry(args[1], Integer.parseInt(args[2]));
			int resultValue;
			
			long fileLength, counter;
			
			RegistryInterface registryServerInterface = (RegistryInterface)registry.lookup("RegistryInterfaceImpl");
			fileServers = registryServerInterface.getFileServers();
			
			if(fileServers == null || fileServers.length == 0)
			{
				System.out.println("Error. No file servers present.");
				System.exit(1);
			}
			
			ReadWriteInterface readWrite = (ReadWriteInterface)registry.lookup(fileServers[0]);
			File file = new File(args[0]);
			if(!file.exists())
			{
				System.out.println("Input file does not exist");
				return;
			}
			filename = file.getName();
			if(filename == null)
				return;
			RandomAccessFile clientFile = new RandomAccessFile(file, "r");
			fileLength = file.length();
			for(counter=0;counter<fileLength;counter+=chunkSize)
			{
				byte result[] = null;
				if(fileLength-counter>=chunkSize)
				{
					result = new byte[(int)chunkSize];
				}
				else
				{
					result = new byte[(int)(fileLength-counter)];
				}
				clientFile.read(result);
				resultValue = readWrite.FileWrite64K(filename, counter, result);
				if(resultValue == -1)
					break;
				result = null;
			}

			
			numberOfChunks = readWrite.NumFileChunks(filename);
			
			long numOfFileServers = fileServers.length;
			if(numOfFileServers<=numberOfChunks)
				numOfThreads = numOfFileServers;
			else
				numOfThreads = numberOfChunks;
			
			
						
			System.out.println("Main thread sleeping...kill servers of your choice :P ");
			Thread.sleep(10000);
			System.out.println("\nMain Thread Resumed!!");
			
			
			File readDirectory = new File("output");
			if(!readDirectory.exists())
			{
				readDirectory.mkdir();
			}
			else if(readDirectory.exists() && !readDirectory.isDirectory())
			{
				readDirectory.delete();
				readDirectory.mkdir();
			}
			File readFile = new File("output/" + filename);
			if(readFile.exists())
			{
				readFile.delete();
			}
			RandomAccessFile outputFile = new RandomAccessFile(readFile, "rw");
			
			List<Thread> threads = new ArrayList<Thread>();
			
			for(int iterator=0;iterator<numOfThreads;iterator++)
			{
				Runnable task = new ReadWriteClient("Thread_"+iterator);
				Thread worker = new Thread(task);
				worker.start();
				worker.join();
				threads.add(worker);
			}
			int failedServerListSize = failedServer.size();
			System.out.println("List Size: " + failedServerListSize);
			for(int iterator=0;iterator<failedServerListSize;iterator++)
			{
				Runnable task = new ReadWriteClient("Thread_"+failedServer.get(iterator));
				Thread worker = new Thread(task);
				worker.start();
				worker.join();
			}
			outputFile.close();
			clientFile.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Error");
			e.printStackTrace();
		}
		catch (Exception e)
		{
			System.out.println("Error");
			e.printStackTrace();
		}
	}

}

