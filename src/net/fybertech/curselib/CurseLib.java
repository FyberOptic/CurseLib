package net.fybertech.curselib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;


public class CurseLib 
{
	/** User agent for HTTP requests */
	public static final String USER_AGENT = "CurseClient/7.1 (Microsoft Windows NT 6.1.7601 Service Pack 1) CurseClient/7.1.6018.41463";
	
	/** Server used for HTTP requests */
	public static final String CURSE_HOST = "clientupdate-v6.cursecdn.com";
	
	/** Local directory for cached files */
	public static final String CACHE_LOCATION = "cache";
	
	/** Local directory for Curse database files */
	public static final String DATABASE_LOCATION = CACHE_LOCATION + File.separator + "database";
	
	/** Local directory for mod/modpack/etc archives */
	public static final String FILECACHE_LOCATION = CACHE_LOCATION + File.separator + "files";
	
	
	
	/**  
	 * Curse stores its JSON database in four separate files to reduce
	 * bandwidth for client updates.  We only download one when it's out 
	 * of date and merge those down into the COMPLETE set if newer than its
	 * specified timestamp.
	 */
	public static enum EnumDatabaseType 
	{
		COMPLETE,
		WEEKLY,
		DAILY,
		HOURLY;
		
		
		/**
		 * Gets string used in requests.
		 */
		public String getUrlSlug() {
			return this.toString().toLowerCase();
		}

		/**
		 * Returns filename of the database.
		 */
		public String getFilename() {
			return this.toString().toLowerCase() + ".json";
		}

		/**
		 * Returns a File object to the local database file.
		 */
		public File getFile() {
			return new File(DATABASE_LOCATION, getFilename());
		}

		/**
		 * Returns the URL needed to request the timestamp of the latest 
		 * version of this database.
		 */
		public String getQueryUrl() {
			return "http://" + CurseLib.CURSE_HOST + "/feed/addons/432/v10/" + this.getUrlSlug() + ".json.bz2.txt";
		}

		/**
		 * Returns the URL needed to request the database for the specified version.
		 */
		public String getDownloadUrl(long version) {
			return "http://" + CurseLib.CURSE_HOST + "/feed/addons/432/v10/" + this.getUrlSlug() + ".json.bz2?t=" + version;
		}
	}
	
	
	/**
	 * Downloads a file as the specified destination filename.
	 */
	public static File downloadFile(String urlstring, String destination) throws IOException
	{
		return downloadFile(urlstring, new File(destination), false);
	}
	

	/**
	 * Downloads a file either to a specified location or as the specified filename.
	 * 
	 * If discoverFilename is true, the filename will automatically be determined
	 * after the HTTP request is made, and downloaded to the provided destination.
	 * 
	 * If discoverFilename false, the destination path must include the output 
	 * filename.
	 */
	public static File downloadFile(String urlstring, File destination, boolean discoverFilename)  throws IOException
	{
		String filename = urlstring.substring(urlstring.lastIndexOf("/"));
		
		URL url = new URL(urlstring);
		
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent", CurseLib.USER_AGENT);
		InputStream is = connection.getInputStream();
		
		if (discoverFilename) {
			String newURL = connection.getURL().toString();
			filename = newURL.substring(newURL.lastIndexOf('/') + 1, newURL.length());
		}
		
		File outfile = new File(destination + File.separator + filename);
		FileOutputStream ostream = new FileOutputStream(outfile);
		
		byte[] downloadbuffer = new byte[1024];
		int count;
		while((count = is.read(downloadbuffer)) != -1)
		{
			ostream.write(downloadbuffer, 0, count);
		}
		
		ostream.close();
		is.close();
		
		return outfile;
	}


	
	/**
	 * Request a URL and return a string of the resulting output.
	 */
	public static String downloadString(String urlstring)
	{
		byte[] data = downloadData(urlstring);
		if (data == null) return null;
		
		return new String(data);
	}
	
	
	
	/**
	 * Request a URL and return the resulting data as a byte array.
	 */
	public static byte[] downloadData(String urlstring)
	{
		byte[] output = null;
		
		try {
			URL url = new URL(urlstring);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty("User-Agent", CurseLib.USER_AGENT);			
			InputStream is = connection.getInputStream();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			byte[] downloadbuffer = new byte[1024];
			int count;
			while((count = is.read(downloadbuffer)) != -1)
			{
				out.write(downloadbuffer, 0, count);
			}		
			
			out.close();
			is.close();
			
			output = out.toByteArray();
		}
		catch (Exception e) {			
		}
		
		return output;
	}
	
	
	
	/**
	 * Writes all possible data from an InputStream to a byte array.
	 */
	public static byte[] writeStreamToArray(InputStream istream) throws IOException
	{	
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		
		byte[] downloadbuffer = new byte[1024];
		int count;
		while((count = istream.read(downloadbuffer)) != -1)
		{
			ostream.write(downloadbuffer, 0, count);
		}
		
		ostream.close();		
		
		return ostream.toByteArray();
	}	
	
	
	/**
	 * Queries Curse for the timestamp of the latest database of the specified type.
	 */
	public static long getLatestDatabaseVersion(EnumDatabaseType dbType)
	{
		String version = downloadString(dbType.getQueryUrl());
		long lversion = 0;		
		try {
			lversion = Long.parseLong(version);
		}
		catch (Exception e) {}
		return lversion;
	}	
	
	
	/** 
	 * Downloads a Curse database of the specified type for the specified timestamp
	 * into the database cache. 
	 * 
	 * Returns true if successful. 
	 */
	public static boolean downloadDatabase(long version, EnumDatabaseType dbType)
	{
		if (version == 0) return false;
		
		File destination = dbType.getFile();
		
		File destPath = destination.getParentFile();
		if (destPath != null && !destPath.exists()) destPath.mkdirs();
		
		byte[] compressedDatabase = downloadData(dbType.getDownloadUrl(version));
		
		if (compressedDatabase == null) return false;
		
		try {
			BZip2CompressorInputStream bz2 = new BZip2CompressorInputStream(new ByteArrayInputStream(compressedDatabase));
			byte[] db = writeStreamToArray(bz2);
		
			FileOutputStream stream = new FileOutputStream(destination);
			try {
				stream.write(db);
			} finally {
				stream.close();
			}
			
			return true;
		}
		catch (Exception e) {}
		
		return false;
	}
	
	
	/**
	 * Returns true if a Curse database of the specified type is already in 
	 * the cache folder.
	 */
	public static boolean doesDatabaseExist(EnumDatabaseType dbType) 
	{		
		return dbType.getFile().exists();		
	}
	
	
}
