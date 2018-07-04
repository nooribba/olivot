/*******************************************************************************
 * KOMORAN 3.0 - Korean Morphology Analyzer
 *
 * Copyright 2015 Shineware http://www.shineware.co.kr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 	
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package kr.co.shineware.nlp.komoran.modeler.model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import kr.co.shineware.nlp.komoran.interfaces.FileAccessible;

public class PosTable implements FileAccessible{
	
	//key = pos
	//value = id
	private Map<String,Integer> posIdTable;
	
	//key = id
	//value = pos
	private Map<Integer,String> idPosTable;
	
	public PosTable(){
		this.init();
	}
	
	private void init() {
		this.posIdTable = null;
		this.idPosTable = null;
		this.posIdTable = new HashMap<String, Integer>();
		this.idPosTable = new HashMap<Integer,String>();
	}
	
	public void put(String pos) {
		Integer id = posIdTable.get(pos);
		if(id == null){
			posIdTable.put(pos, posIdTable.size());
			idPosTable.put(idPosTable.size(), pos);
		}
	}
	
	public int getId(String pos){
		//System.out.println("##### pos:"+pos);
		return posIdTable.get(pos);
	}
	
	public String getPos(int id){
		return idPosTable.get(id);
	}
	
	public int size(){
		return posIdTable.size();
	}

	@Override
	public void save(String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			Set<Entry<String,Integer>> posIdEntrySet = posIdTable.entrySet();
			for (Entry<String, Integer> entry : posIdEntrySet) {
				bw.write(entry.getKey()+"\t"+entry.getValue());
				bw.newLine();
			}
			bw.close();
			bw = null;
			posIdEntrySet = null;
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/*@Override
	public void load(String filename) {
		try{
			this.init();
			System.out.println("##### load filename:"+filename);
			File file = new File(filename);
			System.out.println("##### load file complete");
			System.out.println("##### file absolute path:"+file.getAbsolutePath());
			System.out.println("##### file canoncial path:"+file.getCanonicalPath());
			System.out.println("##### file length:"+file.length());
			FileReader fr = new FileReader(file);
			System.out.println("##### set file reader complete");
			BufferedReader br = new BufferedReader(fr);
			System.out.println("##### set buffred reader complete");
			String line = null;
			while((line = br.readLine()) != null){
				String[] tokens = line.split("\t");
				this.posIdTable.put(tokens[0], Integer.parseInt(tokens[1]));
				this.idPosTable.put(Integer.parseInt(tokens[1]),tokens[0]);
			}
			br.close();
			br = null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/

	@Override
	public void load(String filename) {
		try{
			this.init();
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = null;
			while((line = br.readLine()) != null){
				String[] tokens = line.split("\t");
				this.posIdTable.put(tokens[0], Integer.parseInt(tokens[1]));
				this.idPosTable.put(Integer.parseInt(tokens[1]),tokens[0]);
			}
			br.close();
			br = null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void load(InputStream is) {
		try{
			this.init();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = null;
			while((line = br.readLine()) != null){
				String[] tokens = line.split("\t");
				this.posIdTable.put(tokens[0], Integer.parseInt(tokens[1]));
				this.idPosTable.put(Integer.parseInt(tokens[1]),tokens[0]);
			}
			br.close();
			br = null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void load(File file) {
		try{
			this.init();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine()) != null){
				String[] tokens = line.split("\t");
				this.posIdTable.put(tokens[0], Integer.parseInt(tokens[1]));
				this.idPosTable.put(Integer.parseInt(tokens[1]),tokens[0]);
			}
			br.close();
			br = null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
