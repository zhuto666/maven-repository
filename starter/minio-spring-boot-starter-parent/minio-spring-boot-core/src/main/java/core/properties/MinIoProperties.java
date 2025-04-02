package core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Kevin
 * @version 1.0
 * @date 2023/11/7 15:24 星期二
 */
@Data
@ConfigurationProperties(MinIoProperties.PREFIX)
public class MinIoProperties {


    public static final String PREFIX = "min.io";

    private String endpoint;

    private String accessKey;

    private String secretKey;

}
