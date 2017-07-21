package com.oversea.task.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.oversea.task.domain.QuartzJobBean;
import com.oversea.task.domain.SchedulingJob;
import com.oversea.task.manager.SchedulingJobManager;
import com.oversea.task.util.StringUtil;

/**
 * 任务管理控制类
 *
 * @author linlvping
 */
@Controller
@RequestMapping("/job")
public class QuartzJobController extends ApplicationObjectSupport{

    private Log log = LogFactory.getLog(QuartzJobController.class);
    //参数id
    private static final String ID = "id";
    //错误信息----参数错误
    private static final String PARAM_ERROR = "参数错误";
    //错误信息的key值
    private static final String MESSAGE = "message";

    @Resource
    private SchedulingJobManager schedulingJobManager;

    /**
     * 保存任务
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("save")
    public ModelAndView saveJob(HttpServletRequest request, HttpServletResponse response)throws Exception {
        log.error("begin to execute saveJob");
        ModelMap model = new ModelMap();
        SchedulingJob sj = new SchedulingJob();
        if (handleRequest(request, sj)) {
            try {
                if (StringUtil.isEmpty(sj.getId())) {
                    //添加任务
                    schedulingJobManager.add(sj);
                } else {
                    //修改任务
                    schedulingJobManager.update(sj);
                }
                List<SchedulingJob> list = schedulingJobManager.getJobList();
                model.put("list", list);
            } catch (Exception e) {
                log.error("saveJob error" + e.getMessage(), e);
                model.put(MESSAGE, e.getMessage());
                return new ModelAndView("error", model);
            }
        } else {
            model.put(MESSAGE, PARAM_ERROR);
            return new ModelAndView("error", model);
        }
        log.error("end to execute saveJob");
        return new ModelAndView("joblist", model);
    }

    /**
     * 跳转到更新任务页面
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("update")
    public ModelAndView updateJob(HttpServletRequest request, HttpServletResponse response) {
        log.error("begin to execute updateJob");
        ModelMap model = new ModelMap();
        try {
            String id = request.getParameter(ID);
            if (StringUtil.isEmpty(id)) {
                model.put(MESSAGE, PARAM_ERROR);
                return new ModelAndView("error", model);
            }
            SchedulingJob sj = schedulingJobManager.getJob(Integer.parseInt(id));
            model.put("sj", sj);
        } catch (Exception e) {
            log.error("updateJob error" + e.getMessage(), e);
            model.put(MESSAGE, e.getMessage());
            return new ModelAndView("error", model);
        }
        log.error("end to execute updateJob");
        return new ModelAndView("jobdetail", model);
    }

    /**
     * 跳转到任务列表页面
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("list")
    public ModelAndView listJob(HttpServletRequest request, HttpServletResponse response) {
        ModelMap model = new ModelMap();
        try {
            List<SchedulingJob> list = schedulingJobManager.getJobList();
            model.put("list", list);
        } catch (Exception e) {
            log.error("list error", e);
            model.put(MESSAGE, e.getMessage());
            return new ModelAndView("error", model);
        }
        return new ModelAndView("joblist", model);
    }

    /**
     * 删除任务
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("delete")
    public ModelAndView deleteJob(HttpServletRequest request, HttpServletResponse response) {
        log.error("begin to execute deleteJob");
        ModelMap model = new ModelMap();
        try {
            String id = request.getParameter(ID);
            if (StringUtil.isEmpty(id)) {
                model.put(MESSAGE, PARAM_ERROR);
                return new ModelAndView("error", model);
            }
            schedulingJobManager.delete(Integer.parseInt(id));
            List<SchedulingJob> list = schedulingJobManager.getJobList();
            model.put("list", list);
        } catch (Exception e) {
            log.error("delete error", e);
            model.put(MESSAGE, e.getMessage());
            return new ModelAndView("error", model);
        }
        log.error("end to execute deleteJob");
        return new ModelAndView("joblist", model);
    }

    /**
     * 启动任务
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("start")
    public ModelAndView startJob(HttpServletRequest request, HttpServletResponse response) {
        log.error("begin to execute startJob");
        ModelMap model = new ModelMap();
        try {
            String id = request.getParameter(ID);
            if (StringUtil.isEmpty(id)) {
                model.put(MESSAGE, PARAM_ERROR);
                return new ModelAndView("error", model);
            }
            schedulingJobManager.start(Integer.parseInt(id));
            List<SchedulingJob> list = schedulingJobManager.getJobList();
            model.put("list", list);
        } catch (Exception e) {
            log.error("start error", e);
            model.put(MESSAGE, e.getMessage());
            return new ModelAndView("error", model);
        }
        log.error("end to execute startJob");
        return new ModelAndView("joblist", model);
    }

    /**
     * 停止任务
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("stop")
    public void stopJob(HttpServletRequest request, HttpServletResponse response) {
        log.error("begin to execute stopJob");
        ModelMap model = new ModelMap();
        try {
            String id = request.getParameter(ID);
            if (StringUtil.isEmpty(id)) {
                model.put(MESSAGE, PARAM_ERROR);
                return;
            }
            schedulingJobManager.stop(Integer.parseInt(id));
            List<SchedulingJob> list = schedulingJobManager.getJobList();
            model.put("list", list);
        } catch (Exception e) {
            log.error("stop error", e);
            model.put(MESSAGE, e.getMessage());
            return;
        }
        log.error("end to execute stopJob");
    }

    /**
     * 立即执行任务
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("startNow")
    public void startNow(HttpServletRequest request, HttpServletResponse response) {
        log.error("begin to execute startNow");
        ModelMap model = new ModelMap();
        try {
            String id = request.getParameter(ID);
            if (StringUtil.isEmpty(id)) {
                model.put(MESSAGE, PARAM_ERROR);
                return;
            }
            schedulingJobManager.startNow(Integer.parseInt(id));
            List<SchedulingJob> list = schedulingJobManager.getJobList();
            model.put("list", list);
        } catch (Exception e) {
            log.error("stop error", e);
            model.put(MESSAGE, e.getMessage());
            return;
        }
        log.error("end to execute stopJob");
    }

    /**
     * 启动所有任务
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("startAll")
    public ModelAndView startAllJob(HttpServletRequest request, HttpServletResponse response) {
        log.error("begin to execute startAllJob");
        ModelMap model = new ModelMap();
        try {
            schedulingJobManager.startAll();
            List<SchedulingJob> list = schedulingJobManager.getJobList();
            model.put("list", list);
        } catch (Exception e) {
            log.error("start all", e);
            model.put(MESSAGE, e.getMessage());
            return new ModelAndView("error", model);
        }
        log.error("end to execute startAllJob");
        return new ModelAndView("joblist", model);
    }

    /**
     * 停止所有任务
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("stopAll")
    public ModelAndView stopAllJob(HttpServletRequest request, HttpServletResponse response) {
        log.error("begin to execute stopAllJob");
        ModelMap model = new ModelMap();
        try {
            schedulingJobManager.stopAll();
            List<SchedulingJob> list = schedulingJobManager.getJobList();
            model.put("list", list);
        } catch (Exception e) {
            log.error("stopAll error", e);
            model.put(MESSAGE, e.getMessage());
            return new ModelAndView("error", model);
        }
        log.error("end to execute stopAllJob");
        return new ModelAndView("joblist", model);
    }

    /**
     * 初始化方法，在容器启动的时候调用，开始所有状态为“0”的任务
     */
    public void init() {
        try {
            log.error("start inti QuartzJobController");
            ApplicationContext ac = getApplicationContext();
            QuartzJobBean.setAc(ac);
            List<SchedulingJob> list = schedulingJobManager.getJobList();
            if (list != null) {
                log.error("inti QuartzJobController size=" + list.size());
                for (SchedulingJob sj : list) {
                    log.error(sj.toString());
                    if (SchedulingJobManager.JOB_STATUS_ON.equalsIgnoreCase(sj.getJobStatus())) {
                        schedulingJobManager.start(sj);
                    }
                }
            }
            log.error("end inti QuartzJobController");
        } catch (Exception e) {
            log.error("error inti QuartzJobController", e);
        }
    }

