// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 2017/2/13 16:53:27
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   DoDing.java

package com.atlassian.jira.event.ding;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import com.atlassian.jira.event.email.TakeEmail;
import com.atlassian.jira.event.memcached.EmailUtil;
import com.danga.MemCached.MemCachedClient;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

// Referenced classes of package com.atlassian.jira.event.ding:
//            Ding, JsonPostConnectionUtils

public class DoDing
{
//	MemCachedClient memCachedClient = EmailUtil.getMemCachedClient();

    public DoDing()
    {
    }

    public String doDing(String email, String content) throws Exception
    {
        String resString = "";
        List list = TakeEmail.getEamils();
        if(list != null)
        {
        	int index = 0;
            for(Iterator iterator = list.iterator(); iterator.hasNext();)
            {
            	
                Map map = (Map)iterator.next();
                if(email.equals(map.get("email"))){
                	index = index + 1;
                    resString = doSend((String)map.get("id"), content);
                	break;
                }
            }
            if (index == 0) {
				throw new Exception("该用户邮箱与钉钉邮箱不符。请询问钉钉管理员。");
			}
        }
        return resString;
    }

    public String doSend(String id, String content) throws Exception
    {
        String resString = "";
        String urlString = (new StringBuilder("https://oapi.dingtalk.com/message/send?access_token=")).append((new Ding()).getAccessToken()).toString();
        JSONObject json = new JSONObject();
        json.put("touser", id);
        //jetair
        json.put("agentid", "77660890");
        //mytame
//        json.put("agentid", "75834415");
        
        json.put("msgtype", "text");
        JSONObject json2 = new JSONObject();
        json2.put("content", content);
        json.put("text", JSONUtils.valueToString(json2));
        String reqString = JSONUtils.valueToString(json);
        try
        {
            resString = JsonPostConnectionUtils.doJsonHttpPost(reqString, urlString, "POST", "application/json");
           
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return resString;
        }
        return resString;
    }
//    public List getEmails() throws Exception{
//    	List list =  (List) memCachedClient.get("emails");
//    	if (list == null) {
//    		System.out.println("调用接口");
//    		list = new Ding().getEmailList();
//    	}
//    	return list;
//    }
}
