package net.fybertech.curselib.database;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.fybertech.curselib.CurseLib;
import net.fybertech.curselib.database.manifest.CurseManifest;

public class CurseDatabase 
{
	/** JSON Field - Current timestamp of this database */
	private long timestamp;
	
	/** JSON Field - Array of projects in this database */
	private CurseProject[] data;
	
	
	
		
	/** A list of parsed project sections, such as "Mods", "Modpacks", "Texture Packs", etc. */ 
	private List<String> sections = new ArrayList<>();
	/** Projects sorted by section. */
	private Map<String, List<CurseProject>> dataBySection = new HashMap<>();
	
	/** A list of parsed game versions, such as "1.7.10" */
	private List<String> versions = new ArrayList<>();	
	/** Projects sorted by version. */
	private Map<String, List<CurseProject>> dataByVersion = new HashMap<>();
	
	/** A list of parsed categories, such as "Redstone". */
	private List<String> categories = new ArrayList<>();
	/** Projects sorted by category. NOTE: Uses lower-cased key! */
	private Map<String, List<CurseProject>> dataByCategory = new HashMap<>();
	
	/** 
	 * A Map<File ID, Owner ID> to make it easier to find the associated
	 * project belonging to a file ID. 
	 */
	public Map<Integer, Integer> files = new HashMap<>();
	
	
	
	/**
	 * Getter for the database timestamp.
	 */
	public long getDatabaseVersion()
	{
		return timestamp;
	}
	
	
	/**
	 * Returns a list of all projects in the database.
	 */
	public List<CurseProject> getAllProjects() {
		return Arrays.asList(this.data);
	}	
	
	
	/** 
	 * Returns a list of parsed project sections, such as "Mods", "Modpacks",
	 * "Texture Packs", etc.
	 */
	public List<String> getSections() {
		return this.sections;
	}
	
	/**
	 * Returns a list of all projects in the specified section.
	 */
	public List<CurseProject> getProjectsBySection(String section) {
		return dataBySection.get(section);
	}
	
	
	/** 
	 * Returns a list of parsed game versions, such as "1.7.10".
	 */
	public List<String> getVersions() {
		return this.versions;
	}
	
	/**
	 * Returns a list of all projects with files matching the specified 
	 * version.
	 */
	public List<CurseProject> getProjectsByVersion(String version) {
		return dataByVersion.get(version);
	}
	
	
	/**
	 * Returns a list of parsed categories, such as "Redstone".
	 */
	public List<String> getCategories() {
		return this.categories;
	}
	
