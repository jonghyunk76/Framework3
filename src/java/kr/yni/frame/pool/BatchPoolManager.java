package kr.yni.frame.pool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import kr.yni.frame.Constants;
import kr.yni.frame.util.StringHelper;

/**
 * <p>
 * 스케쥴 배치의 생성 및 삭제를 관리하는 클래스
 * </p>
 * 
 * @author YNI-maker
 *
 */
public class BatchPoolManager {
	
	private static Log log = LogFactory.getLog(BatchPoolManager.class);
	
	// 스케쥴 목록을 가지고 있는 테이블
	private static Hashtable<String, Object> batchJobClass = new Hashtable<String, Object>();
	
	/**
	 * 배치 생성 매소드
	 * 
	 * @param map
	 * @throws SchedulerException
	 */
	public static Scheduler startBatch(Map<String, String> map) throws SchedulerException {
		// JobDetail Info
		String jobGroup = map.get("COMPANY_CD");
		String jobName = map.get("SCHEDULE_CD");
		String program = StringHelper.null2string(map.get("EXECUTION_PROGRAM"), "BatchStarter");
		String jobClass = null;
		
		// Job을 통해 실행할 클래스 지정
		if(program.indexOf(".") > 0) {
			jobClass = program;
		} else {
			jobClass = Constants.PREFIX_CLASS_PACKAGE + "." + program;
		}
		
		// CronTrigger Info
		String triggerGroup = map.get("COMPANY_CD") + "_TRIGGER";
		String triggerName = map.get("SCHEDULE_CD") + "Trigger";
		
		// Scheduler aliase name
		String scheduleName = jobName + "_" + jobGroup;
		Object batchObj = batchJobClass.get(scheduleName);
		Scheduler scheduler = null;
		String cronExpress = makeCronExpression(map);
		
		if (batchObj != null) {
			scheduler = (Scheduler) batchObj;
			
			if(scheduler.isStarted()) {
				if (log.isDebugEnabled()) {
					log.debug("batch is already running.(schedule name=" + scheduleName + ", class=" + jobClass + ")");
				}
			} else if(scheduler.isShutdown()){
				scheduler.start(); // 스케쥴이 수행중인 상태가 아니라면 재시작한다.
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Create batch...(schedule name=" + scheduleName + ", class=" + jobClass + ")");
			}
			
			// Initiate a Schedule Factory
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			// Retrieve a scheduler from schedule factory
			scheduler = schedulerFactory.getScheduler();
		}
		
		if(!isJobRunning(map)) {
			// Initiate JobDetail with job name, job group, and executable job
			// class
			JobDetail jobDetail = new JobDetail(jobName, jobGroup, classForName(jobClass));
			// Initiate CronTrigger with its name and group name
			CronTrigger cronTrigger = new CronTrigger(triggerName, triggerGroup);
			
			try {
				// setup CronExpression
				CronExpression cexp = new CronExpression(cronExpress);
				// Assign the CronExpression to CronTrigger
				cronTrigger.setCronExpression(cexp);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// schedule a job with JobDetail and Trigger
			scheduler.scheduleJob(jobDetail, cronTrigger);

			// start the scheduler
			scheduler.start();

			if (scheduler.isStarted()) {
				batchJobClass.put(scheduleName, scheduler);
			}
		}

		return scheduler;
	}

	/**
	 * 스쥴코드에 해당하는 스케쥴 시간을 변경한다.
	 * 
	 * @param scheduler
	 * @param map
	 * @throws ParseException
	 */
	@SuppressWarnings("rawtypes")
	public static void rescheduleJob(Scheduler scheduler, Map map) throws SchedulerException, ParseException {
		// JobDetail Info
		String jobGroup = StringHelper.null2void(map.get("COMPANY_CD"));
		String jobName = StringHelper.null2void(map.get("SCHEDULE_CD"));
		// CronTrigger Info
		String triggerGroup = map.get("COMPANY_CD") + "_TRIGGER";
		String triggerName = map.get("SCHEDULE_CD") + "Trigger";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String cronExpress = makeCronExpression(map);
		
		if(isJobRunning(map)) {
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerName, triggerGroup);
			trigger.setJobName(jobName);
			trigger.setJobGroup(jobGroup);
	
			if (log.isDebugEnabled()) {
				log.info("Before NextFireTime : "
						+ sdf.format(trigger.getNextFireTime()));
			}
	
			// Trigger의 스케쥴을 변경.
			if(scheduler != null) {
				Date toDate = new Date(System.currentTimeMillis());
				int addMin = StringHelper.null2zero(map.get("ADD_MINUTE_NUM"));
				
				if(addMin != 0) {
					toDate.setMinutes(toDate.getMinutes()+addMin);
					
					if (log.isDebugEnabled()) {
						log.debug("Add minute("+addMin+") Time : " + sdf.format(new Date(System.currentTimeMillis())) + " > " + sdf.format(toDate));
					}
				}
				
				trigger.setStartTime(toDate); // 시작시간은 현재 +1분 시간이후로 해서 중복실행하지 않도록 만든다.
				trigger.setCronExpression(cronExpress); // 새롭게 변경되는 스케줄 적용
				
				scheduler.rescheduleJob(triggerName, triggerGroup, trigger);
//				CronTrigger afterTrigger = (CronTrigger) scheduler.getTrigger(triggerName, triggerGroup);
		
				if (log.isInfoEnabled()) {
					log.info("After NextFireTime : " + sdf.format(trigger.getNextFireTime()));
				}
			}
		} else {
			startBatch(map);
		}
	}
	
