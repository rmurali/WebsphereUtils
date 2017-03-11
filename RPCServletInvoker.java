
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.*;

/**
 * A class that schedules a JOB that will
 * invoke an XML RPC web service
 */
public class RPCServletInvoker extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Scheduler scheduler = null;


	public RPCServletInvoker() {

	}

	public void init(ServletConfig cfg) throws ServletException {

		try {

			// Grab the Scheduler instance from the Factory
			scheduler = StdSchedulerFactory.getDefaultScheduler();

			// and start it off
			scheduler.start();

			JobDetail job = JobBuilder
					.newJob(InvokerJob.class)
					.withIdentity("PoolServletInvokerJob",
							"PoolServletInvokerGroup").build();

			// Trigger trigger =
			// TriggerBuilder.newTrigger().withIdentity("trigger1",
			// "group1").startNow().withSchedule(cronSchedule("0 0 18 * * * ")).build();
			// Trigger trigger =
			// TriggerBuilder.newTrigger().withIdentity("PoolServletInvokerTrigger",
			// "PoolServletInvokerGroup").startNow().withSchedule(cronSchedule("0 */2 * * * ?")).build();
			Trigger trigger = TriggerBuilder
					.newTrigger()
					.withIdentity("RPCServletInvokerTrigger",
							"RPCServletInvokerGroup").startNow()
					.withSchedule(cronSchedule("0 15 07 * * ?")).build();

			// Tell quartz to schedule the job using our trigger
			scheduler.scheduleJob(job, trigger);

		} catch (SchedulerException se) {
			se.printStackTrace();
		}

	}

	public void destroy() {
		try {
			if (scheduler != null)
				scheduler.shutdown();
		} catch (SchedulerException e) {

		}
	}

}

