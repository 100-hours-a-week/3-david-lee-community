package bootcamp.kakao.community.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.TreeMap;

import static bootcamp.kakao.community.common.util.KeyUtil.BEARER;
import static bootcamp.kakao.community.common.util.KeyUtil.JWT;

@Configuration
public class LocalSwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(JWT);
        Components components = new Components().addSecuritySchemes(JWT, new SecurityScheme()
                .name(JWT)
                .type(SecurityScheme.Type.HTTP)
                .scheme(BEARER)
                .bearerFormat(JWT)
        );
        return new OpenAPI()
                .components(components)
                .info(apiInfo())
                .addSecurityItem(securityRequirement);
    }

    private Info apiInfo() {
        return new Info()
                .title("KTB Community Swagger")
                .description("카카오테크 부트캠프 커뮤니티 스웨거입니다.")
                .version("1.0.0");
    }

    /// 필요없는 스키마 제거
    @Bean
    public OpenApiCustomizer removeGenericSchemas() {
        return openApi -> {
            openApi.getComponents().getSchemas().keySet().removeIf(name ->
                    name.contains("ApiResponse") || name.contains("SliceResponse") || name.contains("FieldErrorResponse")
            );
        };
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
