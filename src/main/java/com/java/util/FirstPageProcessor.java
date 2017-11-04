package com.java.util;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

public class FirstPageProcessor implements PageProcessor{

	private Site site = Site.me().setTimeOut(1000).setSleepTime(100).setCharset("utf-8");
	
	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	@Override
	public void process(Page page) {
		// TODO Auto-generated method stub
System.out.println("Requesting: " + page.getUrl());
        
        List<String> list = page.getHtml().xpath("//article[@class='well clearfix']").all();
        
        for(String header : list){
            Html tmp = Html.create(header);
//            System.out.println(tmp);
            
            System.out.println(tmp.xpath("//h1[@class='entry-title']/a/text()").toString().trim());
            System.out.println(tmp.xpath("//span[@class='fa fa-user']/a/text()").toString());
            System.out.println(tmp.xpath("//h1[@class='entry-title']/a/@href").toString());
            System.out.println(tmp.xpath("//div[@class='pull-left footer-tag']/a/text()").all().toString());
            System.out.println("------------------------------");
        }
 
        if (list.size() <=0) {
            // 忽略这个页面
            page.setSkip(true);
        }
        //从页面发现后续的url地址来抓取
//        page.addTargetRequests(page.getHtml().links().regex("(https://www.zifangsky.cn/page/\\d*)").all());
        
	}
	public static void main(String[] args) {
		 Spider.create(new FirstPageProcessor())
         .addUrl("https://www.zifangsky.cn")
         .thread(5)
         .run();
	}

}
