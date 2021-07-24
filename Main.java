import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.zip.*;
import java.security.*;

//A megjegyzések addig maradnak bent amíg meg nem tanulok programozni(addig ne is vedd ki õket)

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// Elsõ lépés: Megkeresni az MTA telepítési helyét registry használatával.
		String path = WinRegistry.readString (
			    WinRegistry.HKEY_LOCAL_MACHINE,
			   "SOFTWARE\\WOW6432Node\\Multi Theft Auto: San Andreas All\\1.5",
			   "Last Install Location");
		path += "\\MTA\\logs";
		System.out.println("MTA log mappája = " + path);
		
		//A registryben el van mentve néhány infó ami a program mûködéséhez kell, 
		//ez a kódsor létrehozza a kulcsokat a registryben.
		WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, "SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver");

		String[] originalLogList = {"console.log.5","console.log.4","console.log.3","console.log.2","console.log.1","console.log"};
		//Az eredeti log fájlok elmentése
		File consolelog = new File(path+"\\console.log");
		File consolelog1 = new File(path+"\\console.log.1");
		File consolelog2 = new File(path+"\\console.log.2");
		File consolelog3 = new File(path+"\\console.log.3");
		File consolelog4 = new File(path+"\\console.log.4");
		File consolelog5 = new File(path+"\\console.log.5");
		
		//a "backup" mappa létrehozása
		File backupfolder = new File(path + "\\backup");
		backupfolder.mkdir();
		File[] filelist = backupfolder.listFiles();
		
		if(filelist.length == 0) {
			zipFile(consolelog.getPath(), backupfolder.getPath() + "\\console.log.1.zip");
			zipFile(consolelog1.getPath(), backupfolder.getPath() + "\\console.log.2.zip");
			zipFile(consolelog2.getPath(), backupfolder.getPath() + "\\console.log.3.zip");
			zipFile(consolelog3.getPath(), backupfolder.getPath() + "\\console.log.4.zip");
			zipFile(consolelog4.getPath(), backupfolder.getPath() + "\\console.log.5.zip");
			zipFile(consolelog5.getPath(), backupfolder.getPath() + "\\console.log.6.zip");
			/*BasicFileAttributes fatr = Files.readAttributes(consolelog.toPath(), 
	                BasicFileAttributes.class);
			WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, 
					"SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver", 
					"NewestLogCreationTime", fatr.lastModifiedTime().toString());*/
			WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, 
					"SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver", 
					"LastLogID", "6");
			return;
		} else {
			/*String newestLogCreateTime = WinRegistry.readString (
		    		WinRegistry.HKEY_LOCAL_MACHINE,
		   			"SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver",
		   			"NewestLogCreationTime");*/
			int LastLogID = Integer.parseInt(WinRegistry.readString (
		    		WinRegistry.HKEY_LOCAL_MACHINE,
		   			"SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver",
		   			"LastLogID"));
			int i = 0;
			/*for(String fileName : originalLogList) {
				BasicFileAttributes fatr = Files.readAttributes(new File(path + "\\" + fileName).toPath(), 
		                BasicFileAttributes.class);
				if(fatr.lastModifiedTime().toString() == newestLogCreateTime) {
					return;
				}
				i++;
			}*/
			if(i == 0) { //azért 0 mert fos vagyok prgoramozásban :'D
				zipFile(consolelog.getPath(), backupfolder.getPath() + "\\console.log." + (LastLogID + 1) + ".zip");
				zipFile(consolelog1.getPath(), backupfolder.getPath() + "\\console.log." + (LastLogID + 2) + ".zip");
				zipFile(consolelog2.getPath(), backupfolder.getPath() + "\\console.log." + (LastLogID + 3) + ".zip");
				zipFile(consolelog3.getPath(), backupfolder.getPath() + "\\console.log." + (LastLogID + 4) + ".zip");
				zipFile(consolelog4.getPath(), backupfolder.getPath() + "\\console.log." + (LastLogID + 5) + ".zip");
				zipFile(consolelog5.getPath(), backupfolder.getPath() + "\\console.log." + (LastLogID + 6) + ".zip");
				/*BasicFileAttributes fatr = Files.readAttributes(consolelog.toPath(), 
		                BasicFileAttributes.class);
				WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, 
						"SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver", 
						"NewestLogCreationTime", fatr.lastModifiedTime().toString());*/
				WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, 
						"SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver", 
						"LastLogID", Integer.toString(LastLogID + 6));
				return;
			} else {/*
				int k = 1;
				for (int j = i + 1; j < 6; j++) {
					k++;
					zipFile(new File(path + originalLogList[j]).getPath(), backupfolder.getPath() + "\\console.log." + (LastLogID + k) + ".zip");
				}
				BasicFileAttributes fatr = Files.readAttributes(consolelog.toPath(), 
		                BasicFileAttributes.class);
				WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, 
						"SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver", 
						"NewestLogCreationTime", fatr.lastModifiedTime().toString());
				WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, 
						"SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver", 
						"LastLogID", Integer.toString(LastLogID + k));
				*/
			}
		} 
	}
	
	
	public static void zipFile(String filePath, String destinationPath) {
		try {
			if(!Files.exists(Paths.get(destinationPath), LinkOption.NOFOLLOW_LINKS)) Files.createFile(Paths.get(destinationPath));
            File file = new File(filePath);
 
            FileOutputStream fos = new FileOutputStream(destinationPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
 
            zos.putNextEntry(new ZipEntry(file.getName()));
 
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            zos.write(bytes, 0, bytes.length);
            zos.closeEntry();
            zos.close();
 
        } catch (FileNotFoundException ex) {
            System.err.format("The file %s does not exist", filePath);
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex);
        }
	}
	
	/* Felesleges kód amíg meg nem tanulok programozni :D
	public static byte[] createSha256(File file) throws Exception  {
	    byte[] buffer= new byte[8192];
	    int count;
	    MessageDigest digest = MessageDigest.getInstance("SHA-256");
	    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
	    while ((count = bis.read(buffer)) > 0) {
	        digest.update(buffer, 0, count);
	    }
	    bis.close();

	    byte[] hash = digest.digest();
	    return Base64.getEncoder().encode(hash);
	}*/
}