package komoran;
/*******************************************************************************
 * olivot komoran test class
 *******************************************************************************/


import kr.co.shineware.nlp.komoran.modeler.builder.ModelBuilder;

import java.io.File;

public class ModelBuildTest {

//	public static void main(String[] args) {
//		String userDicPath = null;
//		String modelsFullPath = null;
//		String modelsLightPath = null;
//		String osName = null;
//		if(osName == null){
//        	osName = System.getProperty("os.name").toLowerCase();
//			System.out.println("##### osName:"+osName);
//        }
//        if(userDicPath == null || modelsFullPath == null || modelsLightPath == null){
//    	    if(osName.contains("window")){
//    		   userDicPath = "user_data"+File.separator;
//			   modelsFullPath = "models_full";
//			   modelsLightPath = "models_light";
//    	    }else{
//    		   userDicPath = "WEB-INF"+File.separator+"user_data"+File.separator;
//    		   modelsFullPath = "WEB-INF"+File.separator+"models_full";
//    		   modelsLightPath = "WEB-INF"+File.separator+"models_light";
//    	    }
//        }
//		
//		modelSave(false);
//		modelSave(true);
//		modelLoad();
//	}

	@SuppressWarnings("deprecation")
	private static void modelLoad() {
		ModelBuilder builder = new ModelBuilder();
		builder.load("models_light");
		builder.load("models_full");
		System.out.println("LOAD COMPLETE");
	}

	private static void modelSave(boolean includeWikiTitle) {
		ModelBuilder builder = new ModelBuilder();
		if(includeWikiTitle) {
			builder.setExternalDic("user_data" + File.separator + "wiki.titles");
		}
		builder.buildPath("corpus_build");

		String modelPath = "models";
		if(includeWikiTitle){
			modelPath += "_full";
			System.out.println("_full");
		}else{
			modelPath += "_light";
			System.out.println("_light");
		}
		builder.save(modelPath);
		System.out.println("SAVE COMPLETE");
	}

}
