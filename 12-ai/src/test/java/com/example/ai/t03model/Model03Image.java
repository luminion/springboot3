package com.example.ai.t03model;

import org.junit.jupiter.api.Test;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 图片生成模型
 *
 * @author bootystar
 */
@SpringBootTest
public class Model03Image {
    @Autowired
    private OpenAiImageModel openaiImageModel;

    @Test
    void createImage() {
        String instructions = "A light cream colored mini golden doodle";
        var imageOptions = OpenAiImageOptions.builder()
                .quality("hd") // 将要生成的图像的质量。HD 会创建细节更精细且图像一致性更高的图像。此参数仅支持 dall-e-3
                .N(1) // 生成图像的数量。必须在 1 到 10 之间。对于 dall-e-3，仅支持 n=1
                //   生成图像的大小。
                //   对于 dall-e-2，必须是 256x256、512x512 或 1024x1024 中的一种。
                //   对于 dall-e-3 模型，必须是 1024x1024、1792x1024 或 1024x1792 中的一种
                .height(1024)
                .width(1024)
                .style("natural") //生成图像的风格。必须是 vivid 或 natural 中的一个。Vivid 会使模型倾向于生成超真实和戏剧性的图像。Natural 会使模型生成更自然、不那么超真实的图像。此参数仅支持 dall-e-3
                .build();
        ImagePrompt imagePrompt = new ImagePrompt(instructions, imageOptions);
        ImageResponse response = openaiImageModel.call(imagePrompt);
        var output = response.getResult().getOutput();
        System.out.println(output.getUrl());

    }   
}
