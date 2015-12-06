import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ReadWriteInterfaceImpl extends UnicastRemoteObject implements ReadWriteInterface 
{
	private static final int CHUNKSIZE = 65536;
	static final long serialVersionUID = 1L;
	protected ReadWriteInterfaceImpl() throws RemoteException {
		super();
	}

	@Override
	public int FileWrite64K(String filename, long offset, byte[] data) throws IOException, RemoteException 
	{
		try
		{
			long chunkNumber=offset/CHUNKSIZE;
			File dir=new File(filename);
			File chunk;
			FileOutputStream fos;
			boolean dirStatus=false;
			if(!dir.exists())
			{
				if(dir.isFile())
				{
					dir.delete();
				}
				dirStatus=dir.mkdir();
			}
			else
			{
				if(dir.isDirectory())
				{
					if(offset==0)
					{
					//delete all the previous chunks
					File arr[]=dir.listFiles();
					for(int i=0;i<arr.length;i++)
						arr[i].delete();
					}
				}
			}
			if(data.length!=CHUNKSIZE){
				throw new InvalidDataSizeException();
			}

			chunk=new File(filename+"/chunk"+chunkNumber);
			//System.out.println(chunk.getAbsolutePath());
			fos=new FileOutputStream(chunk);
			fos.write(data);
			if(fos!=null)
				fos.close();
			
			return 0;
		}
		catch(InvalidDataSizeException ex)
		{
			return -1;
		}
	}

	@Override
	public long NumFileChunks(String filename) throws IOException,RemoteException 
	{
		File dir=null;
		long countChunk=0;
		if(filename!=null || filename!="")
			dir=new File(filename);
		if(dir.exists())
			return dir.listFiles().length;
		return countChunk;
	}

	@Override
	public byte[] FileRead64K(String filename, long offset) throws IOException,RemoteException 
	{
	System.out.println("Starting Offset: "+offset);
		byte data[]=null;
		File dir=new File(filename);
		File file=null;
		FileInputStream in=null;
		int status;

		if(dir.exists() && dir.isDirectory())
		{
			file=new File(dir.getName()+"/chunk"+offset/CHUNKSIZE);
			if(file.exists())
			{
				//System.out.println("Filename is: "+file.getName());
				in=new FileInputStream(file);
				data=new byte[CHUNKSIZE];
				status=in.read(data);
				System.out.println("\n\nServer read: "+status+"\n\n");
				//System.out.println(new String(data));
				if(status != CHUNKSIZE)
				{
					//throw USERDEFINEDEXCEPTION-not 64k chunk 
				}
			}
			else
			{
				//throw CHUNK NOT FOUND EXCEPTION 
			}
			
			file=null;
			if(in!=null)
				in.close();
		}
		return data;
	}


}
