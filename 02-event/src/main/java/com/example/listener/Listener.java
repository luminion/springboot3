package com.example.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;


/**
 * springboot中的监听器
 * spring中, 有以下几种listener:
 *
 * ● SpringApplicationRunListener：       感知全阶段生命周期 + 各种阶段都能自定义操作； 功能更完善。
 *   ○ META-INF/spring.factories
 *
 * ● BootstrapRegistryInitializer：    感知特定阶段：感知引导初始化
 *   ○ META-INF/spring.factories
 *   ○ 创建引导上下文bootstrapContext的时候触发。
 *   ○ application.addBootstrapRegistryInitializer();
 *   ○ 场景：进行密钥校对授权(例如程序使用期限一年,从服务器请求验证,验证不通过,退出程序)。
 *
 * ● ApplicationListener：    感知全阶段：基于事件机制，感知事件。 一旦到了哪个阶段可以做别的事
 *   ○ @Bean或@EventListener： 事件驱动
 *   ○ SpringApplication.addListeners(…)或 SpringApplicationBuilder.listeners(…)
 *   ○ META-INF/spring.factories
 *
 * ● ApplicationContextInitializer：   感知特定阶段： 感知ioc容器初始化
 *   ○ META-INF/spring.factories
 *   ○ application.addInitializers();
 *
 * ● ApplicationRunner:          感知特定阶段：感知应用就绪Ready。卡死应用，就不会就绪
 *   ○ @Bean
 *
 * ● CommandLineRunner：   感知特定阶段：感知应用就绪Ready。卡死应用，就不会就绪
 *   ○ @Bean
 *
 *
 * 部分监听器使用@Configration或@Component注解的listener不会生效,
 * 因为在项目启动时. 还未创建ioc容器, 所以无法扫描到
 *
 * spring中主要有以下事件:
 *      starting
 *      environmentPrepared
 *      contextPrepared
 *      contextLoaded
 *      started
 *      ready
 *      failed
 *
 * 1、引导： 利用 BootstrapContext 引导整个项目启动
 *      starting：              应用开始，SpringApplication的run方法一调用，只要有了 BootstrapContext 就执行
 *      environmentPrepared：   环境准备好（把启动参数等绑定到环境变量中），但是ioc还没有创建；【调一次】
 * 2、启动：
 *      contextPrepared：       ioc容器创建并准备好，但是sources（主配置类）没加载。并关闭引导上下文；组件都没创建  【调一次】
 *      contextLoaded：         ioc容器加载。主配置类加载进去了。但是ioc容器还没刷新（我们的bean没创建）。
 *      =======截止以前，ioc容器里面还没造bean呢=======
 *      started：               ioc容器刷新了（所有bean造好了），但是 runner 没调用。
 *      ready:                  ioc容器刷新了（所有bean造好了），所有runner调用完了。
 * 3、运行
 *     以前步骤都正确执行，代表容器running。
 *
 *
 * @author booty
 */
@Configuration
@Slf4j
public class Listener {

    @EventListener
    public void onApplicationEvent(ApplicationEvent event) {
        log.debug("=====触发监听器@EventListener的注解方法监听器===event: " + event.getClass().getSimpleName());
    }

    /*
    监听器的使用时机
    ● 如果项目启动前做事： BootstrapRegistryInitializer 和 ApplicationContextInitializer
    ● 如果想要在项目启动完成后做事：ApplicationRunner和 CommandLineRunner
    ● 如果要干涉生命周期做事：SpringApplicationRunListener
    ● 如果想要用事件机制：ApplicationListener
     */

    /*
    9大事件触发顺序&时机
    1. ApplicationStartingEvent：应用启动但未做任何事情, 除过注册listeners and initializers.
    2. ApplicationEnvironmentPreparedEvent：  Environment 准备好，但context 未创建.
    3. ApplicationContextInitializedEvent: ApplicationContext 准备好，ApplicationContextInitializers 调用，但是任何bean未加载
    4. ApplicationPreparedEvent： 容器刷新之前，bean定义信息加载
    5. ApplicationStartedEvent： 容器刷新完成， runner未调用
    =========以下就开始插入了探针机制, 探针机制一般用于对接K8S等云平台============
    6. AvailabilityChangeEvent： LivenessState.CORRECT应用存活； 存活探针
    7. ApplicationReadyEvent: 任何runner被调用
    8. AvailabilityChangeEvent：ReadinessState.ACCEPTING_TRAFFIC就绪探针，可以接请求
    9. ApplicationFailedEvent ：启动出错
     */

}