	/**
	 * 스케쥴 시간을 조회한다. 
	 * @param scheduler
	 * @param map
	 * @return
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	@SuppressWarnings("rawtypes")
	public static String getNextFireTime(Scheduler scheduler, Map map)
			throws SchedulerException, ParseException {
		String returnValue = "";
		
		if(!scheduler.isStarted()) {
			return "--/--/-- --:--:--";
		}
		
		// CronTrigger Info
		String triggerGroup = map.get("COMPANY_CD") + "_TRIGGER";
		String triggerName = map.get("SCHEDULE_CD") + "Trigger";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		CronTrigger beforeTrigger = (CronTrigger) scheduler.getTrigger(triggerName, triggerGroup);
		
		if(beforeTrigger != null) {
			returnValue = sdf.format(beforeTrigger.getNextFireTime());
		}
		
		return returnValue;
	}
	
	/**
	 * 기본 스케쥴 객체를 얻는다.
	 * @return
	 */
	public static Scheduler getDefaultScheduler() throws SchedulerException {
		// Initiate a Schedule Factory
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		// Retrieve a scheduler from schedule factory
		Scheduler scheduler = schedulerFactory.getScheduler();
		
		return scheduler;
	}
	
	/**
	 * 특정 스케쥴 객체를 얻는다.
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Scheduler getDefaultScheduler(Map map) {
		String jobGroup = StringHelper.null2void(map.get("COMPANY_CD"));
		String jobName = StringHelper.null2void(map.get("SCHEDULE_CD"));

		// scheduler aliase name
		String scheduleName = jobName + "_" + jobGroup;

		if (batchJobClass.get(scheduleName) != null) {
			return (Scheduler) batchJobClass.get(scheduleName);
		} else {
			return null;
		}
	}

	/**
	 * Job Name(=EXECUTION_PROGRAM)에 해당하는 배치를 일시 정지한다.
	 * 
	 * @param jobName 형식 : SCHEDULE_CD + "_" + COMPANY_CD
	 */
	@SuppressWarnings("rawtypes")
	public static void pauseScheduler(Scheduler scheduler, Map map)
			throws SchedulerException {
		if (scheduler != null) {
			// JobDetail Info
			String jobGroup = StringHelper.null2void(map.get("COMPANY_CD"));
			String jobName = StringHelper.null2void(map.get("SCHEDULE_CD"));
			if (scheduler != null && scheduler.isStarted()) {
				scheduler.pauseJob(jobName, jobGroup);
			}
			
			if (log.isInfoEnabled()) {
				log.info("pause request(scheduler status = " + scheduler.isStarted() + ")");
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Scheduler Object is null");
			}
		}
	}
	
	/**
	 * schedule을 시작한다.
	 */
	public static boolean startScheduler(Scheduler scheduler)
			throws SchedulerException {
		if (scheduler != null) {
			if(!scheduler.isInStandbyMode()) {
	            scheduler.standby();
	        }
			
			if(!scheduler.isStarted()) {
				// 스케쥴 테이블 초기화 시킨다.
				if(batchJobClass != null) {
					batchJobClass.clear();
				}
				
				scheduler.start();
			}

			return scheduler.isStarted();
		}

		return false;
	}
	
