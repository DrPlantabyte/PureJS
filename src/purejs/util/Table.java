/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package purejs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A data structure for turning text into a table.
 * @author CCHall
 */
public class Table {
	private String delimiter = "\t";
	private String comment = "#";
	private boolean firstRowHeader = false;
	private final List<String[]> data;
	/** [header,column index] */
	private final Map<String,Integer> headerMap;
	/**
	 * Creates a data table instance ready to parse data from provided Strings.
	 * @param cellDelimiter The string to use to delimit cells in a row
	 * @param commentPrefix The string that indicates that a row is a comment
	 * @param headerFirstRow If true, the first row parsed will be stored as a 
	 * header AND NOT INSERTED INTO THE DATA TABLE.
	 */
	public Table(String cellDelimiter,String commentPrefix, boolean headerFirstRow){
		data = new ArrayList<>();
		headerMap = new HashMap<>();
		this.delimiter = cellDelimiter;
		this.comment = commentPrefix;
		firstRowHeader = headerFirstRow;
	}
	
	public int getSize(){
		return data.size();
	}
	/**
	 * Creates a default data table. The delimiter will be the tab character and 
	 * the number sign # as the comment prefix.
	 */
	public Table(){
		data = new ArrayList<>();
		headerMap = new HashMap<>();
	}
	
	public void setDelimiter(String d){
		delimiter = d;
	}
	
	public void setCommentPrefix(String p){
		comment = p;
	}
	public String getCommentPrefix(){
		return comment;
	}
	private boolean firstRow = true;
	/**
	 * Loads the provided text into the internal data table.
	 * @param rows An array/list of rows of text to be parsed and added to the 
	 * data table
	 */
	public void parse(List<String> rows){
		parse(rows.toArray(new String[rows.size()]));
	}
	/**
	 * Loads the provided text into the internal data table.
	 * @param rows An array/list of rows of text to be parsed and added to the 
	 * data table
	 */
	public void parse(String... rows){
		for(String row : rows){
			if(row == null){
				continue;
			}
			if(row.trim().isEmpty()){
				continue;
			}
			if(row.trim().startsWith(comment)){
				continue;
			}
			String[] cells = row.split(delimiter);
			if(firstRow && firstRowHeader){
				for(int i = 0; i < cells.length; i++){
					headerMap.put(cells[i], i);
				}
				firstRow = false;
			} else {
				data.add(cells);
			}
		}
	}
	/**
	 * Gets the value at a specified location in the data table. Note that the 
	 * enumeration starts at 0, not 1. If the specified cell does not exist, an 
	 * empty string is returned.
	 * @param col Column index (first column has index 0)
	 * @param row Row index (first row has index 0)
	 * @return The String stored in the cell at the given location
	 */
	public String get(int col, int row) throws IndexOutOfBoundsException{
		if(row < 0 || row >= data.size()){
			return "";
		}
		String[] cells = data.get(row);
		if(col < 0 || col >= cells.length){
			return "";
		}
		return cells[col];
	}
	/**
	 * Gets the value at a specified location in the data table. Note that the 
	 * enumeration starts at 0, not 1. If the specified cell does not exist, an 
	 * empty string is returned.
	 * @param colHeader Column title to look-up
	 * @param row Row index (first row has index 0)
	 * @return The String stored in the cell at the given location
	 * @throws IndexOutOfBoundsException Thrown if the specified header column 
	 * does not exist.
	 */
	public String get(String colHeader,int row) throws IndexOutOfBoundsException{
		Integer col = headerMap.get(colHeader);
		if(col == null){
			throw new IndexOutOfBoundsException("Column with header '"
					+ colHeader + "' does not exist in dataset. Acceptable "
					+ "headers are:\n\t"+toString(headerMap.keySet()));
		}
		return data.get(row)[col];
	}
	/**
	 * Gets a cell from the table and parses it as a number.
	 * @param col Column index (first column has index 0)
	 * @param row Row index (first row has index 0)
	 * @return The numerical value of the String stored in the cell at the given location
	 * @throws IndexOutOfBoundsException Thrown if the cell lies outside the bounds of the table
	 * @throws NumberFormatException Thrown if the content of the cell is not formatted correctly
	 */
	public double getAsNumber(int col, int row) throws IndexOutOfBoundsException, NumberFormatException{
		String number = get(col,row);
		if(number.length() == 0){
			throw new IndexOutOfBoundsException("Cell ("+col+","+row+") could not be parsed as a number because it does not exist");
		}
		return Double.parseDouble(number);
	}
	/**
	 * Gets a cell from the table and parses it as a number.
	 * @param colHeader Column title to look-up
	 * @param row Row index (first row has index 0)
	 * @return The numerical value of the String stored in the cell at the given location
	 * @throws IndexOutOfBoundsException Thrown if the cell lies outside the 
	 * bounds of the table or if the specified header column does not exist.
	 * @throws NumberFormatException Thrown if the content of the cell is not formatted correctly
	 */
	public double getAsNumber(String colHeader,int row) throws IndexOutOfBoundsException, NumberFormatException{
		String number = get(colHeader,row);
		if(number.length() == 0){
			throw new IndexOutOfBoundsException("Cell ("+colHeader+","+row+") could not be parsed as a number because it does not exist");
		}
		return Double.parseDouble(number);
	}
	public String getDelimiter(){
		return delimiter;
	}
	/**
	 * Slices this table into a series of smaller tables, each with a number of 
	 * rows equal to the provided slice size.
	 * @param sliceSize The number of rows in each table slice
	 * @return Several smaller tables
	 */
	public List<Table> slice(int sliceSize){
		if (sliceSize <= 0){
			throw new IllegalArgumentException("Cannot slice table into slices with size "+sliceSize);
		}
		List<Table> slices  = new ArrayList<>();
		int row = 0;
		int tableSlice = -1;
		while(row < data.size()){
			if((row / sliceSize) > tableSlice){
				tableSlice++;
				slices.add(new Table(this.delimiter,this.comment, this.firstRowHeader));
				slices.get(tableSlice).firstRow = false;
				if(firstRowHeader){
					for(String key : headerMap.keySet()){
						slices.get(tableSlice).headerMap.put(key, this.headerMap.get(key));
					}
				}
			}
			slices.get(tableSlice).data.add(this.data.get(row));
			row++;
		}
		return slices;
	}
	
	protected final String toString(String[] strarr){
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(String s : strarr){
			if(!first){
				sb.append(delimiter);
			}
			sb.append(s);
		}
		return sb.toString();
	}
	protected final String toString(Set<String> strset){
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(String s : strset){
			if(!first){
				sb.append(delimiter);
			}
			sb.append(s);
		}
		return sb.toString();
	}
	
	public void print(Writer out) throws IOException{
		if(firstRowHeader){
			out.append(toString(headerMap.keySet())).append("\r\n");
		}
		
		
		for(int row = 0; row < data.size(); row++){
			out.append(toString(data.get(row))).append("\r\n");
		}
	}
	/**
	 * Serializes the table as a String
	 * @return The table in a format determined by the delimiter 
	 */
	@Override
	public String toString(){
		StringWriter sw = new StringWriter();
		try{print(sw);}catch(IOException ex){}
		sw.flush();
		return sw.toString();
	}
}
