package com.springboot;

import com.tuktuk.serviceimpl.FirstScreenSuggestionImpl;
import com.tuktuk.serviceimpl.SupportServiceImpl;
import com.tuktuk.serviceinteface.FirstScreenSuggestion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration
public class TukTukApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Bean
	public FirstScreenSuggestion firstScreenSuggestion(){
		return new FirstScreenSuggestionImpl(new SupportServiceImpl());
	}
}