	/**
	 * schedule을 멈춘다.
	 * @throws SchedulerException
	 */
	public static boolean shutdownScheduler(Scheduler scheduler)
			throws SchedulerException {
		if (scheduler != null) {
			if(scheduler.isStarted()) {
				scheduler.shutdown();
			}
			
			if(scheduler.isShutdown()) {
				batchJobClass.clear();
			}
			
			return scheduler.isShutdown();
		}

		return false;
	}
	
	/**
	 * 특정 schedule를 삭제한다.
	 * 
	 * @throws SchedulerException
	 */
	@SuppressWarnings("rawtypes")
	public static boolean deleteJob(Scheduler scheduler, Map map)
			throws SchedulerException {
		boolean del = true;
		
		if (scheduler != null) {
			String jobGroup = StringHelper.null2void(map.get("COMPANY_CD"));
			String jobName = StringHelper.null2void(map.get("SCHEDULE_CD"));
			
			if(isJobRunning(map)) {
				del = scheduler.deleteJob(jobName, jobGroup);
			}

			String scheduleName = jobName + "_" + jobGroup;
			batchJobClass.remove(scheduleName);

			return del;
		} else {
			del = false;
		}

		return del;
	}
	
	/**
     * 전체 구동중인 스케쥴에서 Job(배치가) 구동중인지 체크한다.
     * 
     * @param map
     * @author carlos
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	public static boolean isJobRunning(Map map) throws SchedulerException {        
        String jobGroup = StringHelper.null2void(map.get("COMPANY_CD"));
		String jobCode = StringHelper.null2void(map.get("SCHEDULE_CD"));
		boolean isrun = false;
		
		Scheduler scheduler = BatchPoolManager.getDefaultScheduler();
		
        for (String groupName : scheduler.getJobGroupNames()) {
            for (String jobName : scheduler.getJobNames(groupName)) {
            	if(log.isDebugEnabled()) log.debug("job/param = " + jobName + " / " + jobCode + ", group/param = " + groupName + " / " + jobGroup);
            	
                if(jobGroup.equals(groupName) && jobCode.equals(jobName)) {
                	if(log.isInfoEnabled()) log.info("Job is exists....");
                	
                	return true;
                }
            }
        }
        
        return isrun;
    }
    
	/**
	 * 월.일.시.분의 value을 이용하여 cron표현식으로 변환
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String makeCronExpression(Map map) {
		// cron주기 생성
		String yearExp = StringHelper.null2void(map.get("YEAR")); // 년 주기
		if (yearExp == null || yearExp.isEmpty()) {
			yearExp = "";
		}
		String weekExp = StringHelper.null2void(map.get("WEEK")); // 요일 주기
		if (weekExp == null || weekExp.isEmpty() || "*".equals(weekExp)) {
			weekExp = "?";
		}
		String monthExp = StringHelper.null2void(map.get("MONTH")); // 달 주기
		if (monthExp == null || monthExp.isEmpty()) {
			monthExp = "*";
		}
		String dayExp = StringHelper.null2void(map.get("DAY")); // 일 주기
		if (dayExp == null || dayExp.isEmpty()) {
			dayExp = "*";
		}
		String hourExp = StringHelper.null2void(map.get("HOUR")); // 시간 주기
		if (hourExp == null || hourExp.isEmpty()) {
			hourExp = "0";
		}
		String minuteExp = StringHelper.null2void(map.get("MINUTES")); // 분 주기
		if (minuteExp == null || minuteExp.isEmpty()) {
			minuteExp = "0";
		}
		String secondExp = StringHelper.null2void(map.get("SECOND")); // 초 주기
		if (secondExp == null || secondExp.isEmpty()) {
			secondExp = "0";
		}
		// 배치주기 표현식(초 분 시 일 월 년 요일)
		String cronExpress = secondExp + " " + minuteExp + " " + hourExp + " " + dayExp
				+ " " + monthExp + " " + weekExp + " " + yearExp;
		if (log.isDebugEnabled()) {
			log.debug("Cron Expression = " + cronExpress);
		}

		return cronExpress;
	}

	public static Class<?> classForName(String className) {
		Class<?> ldclass = null;
		ClassLoader cloader = Thread.currentThread().getContextClassLoader();
		try {
			ldclass = cloader.loadClass(className);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return ldclass;
	}
}
