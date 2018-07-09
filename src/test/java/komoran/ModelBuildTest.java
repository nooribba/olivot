package komoran;
/*******************************************************************************
 * olivot komoran test class
 *******************************************************************************/


import kr.co.shineware.nlp.komoran.modeler.builder.ModelBuilder;

import java.io.File;

public class ModelBuildTest {
	private static String userDicPath = null;

	@SuppressWarnings("deprecation")
	private static void modelLoad() {
		ModelBuilder builder = new ModelBuilder();
		builder.load("models_light");
		System.out.println("models_light LOAD COMPLETE");
		builder.load("models_full");
		System.out.println("models_full LOAD COMPLETE");
	}

	private static void modelSave(boolean includeWikiTitle) {
		ModelBuilder builder = new ModelBuilder();
		if(includeWikiTitle) {
			builder.setExternalDic("user_data/wiki.titles");
		}
		builder.buildPath("corpus_build");

		String modelPath = "models";
		if(includeWikiTitle){
			modelPath += "_full";
		}else{
			modelPath += "_light";
		}
		builder.save(modelPath);
		System.out.println(modelPath+" SAVE COMPLETE");
	}
	
	public static void main(String[] args) {
		modelSave(false);//light
		modelSave(true);//full
		modelLoad();
	}

}
