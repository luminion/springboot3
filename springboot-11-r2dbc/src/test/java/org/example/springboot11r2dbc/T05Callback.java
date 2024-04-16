package org.example.springboot11r2dbc;

import org.junit.jupiter.api.Test;
import org.springframework.data.r2dbc.mapping.event.BeforeSaveCallback;

/**
 * Spring Data 基础设施提供了用于在调用某些方法之前和之后修改实体的钩子。这些所谓的EntityCallback实例提供了一种方便的方法来检查并可能以回调方式修改实体。
 * AnEntityCallback看起来很像一个专门的ApplicationListener.一些 Spring Data 模块发布BeforeSaveEvent允许修改给定实体的存储特定事件（例如）。
 * 在某些情况下，例如在使用不可变类型时，这些事件可能会导致问题。
 * 此外，事件发布依赖于ApplicationEventMulticaster.如果使用异步配置TaskExecutor它可能会导致不可预测的结果，因为事件处理可以分叉到线程上。
 * 实体回调提供了同步和反应式 API 的集成点，以保证在处理链中明确定义的检查点处按顺序执行，返回可能修改的实体或反应式包装器类型。
 * 实体回调通常按 API 类型分隔。这种分离意味着同步 API 仅考虑同步实体回调，而响应式实现仅考虑响应式实体回调
 *
 * Spring Data Commons 2.2 中引入了实体回调 API。这是应用实体修改的推荐方法。现有的存储特定信息ApplicationEvents仍会在调用可能注册的实例之前EntityCallback发布
 *
 * @author booty
 */
public class T05Callback {

    @Test
    void callback(){

    }


}
