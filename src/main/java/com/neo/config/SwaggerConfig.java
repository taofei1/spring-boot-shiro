package com.neo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger2�Ľӿ�����
 *
 * @author ruoyi
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig

{
    @Value("${project.name}")
    private String name;
    @Value("${project.version}")
    private String version;
    /**
     * ����API
     */
    @Bean
    public Docket createRestApi()
    {
        return new Docket(DocumentationType.SWAGGER_2)
                // ��ϸ����
                .apiInfo(apiInfo())
                .select()
                // ָ����ǰ��·��
                .apis(RequestHandlerSelectors.basePackage("com.neo.testSwagger"))
                // ɨ������ .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * ���ժҪ��Ϣ
     */
    private ApiInfo apiInfo()
    {
        // ��ApiInfoBuilder���ж���
        return new ApiInfoBuilder()
                .title("���⣺������Ϣϵͳ�ӿ��ĵ�")
                .description("���������ڹ��������¹�˾����Ա��Ϣ,�������XXX,XXXģ��...")
                .contact(new Contact(name, null, null))
                .version("�汾��:" + version)
                .build();
    }

}

