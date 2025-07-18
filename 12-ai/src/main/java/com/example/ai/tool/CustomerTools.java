package com.example.ai.tool;

import com.example.ai.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

/**
 * 自定义工具
 * <p>
 * 通过{@link Tool}注解指定工具, 通过{@link ToolParam}注解指定参数
 * <p>
 *
 * @author bootystar
 */
@Slf4j
public class CustomerTools {

    /**
     * 将顾客信息保存到数据库
     * <p>
     * 注意, 参数需要加上{@link ToolParam}注解说明,否则会报错
     *
     * @param customer customer
     */
    @Tool(description = "save customer and information")
    public void saveCustomerInfo(@ToolParam(description = "customer info") Customer customer) {
        log.info("\n---- saveCustomerInfo tool was called ----, customer info:\n{}", customer);
    }
    
    /**
     * 更新客户信息
     * 默认参数都是必须的
     * 使用{@link ToolParam}注解指定参数的描述, 并且可以指定参数是否是必需的
     *
     * @param id    id
     * @param name  姓名
     * @param email 电子邮件
     */
    @Tool(description = "Update customer information")
    void updateCustomerInfo(Long id, String name, @ToolParam(required = false) String email) {
        log.info("\n---- updateCustomerInfo tool was called ----, id:{}, name:{}, email:{}", id, name, email);
    }


    /**
     * 获取客户信息
     *
     * @param id          id
     * @return {@link Customer }
     */
    @Tool(description = "Retrieve customer information")
    Customer getCustomerInfo(Long id) {
        log.info("\n---- getCustomerInfo tool was called ----, id:{}", id);
        return new Customer(id, "lisi", "123@123.com");
    }
    
    /**
     * 获取特殊客户信息(该方法需要ToolContext)
     *
     * @param id          id
     * @param toolContext 工具上下文
     * @return {@link Customer }
     */
    @Tool(description = "Retrieve special customer information")
    public Customer getSpecialCustomerInfo(Long id, ToolContext toolContext) {
        log.info("\n---- getCustomerInfoWithContext tool was called ----, id:{}", id);
        var context = toolContext.getContext();
        var name = context.getOrDefault("name","").toString();
        var email = context.getOrDefault("email", "").toString();
        return new Customer(id, name, email);
    }

    /**
     * 刷新系统作用域的Customer信息
     * <p>
     * 通过returnDirect属性指定是否直接返回, 如果为true, 则直接返回, 不会传递回模型
     *
     * @return {@link Customer }
     */
    @Tool(description = "Refresh System Default CustomerInfos", returnDirect = true)
    public List<Customer> refreshSysCustomerInfo() {
        return List.of(new Customer(1L,"张三","123@123.com"));
    }
    
}
