/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package purejs.util;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author CCHall
 */
public class DataFile {
	public DataFile(String filepath) throws FileNotFoundException{
		File f = new File(filepath);
		if(f.exists() == false || f.isDirectory()){ // sanity check
			throw new FileNotFoundException("Failed to open file '"+filepath+"'");
		}
		// TODO: load file to 2D array
	}
}
