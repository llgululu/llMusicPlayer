package com.gululu.llmusicplayer.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;

public class Generator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/llmusicplayer?serverTimezone=Asia/Shanghai", "root", "lh20020123")
                .globalConfig(builder -> {
                    builder.author("llgululu") // 设置作者
                            .outputDir("E:\\springBootProjects\\llMusicPlayer\\src\\main\\java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.gululu.llmusicplayer"); // 设置父包名
                })
                .strategyConfig(builder -> {
                    builder.addInclude("user"); // 设置需要生成的表名
                })
                .execute();
    }
}
