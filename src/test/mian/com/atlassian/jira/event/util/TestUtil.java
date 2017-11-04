package com.atlassian.jira.event.util;

import java.util.Properties;

import org.junit.Test;

public class TestUtil {
	private static final Properties proInfo = FileUtil.getProperties("dingding.properties");
	@Test
	public void takeFile(){
		String corpid = proInfo.getProperty("corpid");
		System.out.println(corpid);
		System.out.println(proInfo.getProperty("done"));
		System.out.println(proInfo.getProperty("todo"));
		System.out.println(proInfo.getProperty("reopen"));
	}
}
