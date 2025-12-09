package bootcamp.kakao.community.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.TreeMap;

import static bootcamp.kakao.community.common.util.KeyUtil.BEARER;
import static bootcamp.kakao.community.common.util.KeyUtil.JWT;

@Profile("prod")
@Configuration
public class SwaggerConfig {

    @Value("${cors.back.host}")
    private String backHost;

    @Bean
    public OpenAPI openAPI() {
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(JWT);
        Components components = new Components().addSecuritySchemes(JWT, new SecurityScheme()
                .name(JWT)
                .type(SecurityScheme.Type.HTTP)
                .scheme(BEARER)
                .bearerFormat(JWT)
        );

        /// 개발 환경 추가
        io.swagger.v3.oas.models.servers.Server server = new io.swagger.v3.oas.models.servers.Server()
                .url(backHost)
                .description("개발자 커뮤니티 서버");

        return new OpenAPI()
                .components(components)
                .info(apiInfo())
                .addServersItem(server)
                .addSecurityItem(securityRequirement);
    }

    private Info apiInfo() {
        return new Info()
                .title("KTB Developer Community Swagger")
                .description("개발자 커뮤니티 스웨거입니다.")
                .version("1.0.1");
    }

    /// 스키마 이름 기준 오름차순
    @Bean
    public OpenApiCustomizer sortSchemasAlphabetically() {
        return openApi -> {
            Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            openApi.getComponents().setSchemas(new TreeMap<>(schemas));
        };
    }
}
