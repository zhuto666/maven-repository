package core.vo;

import lombok.Data;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/9/19 15:40 星期四
 */
@Data
public class FileNameVO {

    private String bucketName;

    private String fileName;

    private String lastModified;

}
