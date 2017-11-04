// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 2017/2/13 16:54:47
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Ding.java

package com.atlassian.jira.event.ding;

import java.util.*;

import com.atlassian.crowd.cql.parser.antlr.CqlParser.string_return;
import com.atlassian.jira.event.memcached.EmailUtil;
import com.atlassian.jira.event.util.FileUtil;
import com.danga.MemCached.MemCachedClient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

// Referenced classes of package com.atlassian.jira.event.ding:
//            JsonPostConnectionUtils

public class Ding
{
private static final Properties proInfo = FileUtil.getProperties("dingding.properties");
	
	public Ding() {
	}

	public static String getAccessToken() throws Exception {
		String resString = "";
		String corpid = proInfo.getProperty("corpid");
		String corpsecret = proInfo.getProperty("corpsecret");
		String url = "https://oapi.dingtalk.com/gettoken?corpid="+corpid+"&corpsecret="+corpsecret;
		try {
			resString = JsonPostConnectionUtils.getHttpResult(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
			return resString;
		}
		JSONObject formObject = JSONObject.fromObject(resString);
		if (formObject != null) {
			if ("ok".equals(formObject.getString("errmsg"))) {
				resString = formObject.getString("access_token");
			} else {
				throw new Exception("corpid或corpsecret不合法");
			}
		}
		return resString;
	}

    public static List getDepIdList() throws Exception
    {
        List list = new ArrayList();
        String accessTocken = getAccessToken();
        if ("".equals(accessTocken)) {
			return list;
		}
        String url = (new StringBuilder("https://oapi.dingtalk.com/department/list?access_token=")).append(accessTocken).toString();
        String resString = "";
        try
        {
            resString = JsonPostConnectionUtils.getHttpResult(url, "GET");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return list;
        }
        JSONObject jsonObject = JSONObject.fromObject(resString);
        JSONArray jsonArray = jsonObject.getJSONArray("department");
        for(int i = 0; i < jsonArray.size(); i++)
        {
            String dep = jsonArray.getString(i);
            JSONObject formObject = JSONObject.fromObject(dep);
            String id = formObject.getString("id");
            list.add(id);
        }

        return list;
    }

    public static List getEmailList() throws Exception
    {
        List list = new ArrayList();
        List idList = getDepIdList();
        String accessTocken = getAccessToken();
        if ("".equals(accessTocken)) {
			return list;
		}
        for(Iterator iterator = idList.iterator(); iterator.hasNext();)
        {
            String id = (String)iterator.next();
            String url = (new StringBuilder("https://oapi.dingtalk.com/user/list?access_token=")).append(getAccessToken()).append("&department_id=").append(id).toString();
            try
            {
                String resString = JsonPostConnectionUtils.getHttpResult(url, "GET");
                JSONObject formObject = JSONObject.fromObject(resString);
                String userlist = formObject.getString("userlist");
                JSONArray jsonArray = JSONArray.fromObject(userlist);
                for(int i = 0; i < jsonArray.size(); i++)
                {
                    String user = jsonArray.getString(i);
                    JSONObject fromObject = JSONObject.fromObject(user);
                    if (fromObject.containsKey("email")) {
                    	String userid = fromObject.getString("userid");
                    	String email = fromObject.getString("email");
                    	String name = fromObject.getString("name");
                    	Map map = new HashMap();
                    	map.put("id", userid);
                    	map.put("email", email);
                    	if (email.equals("dongchaohui@jetair.com.cn")) {
							map.put("email", fromObject.getString("orgEmail"));
						}
                    	map.put("name", name);
                    	list.add(map);
					}
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return list;
            }
        }
        return list;
    }

    private static String  url = "https://oapi.dingtalk.com/gettoken?corpid=ding669cb3e0175e09ac&corpsecret=ryooDm1gwJWhrJgxNIvXcJ-saRwoRymCu_1WZBWRvTOU4Lzx3m_wwriXYio2LBHG";;
}
