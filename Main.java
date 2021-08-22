import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

public class Main {

	public static void main(String[] args) throws Exception {
		// Első lépés: Megkeresni az MTA telepítési helyét registry használatával.
		String path = WinRegistry.readString (
			    WinRegistry.HKEY_LOCAL_MACHINE,
			   "SOFTWARE\\WOW6432Node\\Multi Theft Auto: San Andreas All\\1.5",
			   "Last Install Location");
		path += "\\MTA\\logs";
		System.out.println("MTA log mappája = " + path);
		
		//A registryben el van mentve néhány infó ami a program működéséhez kell, 
		//ez a kódsor létrehozza a kulcsokat a registryben.
		WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, "SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver");

		String[] originalLogList = {"console.log","console.log.1","console.log.2","console.log.3","console.log.4","console.log.5"};
		
		//a "backup" mappa létrehozása
		File backupfolder = new File(path + "\\backup");
		backupfolder.mkdir();
		File[] filelist = backupfolder.listFiles();
		
		if(filelist.length == 0) {
			int i = 1;
			for(String fileName : originalLogList) {
				decode(path + "\\" + fileName, path);
				zipFile(System.getProperty("java.io.tmpdir") + "\\" + fileName, backupfolder.getPath() + "\\console.log." + i + ".zip");
				i++;
			}
			WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, 
					"SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver", 
					"LastLogID", Integer.toString(6));
			WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, 
					"SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver", 
					"LastLogID", "6");
		} else {
			int LastLogID = Integer.parseInt(WinRegistry.readString (
		    		WinRegistry.HKEY_LOCAL_MACHINE,
		   			"SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver",
		   			"LastLogID"));
			int i = 1;
			for(String fileName : originalLogList) {
				decode(path + "\\" + fileName, path);
				zipFile(System.getProperty("java.io.tmpdir") + "\\" + fileName, backupfolder.getPath() + "\\console.log." + (LastLogID + i) + ".zip");
				i++;
			}
			WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, 
					"SOFTWARE\\WOW6432Node\\MesterMan03\\MTALogSaver", 
					"LastLogID", Integer.toString(LastLogID + 6));
		}
		System.out.println("A logokat sikerült elmenteni, a kilépéshez nyomd meg az Entert...");
		System.in.read();
	}
	
	//MTA log javító(bugos log esetén) by Spatulka
	public static void decode(String filename, String path) {
		//Létrehoz egy új ÜRES fájlt. A fájl neve ugyanaz lesz, amit a filename
		//Váltózonak adnak át, viszont a fájl helye a temp mappában lesz
		Path filepath = Paths.get(System.getProperty("java.io.tmpdir") + "\\" + new File(filename).getName());
		try {
			Files.deleteIfExists(filepath);
			Files.createFile(filepath);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//fileIn = eredeti log, fileOut = javított log
		File fileIn = new File(filename);
		File fileOut = new File(System.getProperty("java.io.tmpdir") + "\\" + new File(filename).getName());
		
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileOut), "UTF-8");
			String line;
			System.out.println(fileIn.getName() + " beolvasása és bugos karakterek javítása folyamatban...");
			BufferedReader bufferReader = new BufferedReader(new FileReader(fileIn));
	        while ((line = bufferReader.readLine()) != null) {
	        	//Kis betűk
				line = line.replaceAll("Ã©", "é");
				line = line.replaceAll("Ã¡", "á");
				line = line.replaceAll("Ã³", "ó");
				line = line.replaceAll("Å‘", "ő");
				line = line.replaceAll("Ã¶", "ö");
				line = line.replaceAll("Å±", "ű");
				line = line.replaceAll("Ãº", "ú");
				line = line.replaceAll("Ã¼", "ü");
				line = line.replaceAll("Ã­", "í");
				
				//Nagy betűk
				//Csak ezzel a négy karakterrel működik, a többi javíthatatlan :(
				line = line.replaceAll("Ã‰", "É");
				line = line.replaceAll("Ã–", "Ö");
				line = line.replaceAll("Ãš", "Ú");
				line = line.replaceAll("Ãœ", "Ü");
				outputStreamWriter.write(line + "\n");
	        }
	        outputStreamWriter.flush();
			outputStreamWriter.close();
			bufferReader.close();
			System.out.println(fileIn.getName() + " sikeresen megtisztítva.");
			
		//Ennek soha az életben nem kéne megtörténnie, csak is akkor lehetséges
		//Ha nincs írási jog a temp mappában (bár biztos hogy lesz az admin jogok miatt...)
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Hiba történt. Részletek:");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Hiba történt. Részletek:");
			e.printStackTrace();
		}
	}
	
	//Ezt egy random weboldalon találtam, fingom nincs hogy működik :'D
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
}