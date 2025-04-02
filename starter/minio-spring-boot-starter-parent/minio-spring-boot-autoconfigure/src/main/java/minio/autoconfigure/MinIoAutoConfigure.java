package minio.autoconfigure;

import core.properties.MinIoProperties;
import core.util.MinIoUtil;
import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author Kevin
 * @version 1.0
 * @date 2023/11/7 15:20 星期二
 */
@EnableConfigurationProperties(MinIoProperties.class)
public class MinIoAutoConfigure {

    @Bean
    public MinioClient minioClient(MinIoProperties minIoProperties) {
        return MinioClient.builder()
                .endpoint(minIoProperties.getEndpoint())
                .credentials(minIoProperties.getAccessKey(), minIoProperties.getSecretKey())
                .build();
    }

    @Bean
    MinIoUtil minIoUtil(MinioClient minioClient) {
        return new MinIoUtil(minioClient);
    }

}
