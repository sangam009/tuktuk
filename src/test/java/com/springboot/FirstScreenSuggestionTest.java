package com.springboot;

import com.tuktuk.model.SuggestionRequest;
import com.tuktuk.serviceimpl.FirstScreenSuggestionImpl;
import com.tuktuk.serviceinteface.FirstScreenSuggestion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

/**
 * Created by garvitasharma on 26/12/17.
 */

public class FirstScreenSuggestionTest extends TukTukApplicationTests{
    private FirstScreenSuggestion firstScreenSuggestion;

    @PostConstruct
    public void setUp(){
        this.firstScreenSuggestion = firstScreenSuggestion();
    }

    @Test
    public void getSuggestionTest(){
        SuggestionRequest request = new SuggestionRequest();
        request.setLatitude(28.7041);
        request.setLongitude(77.1025);
        try {
            //firstScreenSuggestion.getSuggestion(req, res);
        	System.out.println("sangam");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}