    /**
     * 处理request请求，取出里面的参数set到SchedulingJob中
     *
     * @param request
     * @param sj
     * @return
     * @throws Exception 
     */
    private boolean handleRequest(HttpServletRequest request, SchedulingJob sj) throws Exception {
        String id = request.getParameter(ID);
        String jobClass = request.getParameter("jobClass");
        String jobMethod = request.getParameter("jobMethod");
        String jobGroup = request.getParameter("jobGroup");
        String jobName = request.getParameter("jobName");
        String jobStatus = request.getParameter("jobStatus");
        String description = request.getParameter("description");
        String cronExpression = request.getParameter("cronExpression");
        String methodArgs = request.getParameter("methodArgs");

        if (StringUtil.isEmpty(cronExpression) || StringUtil.isEmpty(jobClass) || StringUtil.isEmpty(jobName)
                || StringUtil.isEmpty(jobGroup) || StringUtil.isEmpty(jobMethod)) {
            return false;
        } else {
            if (StringUtil.isEmpty(jobStatus)) {
                jobStatus = "0";
            }
            sj.setId(id);
            sj.setCronExpression(cronExpression);
            sj.setDescription(description);
            sj.setJobClass(jobClass);
            sj.setJobGroup(jobGroup);
            sj.setJobMethod(jobMethod);
            sj.setJobName(jobName);
            sj.setJobStatus(jobStatus);
            sj.setMethodArgs(methodArgs);
        }

        return true;
    }

}
