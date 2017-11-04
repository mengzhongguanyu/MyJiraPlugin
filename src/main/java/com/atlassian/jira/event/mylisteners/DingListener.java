package com.atlassian.jira.event.mylisteners;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import oracle.net.aso.s;

import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.JiraEvent;
import com.atlassian.jira.event.ding.DoDing;
import com.atlassian.jira.event.email.TakeEmail;
import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.pool.MyThreadPool;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.event.type.EventTypeManager;
import com.atlassian.jira.event.user.UserEvent;
import com.atlassian.jira.event.util.FileUtil;
import com.atlassian.jira.workflow.condition.IsSetCondition;

public class DingListener implements
		DisposableBean {

	private  static Properties proInfo = FileUtil.getProperties("dingding.properties");
	private static final Logger log = LoggerFactory
			.getLogger(DingListener.class);
	protected EventPublisher eventPublisher;

	public DingListener(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		eventPublisher.register(this);
	}

	/**
	 * 创建问题向经办人发钉
	 */
	public void issueCreated(final IssueEvent event) {
		log.info("创建问题"+"\t"+event.getIssue().getKey());
		sendToAssignee(event);
	}

	/**
	 * 问题状态给为Done向问题的报告人发钉
	 * 在公司环境中这个不执行
	 */
	public void issueGenericEvent(IssueEvent event) {
		log.info("解决问题"+"\t"+event.getIssue().getKey());
		String status = event.getIssue().getStatus().getString("name");
		if (proInfo.getProperty("done").equals(status)||"Done".equals(status)) {
			sendToCreator(event);
		}else if (proInfo.getProperty("reopen").equals(status)) {
			sendToAssignee(event);
		}
	}

	// 问题被重新分配，向新的经办人发钉
	public void issueAssigned(IssueEvent event) {
		log.info("解决问题"+"\t"+event.getIssue().getKey());
		sendToAssignee(event);
	}

	/**
	 * 问题被修改，向新的经办人发钉
	 */
	public void issueUpdated(IssueEvent event) {
		log.info("修改问题"+"\t"+event.getIssue().getKey());
		try {
			List<GenericValue> changeItems = event.getChangeLog().getRelated(
					"ChildChangeItem");
			for (GenericValue genericValue : changeItems) {
				String string = "更新内容："+getField(genericValue.get("field")) + " 由 '"+getString(genericValue.get("oldstring")) +"' 更新为 '"+ getString(genericValue.get("newstring"))  + "'";
				if (genericValue.get("field").equals("assignee")) {
					sendToAssignee(event);
					threadUpdateSendReport(event, string);
				}else if (genericValue.get("field").equals("reporter")) {
					threadUpdateSendAssignee(event, string);
					threadUpdateSendReport(event, string);
				}else if (genericValue.get("field").equals("description")) {
					string = "更新内容: 问题描述被更新";
					threadUpdateSendAssignee(event, string);
				}else {
					threadUpdateSendAssignee(event, string);
				}
			}
		} catch (GenericEntityException e) {
		}
	}
	/**
	 * 获得修改的项目
	 * @param field
	 * @return
	 */
	private Object getField(Object field){
		if (field.equals("assignee")) {
			return "经办人";
		}else if (field.equals("labels")) {
			return "标签";
		}else if (field.equals("reporter")) {
			return "报告人";
		}else if (field.equals("issuetype")) {
			return "类型";
		}else if (field.equals("priority")) {
			return "优先级";
		}else if (field.equals("description")) {
			return "描述";
		}else {
			return field;
		}
	}
	/**
	 * 提取优先级
	 * @param string
	 * @return
	 */
	private Object getString(Object string){
		if (string.equals("Blocker")) {
			return "紧急";
		}else if (string.equals("Critical")) {
			return "重要";
		}else if (string.equals("Major")) {
			return "一般";
		}else if (string.equals("Minor")) {
			return "次要";
		}else if (string.equals("Trivial")) {
			return "无关紧要";
		}else {
			return string;
		}
		
	}

	/**
	 * 向经办人发钉
	 */
	private String sendAssignee(JiraEvent event) {
		String result = "";
		try {

			if (event instanceof IssueEvent) {
				IssueEvent issueEvent = (IssueEvent) event;

				String status = issueEvent.getIssue().getStatus()
						.getString("name");
				if (issueEvent.getIssue().getAssignee() != null) {
					String email = issueEvent.getIssue().getAssignee()
							.getEmailAddress();
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String content = "jira上有一个问题需要处理\n问题："+
							issueEvent.getIssue().getProjectObject().getName()+" / "
							+ issueEvent.getIssue().getString("summary")
							+ "\n问题类型:"+issueEvent.getIssue().getIssueTypeObject().getNameTranslation()
							+ " \n状态为:" + getStatus(status) + "  \n时间: "
							+ sdf.format(event.getTime())
							+ "\n优先级:"+issueEvent.getIssue().getPriorityObject().getNameTranslation()
							+ "\n报告人："+issueEvent.getIssue().getReporter().getDisplayName()
							+ (issueEvent.getIssue().getDescription()==null?"":"\n问题描述："+issueEvent.getIssue().getDescription())
							+ "\nURL: http://192.168.1.222:8080/browse/"
							+ issueEvent.getIssue().getKey();
					try {
						if (email == null || "".equals(email)) {
							return result;
						}
						result = new DoDing().doDing(email, content);
						log.info("result:"+result);
					} catch (Exception e) {
						log.error(e.getMessage());
						result = new DoDing().doDing(issueEvent.getIssue().getReporter()
								.getEmailAddress(), e.getMessage()+sdf.format(new Date()));
						log.info("result:"+result);
					}
					
				}
			} else if (event instanceof UserEvent) {
				UserEvent userEvent = (UserEvent) event;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 向报告人发钉
	 * 
	 * @param event
	 */
	private String sendCreator(JiraEvent event) {
		String result = "";
		try {
			if (event instanceof IssueEvent) {
				IssueEvent issueEvent = (IssueEvent) event;
				String assEmail = "";
				if (issueEvent.getIssue().getAssignee()!=null) {
					assEmail = issueEvent.getIssue().getAssignee().getEmailAddress();
				}
				String status = issueEvent.getIssue().getStatus()
						.getString("name");
				String email = issueEvent.getIssue().getReporter()
						.getEmailAddress();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String content = "你在jira提的问题已经被解决\n问题:"
						+ issueEvent.getIssue().getProjectObject().getName()+" / "
						+ issueEvent.getIssue().getString("summary")
						+ "\n现在状态为：" + getStatus(status) + "  \n时间: "
						+ sdf.format(event.getTime())
						+ (issueEvent.getIssue().getAssignee()==null?"":"\n经办人："+issueEvent.getIssue().getAssignee().getDisplayName())
						+ "\nURL: http://192.168.1.222:8080/browse/"
						+ issueEvent.getIssue().getKey();
				try {
					if (email == null || "".equals(email)) {
						return result;
					}
					result = new DoDing().doDing(email, content);
					log.info("result:"+result);
				} catch (Exception e) {
					result = new DoDing().doDing(issueEvent.getIssue().getAssignee()
							.getEmailAddress(), e.getMessage()+sdf.format(new Date()));
					log.info("result:"+result);
				}
				
			} else if (event instanceof UserEvent) {
				UserEvent userEvent = (UserEvent) event;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 修改问题内容向经办人发钉
	 * @param event
	 * @param string
	 * @return
	 */
	private String updateSendAssignee(JiraEvent event,String string){
		String result = "";
		try {
			if (event instanceof IssueEvent) {
				IssueEvent issueEvent = (IssueEvent) event;
				String status = issueEvent.getIssue().getStatus()
						.getString("name");
				String email = issueEvent.getIssue().getAssignee()
						.getEmailAddress();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String content = "jira上的问题被更新\n问题:"
						+ issueEvent.getIssue().getProjectObject().getName()+" / "
						+ issueEvent.getIssue().getString("summary")
						+ "\n现在状态为：" + getStatus(status) + "  \n时间: "
						+ sdf.format(event.getTime())
						+ "\n"+string
						+ "\nURL: http://192.168.1.222:8080/browse/"
						+ issueEvent.getIssue().getKey();
				try {
					if (email == null || "".equals(email)) {
						return result;
					}
					result = new DoDing().doDing(email, content);
					log.info("result:"+result);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				
			} else if (event instanceof UserEvent) {
				UserEvent userEvent = (UserEvent) event;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 修改 向报告人发钉
	 * @param event
	 * @param string
	 * @return
	 */
	private String updateSendReport(JiraEvent event,String string){
		String result = "";
		try {
			if (event instanceof IssueEvent) {
				IssueEvent issueEvent = (IssueEvent) event;
				String status = issueEvent.getIssue().getStatus()
						.getString("name");
				String email = issueEvent.getIssue().getReporter()
						.getEmailAddress();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String content = "jira上的问题被更新\n问题:"
						+ issueEvent.getIssue().getProjectObject().getName()+" / "
						+ issueEvent.getIssue().getString("summary")
						+ "\n现在状态为：" + getStatus(status) + "  \n时间: "
						+ sdf.format(event.getTime())
						+ "\n"+string
						+ "\nURL: http://192.168.1.222:8080/browse/"
						+ issueEvent.getIssue().getKey();
				try {
					if (email == null || "".equals(email)) {
						return result;
					}
					result = new DoDing().doDing(email, content);
					log.info("result:"+result);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				
			} else if (event instanceof UserEvent) {
				UserEvent userEvent = (UserEvent) event;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 同步 
	 * @param event
	 * @param string
	 */
	public void threadUpdateSendAssignee(final JiraEvent event ,final String string){
		//XXX:线程池
				final ExecutorService pool = MyThreadPool.getPool();
				final Future<String> future = pool.submit(new Callable<String>() {

					@Override
					public String call() throws Exception {
						return updateSendAssignee(event, string);
					}
				});
				//XXX: 计算线程超时
				pool.execute(new Runnable() {
					
					@Override
					public void run() {
						try {
							future.get(30, TimeUnit.SECONDS);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						} catch (TimeoutException e) {
							log.error("请求超时");
							future.cancel(true);
							e.printStackTrace();
						} 
					}
				});
	}
	/**
	 * 同步
	 * @param event
	 * @param string
	 */
	public void threadUpdateSendReport(final JiraEvent event ,final String string){
		//XXX:线程池
		final ExecutorService pool = MyThreadPool.getPool();
		final Future<String> future = pool.submit(new Callable<String>() {
			
			@Override
			public String call() throws Exception {
				return updateSendReport(event, string);
			}
		});
		//XXX: 计算线程超时
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					future.get(30, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					log.error("请求超时");
					future.cancel(true);
					e.printStackTrace();
				} 
			}
		});
	}
	/**
	 * 获取状态
	 * @param stutas
	 * @return
	 */
	private String getStatus(String stutas) {
		if (proInfo.getProperty("done").equals(stutas)||"Done".equals(stutas)) {
			return "已解决";
		} else if (proInfo.getProperty("todo").equals(stutas)||"To Do".equals(stutas)) {
			return "待办";
		} else if (proInfo.getProperty("inprogress").equals(stutas)) {
			return "进行中";
		} else if (proInfo.getProperty("reopen").equals(stutas)) {
			return "重新打开";
		} else {
			return "";
		}
	}
	/**
	 * 异步方式向经办人发钉
	 * @param event
	 */
	public void sendToAssignee(final JiraEvent event) {
		//XXX:线程池
		final ExecutorService pool = MyThreadPool.getPool();
		final Future<String> future = pool.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {
				return sendAssignee(event);
			}
		});
		//XXX: 计算线程超时
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					future.get(30, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					log.error("请求超时");
					future.cancel(true);
					e.printStackTrace();
				} 
			}
		});
	}
	/**
	 * 异步向报告人发钉
	 * @param event
	 */
	public void sendToCreator(final JiraEvent event) {
		//XXX:线程池
		final ExecutorService pool = MyThreadPool.getPool();
		final Future<String> future = pool.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {
				return sendCreator(event);
			}
		});
		//XXX:监控线程超时
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					future.get(30, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					log.error("请求超时");
					future.cancel(true);
					e.printStackTrace();
				} 
			}
		});
	}

	@EventListener
	public void workflowEvent(IssueEvent event) {
		EventTypeManager eventTypeManager = ComponentAccessor
				.getEventTypeManager();

		Long eventTypeId = event.getEventTypeId();
		EventType eventType = eventTypeManager.getEventType(eventTypeId);

		if (eventType == null) {
			log.error("Issue Event Type with ID '" + eventTypeId
					+ "' is not recognised.");
		} else if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
			issueCreated(event);
		} else if (eventTypeId.equals(EventType.ISSUE_UPDATED_ID)) {
			issueUpdated(event);
		} else if (eventTypeId.equals(EventType.ISSUE_ASSIGNED_ID)) {
			issueAssigned(event);
		}else if (eventTypeId.equals(EventType.ISSUE_RESOLVED_ID)) {
			issueResolved(event);
		} else if (eventTypeId.equals(EventType.ISSUE_COMMENTED_ID)) {
			issueCommented(event);
		} else if (eventTypeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
			issueCommentEdited(event);
		} else if (eventTypeId.equals(EventType.ISSUE_COMMENT_DELETED_ID)) {
			issueCommentDeleted(event);
		} else if (eventTypeId.equals(EventType.ISSUE_CLOSED_ID)) {
			issueClosed(event);
		} else if (eventTypeId.equals(EventType.ISSUE_REOPENED_ID)) {
			issueReopened(event);
		} else if (eventTypeId.equals(EventType.ISSUE_DELETED_ID)) {
			issueDeleted(event);
		} else if (eventTypeId.equals(EventType.ISSUE_MOVED_ID)) {
			issueMoved(event);
		} else if (eventTypeId.equals(EventType.ISSUE_WORKLOGGED_ID)) {
			issueWorkLogged(event);
		} else if (eventTypeId.equals(EventType.ISSUE_WORKSTARTED_ID)) {
			issueStarted(event);
		} else if (eventTypeId.equals(EventType.ISSUE_WORKSTOPPED_ID)) {
			issueStopped(event);
		} else if (eventTypeId.equals(EventType.ISSUE_WORKLOG_UPDATED_ID)) {
			issueWorklogUpdated(event);
		} else if (eventTypeId.equals(EventType.ISSUE_WORKLOG_DELETED_ID)) {
			issueWorklogDeleted(event);
		} else if (eventTypeId.equals(EventType.ISSUE_GENERICEVENT_ID)) {
			issueGenericEvent(event);
		} else {
			customEvent(event);
		}
	}
	private void issueCommented(IssueEvent event) {
		
	}

	private void issueCommentEdited(IssueEvent event) {
		
	}

	private void issueCommentDeleted(IssueEvent event) {
		
	}

	private void issueClosed(IssueEvent event) {
		
	}
	/**
	 * 问题被重新开启向经办人发钉
	 * @param event
	 */
	private void issueReopened(IssueEvent event) {
		log.info("问题被重新开启");
		sendToAssignee(event);
	}

	private void issueDeleted(IssueEvent event) {
		
	}

	private void issueMoved(IssueEvent event) {
		
	}

	private void issueWorkLogged(IssueEvent event) {
		
	}

	private void issueStarted(IssueEvent event) {
		
	}

	private void issueStopped(IssueEvent event) {
		
	}

	private void issueWorklogUpdated(IssueEvent event) {
		
	}

	private void issueWorklogDeleted(IssueEvent event) {
		
	}

	private void customEvent(IssueEvent event) {
		
	}
	/**
	 * 问题被解决向报告发钉
	 * @param event
	 */
	private void issueResolved(IssueEvent event) {
		log.info("问题被解决");
		sendToCreator(event);
	}
	
//	private String getName(String email){
//		List<Map> list = TakeEmail.getEamils();
//		for (Map map : list) {
//			if (email.equals(map.get("email"))) {
//				return (String) map.get("name");
//			}
//		}
//		return null;
//	}

	@Override
	public void destroy() throws Exception {
		eventPublisher.unregister(this);
	}

}
