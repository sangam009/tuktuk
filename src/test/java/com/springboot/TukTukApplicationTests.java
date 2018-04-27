package com.springboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.tuktuk.serviceinteface.FirstScreenSuggestion;

@RunWith(SpringRunner.class)
@ContextConfiguration
public class TukTukApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Bean
	public FirstScreenSuggestion firstScreenSuggestion(){
		return null;//new FirstScreenSuggestionImpl(new SupportServiceImpl());
	}
}