	/**
	 * Returns a list of all projects matching the specified category.
	 */
	public List<CurseProject> getProjectsByCategory(String category) {
		return dataByCategory.get(category.toLowerCase());
	}
	
	
	/**
	 * Returns a sorted list of all parsed game versions.
	 */
	public List<String> getSortedVersions()
	{
		List<String> list = new ArrayList<>(versions);	  
		java.util.Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				String o1Lower = o1.toLowerCase();
				String o2Lower = o2.toLowerCase();
				
				if (o1Lower.startsWith("beta") && !o2Lower.startsWith("beta")) return -1;
				if (!o1Lower.startsWith("beta") && o2Lower.startsWith("beta")) return 1;
				
				String[] split1 = o1Lower.split("[\\.|\\-]");
				String[] split2 = o2Lower.split("[\\.|\\-]");			
				
				int min = Math.max(split1.length,split2.length);
				
				for (int n = 0; n < min; n++) {				
					int a1 = split1.length > n && "snapshot".equals(split1[n]) ? -1 : 0;
					int b1 = split2.length > n && "snapshot".equals(split2[n]) ? -1 : 0;
					try { a1 = Integer.parseInt(split1[n]); } catch (Exception e) {}
					try { b1 = Integer.parseInt(split2[n]); } catch (Exception e) {}
					if (a1 < b1) return -1;
					if (a1 > b1) return 1;
				}	
				
				return o1.compareTo(o2);
			}		  
		});
		return list;
	}



	/**
	 * Filters the specified list by section and returns the resulting list.
	 */
	public List<CurseProject> filterBySection(String section, List<CurseProject> inputList)
	{
		if (section == null) return new ArrayList<>();		
		
		List<CurseProject> sectionList = dataBySection.get(section);		
		if (sectionList == null) return new ArrayList<>();
		
		List<CurseProject> outputList = new ArrayList<>();
		if (inputList == null) inputList = Arrays.asList(data);
		  
		for (CurseProject d : inputList) {
			if (sectionList.contains(d)) outputList.add(d);
		}
		  
		return outputList;
	}
	
	
	/**
	 * Filters the specified list by category and returns the resulting list.
	 */
	public List<CurseProject> filterByCategory(String category, List<CurseProject> inputList)
	{
		if (category == null) return new ArrayList<>();
		category = category.toLowerCase();
		
		List<CurseProject> categoryList = dataByCategory.get(category);		
		if (categoryList == null) return new ArrayList<>();
		
		List<CurseProject> outputList = new ArrayList<>();		
		if (inputList == null) inputList = Arrays.asList(data);
		
		for (CurseProject d : inputList) {
			if (categoryList.contains(d)) outputList.add(d);
		}
		  
		return outputList;
	}
	
	
	/**
	 * Filters the specified list by project name and returns the resulting list.
	 */
	public List<CurseProject> filterByName(String name, List<CurseProject> inputList)
	{
		List<CurseProject> outputList = new ArrayList<>();	
		if (inputList == null) inputList = Arrays.asList(data);
		name = name.toLowerCase();
		  
		for (CurseProject d : inputList) {
			if (d.Name.toLowerCase().contains(name)) outputList.add(d);
		}
		  
		return outputList;
	}
	
	
	/**
	 * Filters the specified list by author and returns the resulting list.
	 */
	public List<CurseProject> filterByAuthor(String name, List<CurseProject> inputList)
	{
		List<CurseProject> outputList = new ArrayList<>();	
		if (inputList == null) inputList = Arrays.asList(data);
		name = name.toLowerCase();
		  
		for (CurseProject d : inputList) {
			if (d.PrimaryAuthorName.toLowerCase().contains(name)) outputList.add(d);
			else {
				for (CurseAuthor author : d.Authors) {
					if (author.Name.toLowerCase().contains(name)) { outputList.add(d); break; }
				}
			}
		}
		  
		return outputList;
	}
	
	
	/**
	 * Filters the specified list by version and returns the resulting list.
	 */
	public List<CurseProject> filterByVersion(String version, List<CurseProject> inputList)
	{
		if (version == null) return new ArrayList<>();
		
		List<CurseProject> versionList = dataByVersion.get(version);		
		if (versionList == null) return new ArrayList<>();	

		List<CurseProject> outputList = new ArrayList<>();		
		if (inputList == null) inputList = Arrays.asList(data);
		
		for (CurseProject d : inputList) {
			if (versionList.contains(d)) outputList.add(d);
		}
		  
		return outputList;
	}
	
	
	
	/**
	 * Applies a filter type to the full database and returns the resulting 
	 * list.
	 */
	public List<CurseProject> filter(CurseFilter filter)
	{
		return filter(filter, null);
	}
	
	
	/**
	 * Applies a filter type to a specified list of projects and returns the 
	 * resulting list.
	 */
	public List<CurseProject> filter(CurseFilter filter, List<CurseProject> currentList) 
	{
		switch (filter.filterType) {
			case AUTHOR:
				return filterByAuthor(filter.filterValue, currentList);
			case CATEGORY:
				return filterByCategory(filter.filterValue, currentList);
			case SECTION:
				return filterBySection(filter.filterValue, currentList);
			case NAME:
				return filterByName(filter.filterValue, currentList);
			case VERSION:
				return filterByVersion(filter.filterValue, currentList);
			default:
				break;
		}

		return currentList;
	}
	
	
	/**
	 * Applies a list of filter types to the full database, in the order 
	 * specified.
	 */
	public List<CurseProject> filter(List<CurseFilter> filters) 
	{
		List<CurseProject> output = Arrays.asList(data);
		for (CurseFilter filter : filters) output = filter(filter, output);
		return output;		
	}
	
	
	/**
	 * Processes all data in the database and categorizes it for quicker
	 * retrieval.
	 * 
	 * This isn't done automatically upon opening the database because you 
	 * likely will want to load multiple database types and compare the
	 * timestamps, so you only need to process the results after all
	 * the required databases are merged.
	 *  
	 */
	public void processDatabaseData()
	{
		sections.clear();
		dataBySection.clear();
		versions.clear();
		dataByVersion.clear();
		categories.clear();
		dataByCategory.clear();
		this.files.clear();
		
		
		for (CurseProject itemData : this.data) {
			if (itemData.CategorySection != null) {
				String section = itemData.CategorySection.Name;
				if (!sections.contains(section)) {
					sections.add(section);
					dataBySection.put(section, new ArrayList<CurseProject>());					
				}				
				List<CurseProject> list = dataBySection.get(section);
				list.add(itemData);
			}			
			
			
			if (itemData.GameVersionLatestFiles != null) {
				for (CurseFileStub cf : itemData.GameVersionLatestFiles) {
					String ver = cf.GameVesion;
					if (cf.GameVesion == null) continue;					
					
					if (!versions.contains(ver)) {
						versions.add(ver);
						dataByVersion.put(ver, new ArrayList<CurseProject>());							
					}
					
					files.put(cf.ProjectFileID, itemData.Id);
					
					List<CurseProject> list = dataByVersion.get(ver);
					list.add(itemData);
				}
			}
			
			
			if (itemData.LatestFiles != null) {
				for (CurseFile cf : itemData.LatestFiles) {
					if (cf.GameVersion == null) continue;
					for (String ver : cf.GameVersion) {
						if (!versions.contains(ver)) {
							versions.add(ver);
							dataByVersion.put(ver, new ArrayList<CurseProject>());							
						}
						List<CurseProject> list = dataByVersion.get(ver);
						list.add(itemData);
					}					
					
					files.put(cf.Id, itemData.Id);
				}
			}
			
			
			if (itemData.PrimaryCategoryName != null) {
				String category = itemData.PrimaryCategoryName;
				
				if (!categories.contains(category)) {
					categories.add(category);
					dataByCategory.put(category.toLowerCase(), new ArrayList<CurseProject>());
				}
				
				List<CurseProject> list = dataByCategory.get(category.toLowerCase());
				list.add(itemData);
			}
			
			if (itemData.Categories != null) {
				for (CurseCategory cat : itemData.Categories) {
					String category = cat.Name;
					
					if (!categories.contains(category)) {
						categories.add(category);
						dataByCategory.put(category.toLowerCase(), new ArrayList<CurseProject>());
					}
					
					List<CurseProject> list = dataByCategory.get(category.toLowerCase());
					list.add(itemData);
				}
			}
		}
		
		Collections.sort(sections);
		Collections.sort(categories);
	}
	
	
	/**
	 * Opens the specified database JSON file and returns a database object.
	 * 
	 * Optionally you can have it automatically process the data if you don't 
	 * intend to merge the database with any others.
	 */
	public static CurseDatabase Open(File dbFile, boolean processData) throws JsonSyntaxException, JsonIOException, IOException
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		
		FileReader reader = new FileReader(dbFile);
		CurseDatabase db = gson.fromJson(reader, CurseDatabase.class);
		reader.close();
		
		if (db != null && processData) db.processDatabaseData();
		
		return db;
	}	
	
	
	/**
	 * Returns a project by its numeric ID, or null if not found.
	 */
	public CurseProject getProjectById(int id) 
	{
		if (id == -1) return null;
		
		for (CurseProject item : data) {
			if (item.Id == id) return item;
		}
		
		return null;
	}

	
	/**
	 * Returns the alphanumeric name for the requested project ID that's 
	 * used in most CurseForge URLs.
	 */
	public String getProjectSlug(int projectID) 
	{
		CurseProject item = getProjectById(projectID);
		return item.getProjectSlug();
	}
	

	/**
	 * Finds or downloads the specified file for the specified project.
	 * 
	 * @return A File object to the requested file, or null.
	 */
	public File getFileFromCache(int projectID, int fileID)
	{
		String slug = getProjectSlug(projectID);
		
		String url = "http://minecraft.curseforge.com/projects/" + slug + "/files/" + fileID + "/download";
		// TODO - Remove debug output
		System.out.println("File URL: " + url);
		
		File outputFile = null;
		
		File destinationPath = new File(CurseLib.FILECACHE_LOCATION, projectID + "/" + fileID + "/");		
		
		if (!destinationPath.exists()) destinationPath.mkdirs();
		else {
			File[] files = destinationPath.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String filename = pathname.getName().toLowerCase();
					return (filename.endsWith(".jar") || filename.endsWith(".zip"));
				}				
			});
			
			if (files.length > 0) {
				outputFile = files[0];
				System.out.println("Using existing file " + outputFile);
			}
		}
		
		
		if (outputFile == null) {
			try {
				System.out.print("Downloading...");
				outputFile = CurseLib.downloadFile(url, destinationPath, true);
				System.out.println("done");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return outputFile;
	}
	

	/**
	 * Returns a modpack manifest for the specified project ID, or null.
	 * 
	 * Will download the pack archive if it doesn't exist.
	 */
	public CurseManifest getModpackManifest(int id) 
	{
		CurseProject parent = getParentProjectOfFile(id);
		if (!parent.isModpack()) throw new RuntimeException("File ID " + id + " doesn't belong to a modpack!");
		
		File modpackFile = getFileFromCache(parent.Id, id);				
		if (modpackFile == null) return null;
		
		String manifestString = null;
		
		ZipFile zip = null;
		try {
			zip = new ZipFile(modpackFile);		
		
			for (Enumeration<? extends ZipEntry> en = zip.entries(); en.hasMoreElements();) {
				ZipEntry entry = en.nextElement();
				
				if (entry.getName().toLowerCase().equals("manifest.json")) {
					try {
						manifestString = new String(CurseLib.writeStreamToArray(zip.getInputStream(entry)));
					} catch (IOException e) {					
						e.printStackTrace();
					}
				}
			}							
		} catch (Exception e) {
			e.printStackTrace();
		}		
		finally {			
			try {
				zip.close();
			} catch (Exception e) {}
		}
		
		
		if (manifestString != null) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			Gson gson = gsonBuilder.create();			
			CurseManifest manifest = gson.fromJson(manifestString, CurseManifest.class);
			return manifest;
		}
		
		return null;
	}


	/**
	 * Returns the parent project ID for the specified file ID
	 */
	public int getParentProjectIdOfFile(int fileId) 
	{
		return files.get(fileId);
	}


	/**
	 * Returns the parent project for the specified file ID.
	 */
	public CurseProject getParentProjectOfFile(int fileId) {
		return getProjectById(getParentProjectIdOfFile(fileId));
	}



	/** 
	 * Merges the specified database with the current one.
	 */
	public void mergeDatabase(CurseDatabase extraDB) 
	{
		if (extraDB == null || (extraDB.getDatabaseVersion() <= this.getDatabaseVersion())) return;
		
		List<CurseProject> thisList = new ArrayList<>(Arrays.asList(this.data));
		
		for (CurseProject itemExtra : extraDB.data) 
		{
			boolean replaced = false;
			
			int thisListSize = thisList.size();
			for (int n = 0; n < thisListSize; n++) {
				CurseProject itemThis = thisList.get(n);
				if (itemThis.Id == itemExtra.Id) {
					thisList.set(n,  itemExtra);
					replaced = true;
					//replaceCount++;
					break;
				}
			}
			
			if (replaced) continue;
			
			thisList.add(itemExtra);
		}
		
		this.data = thisList.toArray(new CurseProject[0]);		
	}


	
	@Override
	public String toString()
	{
		return "Database [timestamp=" + timestamp + ", data=" + data + "]";
	}	
}
