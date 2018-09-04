package com.noori.olivot.service;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.bot.schema.models.Activity;
import com.microsoft.bot.schema.models.ActivityTypes;
import com.noori.olivot.AnswersMappingFactory;
import com.noori.olivot.QuestionClassifier;
import com.noori.olivot.QuestionClassifierFactory;
import com.noori.olivot.QuestionVectorizerFactory;
import com.noori.olivot.TextVectorizer;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;

/**
 * Chatbot implementation
 */
public final class OlivotImpl implements ChatBot {
    private QuestionClassifier classifier;
    private static Logger logger = Logger.getLogger(OlivotImpl.class.getName());

    /**
     * Initializes the components used by the chatbot
     *
     * @param context Servlet context in which the bot is used
     */
    public void init(ServletContext context) {
    	logger.info("##### Olivot init");
        try {
            TextVectorizer vectorizer = QuestionVectorizerFactory.restore(getResourceFile(context, "/WEB-INF/vectorizer.bin"));
            logger.info("##### Olivot TextVectorize bin");
            Map<Integer, String> answerMapping = AnswersMappingFactory.create(getResourceFile(context, "/WEB-INF/answers.csv"));
            logger.info("##### Olivot Answermapping csv");

            classifier = QuestionClassifierFactory.restore(
                    getResourceFile(context, "/WEB-INF/classifier.bin"),
                    vectorizer, answerMapping);
            logger.info("##### Olivot Classifier bin");
        } catch (Exception e) {
            // Do nothing
        	System.out.println("##### exception:"+e.toString());
        }
    }

    @Override
    public void handle(ConversationContext context) {
    	logger.info("##### Olivot def handle");
        if (context.activity().type().equals(ActivityTypes.MESSAGE)) {
            String replyText;
            if (classifier != null) {
                replyText = classifier.predict(context.activity().text());
            } else {
                replyText = "Sorry, I have no clue what to do here. My brain ain't what it used to be.";
            }
            logger.info("##### Olivot def reply text : "+replyText);
            Activity reply = ActivityFactory.createReply(context.activity(), replyText);
            context.sendActivity(reply);
        }
    }
    @Override
    public String olivotHandle(ConversationContext context) {
    	logger.info("##### Olivot handle");
    	String replyText = "No Message";
        if (context.activity().type().equals(ActivityTypes.MESSAGE)) {
            if (classifier != null) {
            	String reqText = context.activity().text();
            	CharSequence contentNormalize = TwitterKoreanProcessorJava.normalize(reqText);
            	String normalizedMessage; 
            	normalizedMessage = contentNormalize.toString();
            	logger.info("##### Olivot normalizedMessage : "+normalizedMessage);
                replyText = classifier.predict(normalizedMessage);
            } else {
                replyText = "Sorry, I have no clue what to do here. My brain ain't what it used to be.";
            }
            logger.info("##### Olivot reply text : "+replyText);
            /*Activity reply = ActivityFactory.createReply(context.activity(), replyText);
            context.sendActivity(reply);*/
        }
        return replyText;
    }
    public String predictHandle(String message) {
    	logger.log(Level.INFO,"##### Olivot predictHandle");
    	String replyText = null;
        if (classifier != null) {
            replyText = classifier.predict(message);
        } else {
            replyText = "Sorry, I have no clue what to do here. My brain ain't what it used to be.";
        }
        logger.log(Level.INFO,"##### Olivot reply text : "+replyText);
        return replyText;
    }
    
    public String predictHandle(String message, HttpServletRequest request, HttpServletResponse response) {
    	logger.log(Level.INFO,"##### Olivot handle");
    	String replyText = null;
        if (classifier != null) {
            replyText = classifier.predict(message);
        } else {
            replyText = "Sorry, I have no clue what to do here. My brain ain't what it used to be.";
        }
        logger.log(Level.INFO,"##### Olivot reply text : "+replyText);
        return replyText;
    }

    private File getResourceFile(ServletContext context, String path) throws Exception {
        return new File(context.getResource(path).toURI());
    }
}
