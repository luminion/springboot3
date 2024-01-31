package com.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author booty
 */
public class YamlMessageConverter extends AbstractHttpMessageConverter<Object> {

    private ObjectMapper objectMapper = null; //把对象转成yaml

    public YamlMessageConverter(){

        // 指定媒体类型并指定字符集(可不指定), 若不指定媒体类型, springboot不知道那种类型参数使用此转换器
        super(new MediaType("application", "yaml", StandardCharsets.UTF_8));

        // 创建yml工厂, 并指定不生成切割文档的分隔符号(---)
        YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        this.objectMapper = new ObjectMapper(factory);
    }


    /**
     * 指定支持的类型, 此处为Object,支持所有类型
     *
     * @param clazz 克拉兹
     * @return boolean
     * @author booty
     */
    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    /**
     * 对应@RequestBody注解的入参
     * 指定格式入参数据怎么转指定对象
     *
     * @param clazz        入参对象类型
     * @param inputMessage 输入消息
     * @return {@code Object }
     * @author booty
     */
    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        System.out.println("自定义YamlMessageConverter读取");
        try(InputStream is = inputMessage.getBody()) {
            return objectMapper.readValue(is,clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对应@ResponseBody的出参(@RestController内数据也可)
     * 出参对象怎么转为指定格式数据
     *
     * @param o             出参对象
     * @param outputMessage 输出消息
     * @author booty
     */
    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        System.out.println("自定义YamlMessageConverter输出");
        try(OutputStream os = outputMessage.getBody()){
            this.objectMapper.writeValue(os,o);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
