package com.micro.app;

import com.micro.pojo.ReqScopedBean;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ThreadContext;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@Slf4j
public class AppScopedBean {

    @Resource
    private ManagedExecutorService mes;

    @Inject
    private ReqScopedBean reqScopedBean;

    public void submitToExecutor() {
        log.info("in submitToExecutor");
        ThreadContext threadContext = ThreadContext.builder().propagated(ThreadContext.CDI).cleared(ThreadContext.ALL_REMAINING).build();
        mes.submit(threadContext.contextualRunnable(() -> {
            log.info("reqScopedBean");
            log.info("reqScopedBean.getComment() " + reqScopedBean.getComment());
        }));
    }
}
