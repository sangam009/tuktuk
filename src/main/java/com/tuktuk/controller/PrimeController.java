package com.tuktuk.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tuktuk.serviceinteface.FirstScreenSuggestion;

@RestController
public class PrimeController {

	@Autowired
	FirstScreenSuggestion suggest;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public String getTestValue(HttpServletRequest req, HttpServletResponse res) {
 		return req.getRequestURI();

	}

	@RequestMapping(value = "/getSuggestion", method = RequestMethod.POST)
	@ResponseBody
	public String getData(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return suggest.getSuggestion(req, res).toString();
	}

}
