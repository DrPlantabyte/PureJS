/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package purejs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;

/**
 *
 * @author CCHall
 */
public class FileIO {
	
	private static Map<String,JFileChooser> fileChooserCache = new HashMap<>();
	
	public static String askForFile(String title){
		JFileChooser fileChooser = fileChooserCache.get(title);
		if(fileChooser == null){
			fileChooser = new JFileChooser();
			fileChooser.setDialogTitle(title);
			fileChooserCache.put(title, fileChooser);
		}
		int action = fileChooser.showOpenDialog(null);
		if(action != JFileChooser.CANCEL_OPTION && fileChooser.getSelectedFile() != null){
			return fileChooser.getSelectedFile().getPath();
		} else {
			return null;
		}
	}
	
	public static String readFile(String filePath) throws IOException{
		File f = new File(filePath);
		BufferedReader in = new BufferedReader(new FileReader(f));
		StringBuilder buffer = new StringBuilder();
		String line = in.readLine();
		while(line != null){
			buffer.append(line).append("\n");
			line = in.readLine();
		}
		in.close();
		return buffer.toString();
	}
	
	public static Table readFileAsTable(String filePath) throws IOException{
		File f = new File(filePath);
		Path p = f.toPath();
		List<String> allLines = Files.readAllLines(p, Charset.forName("UTF-8"));
		Table t = new Table();
		t.parse(allLines);
		return t;
	}
}
