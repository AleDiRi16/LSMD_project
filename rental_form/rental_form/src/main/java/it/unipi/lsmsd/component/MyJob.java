package it.unipi.lsmsd.component;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;


@Component
public class MyJob extends QuartzJobBean {

    @Autowired
    UserComponentLogic UserService;
    @Override
    protected void executeInternal(JobExecutionContext jec) {
        JobDataMap dataMap = jec.getJobDetail().getJobDataMap();
        String id = dataMap.getString("id");
        String name=dataMap.getString("name");
        boolean v=dataMap.getBoolean("v");
        UserService.schedule_reservation(name,id,v);

    }



}