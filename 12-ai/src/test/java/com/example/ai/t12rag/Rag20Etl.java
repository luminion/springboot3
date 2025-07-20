package com.example.ai.t12rag;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 提取、转换和加载（ETL）框架是检索增强生成（RAG）用例中数据处理的核心
 * RAG 用例通过从数据体中检索相关信息来增强生成模型的能力，从而提高生成输出的质量和相关性。
 * <p>
 * ETL 流程创建、转换并存储 Document 实例, Document 类包含文本、元数据，并可选包含图像、音频和视频等媒体类型
 * <p>
 * ETL 流程主要有三个主要组件:
 * <p>
 * DocumentReader 提供来自不同来源的文档源
 * <p>
 * DocumentTransformer 作为处理工作流程的一部分，转换一批文档
 * <p>
 * DocumentWriter 管理 ETL 过程的最终阶段，为文档存储做准备
 * <p>
 *
 * @author bootystar
 */
@SpringBootTest
public class Rag20Etl {
    
    
    @Test
    void test1(){
    }
    
    
}
