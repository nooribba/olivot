package com.noori.olivot.service;

import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;
import com.noori.olivot.*;

import javax.servlet.ServletContext;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Chatbot implementation
 */
public final class MsBotImpl implements ChatBot {
    private QuestionClassifier classifier;
    private static Logger logger = Logger.getLogger(MsBotImpl.class.getName());

    /**
     * Initializes the components used by the chatbot
     *
     * @param context Servlet context in which the bot is used
     */
    public void init(ServletContext context) {
    	logger.info("##### bot init");
        try {
            TextVectorizer vectorizer = QuestionVectorizerFactory.restore(getResourceFile(context, "/WEB-INF/vectorizer.bin"));
            logger.info("##### TextVectorize bin");
            Map<Integer, String> answerMapping = AnswersMappingFactory.create(getResourceFile(context, "/WEB-INF/answers.csv"));
            logger.info("##### Answermapping csv");
            //logger.info("##### answerMapping 6 : "+answerMapping.get(6));

            classifier = QuestionClassifierFactory.restore(
                    getResourceFile(context, "/WEB-INF/classifier.bin"),
                    vectorizer, answerMapping);
            logger.info("##### Classifier bin");
        } catch (Exception e) {
            // Do nothing
        	System.out.println("##### exception:"+e.toString());
        }
    }

    /**
     * Handles incoming chatbot activities
     *
     * @param context Context for the current conversation
     */
    @Override
    public void handle(ConversationContext context) {
        if (context.activity().type().equals(ActivityTypes.MESSAGE)) {
            String replyText;
            logger.info("##### bot handle");
            if (classifier != null) {
                replyText = classifier.predict(context.activity().text());
            } else {
                replyText = "Sorry, I have no clue what to do here. My brain ain't what it used to be.";
            }
            logger.info("##### reply text : "+replyText);
            Activity reply = ActivityFactory.createReply(context.activity(), replyText);
            context.sendActivity(reply);
        }
    }

    private File getResourceFile(ServletContext context, String path) throws Exception {
        return new File(context.getResource(path).toURI());
    }

	@Override
	public String olivotHandle(ConversationContext context) {
		// TODO Auto-generated method stub
		return null;
	}
}
