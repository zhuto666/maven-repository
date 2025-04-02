package core.util;

import com.google.common.collect.Lists;
import com.zhongqin.commons.util.LocalDateTimeUtil;
import core.vo.FileNameVO;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Kevin
 * @version 1.0
 * @date 2023/11/7 15:23 星期二
 */
public class MinIoUtil implements InitializingBean {

    private final MinioClient minIoClient;

    public static MinioClient staticMinIoClient;

    /**
     * 默认过期时间(分钟)
     */
    private final static Integer DEFAULT_EXPIRY = 60;

    @Override
    public void afterPropertiesSet() {
        staticMinIoClient = this.minIoClient;
    }

    public MinIoUtil(MinioClient minIoClient) {
        this.minIoClient = minIoClient;
    }

    /**
     * 判断桶是否存在
     * 此处存在bug 暂时使用报错返回 true
     * bug原因 报错返回 查询桶是否存在无权限 但是权限是开放的
     *
     * @param bucketName
     * @return
     */
    public static boolean bucketExists(String bucketName) {
        try {
            return MinIoUtil.staticMinIoClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 创建桶
     *
     * @param bucketName
     */
    public static void makeBucket(String bucketName) {
        try {
            boolean isExist = bucketExists(bucketName);
            if (!isExist) {
                MinIoUtil.staticMinIoClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件存储服务的所有存储桶名称
     *
     * @return
     */
    public static List<String> listBucketNames() {
        List<Bucket> bucketList = listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        return bucketListName;
    }

    /**
     * 获取所有存储桶对象
     *
     * @return list
     */
    private static List<Bucket> listBuckets() {
        try {
            return MinIoUtil.staticMinIoClient.listBuckets();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<FileNameVO> listFileName(String bucketName) throws RuntimeException {
        List<FileNameVO> fileNameVoList = Lists.newArrayList();
        try {
            for (Result<Item> itemResult : MinIoUtil.staticMinIoClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build())) {
                Item item = itemResult.get();
                FileNameVO fileNameVO = new FileNameVO();
                fileNameVO.setBucketName(bucketName);
                fileNameVO.setFileName(item.objectName());
                // 转LocalDateTime
                LocalDateTime localDateTime = item.lastModified().toLocalDateTime().plusHours(8);
                fileNameVO.setLastModified(LocalDateTimeUtil.getStringByLocalDateTime(localDateTime));
                fileNameVoList.add(fileNameVO);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fileNameVoList;
    }

    /**
     * 删除一个文件
     *
     * @param bucketName 桶
     * @param objectName 文件名
     */
    public static void removeObject(String bucketName, String objectName) {
        if (bucketExists(bucketName)) {
            try {
                MinIoUtil.staticMinIoClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 删除一个文件
     *
     * @param bucketName 桶
     * @param fileName   文件名
     */
    public static void delete(String bucketName, String fileName) {
        try {
            MinIoUtil.staticMinIoClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param bucketName
     * @param directory
     * @param fileName
     * @return
     * @throws Exception
     */
    public static boolean upload(MultipartFile multipartFile, String bucketName, String directory, String fileName) {
        try {
            if (!bucketExists(bucketName)) {
                makeBucket(bucketName);
            }
            InputStream inputStream = multipartFile.getInputStream();
            directory = Optional.ofNullable(directory).orElse("");
            String minFileName = directory + fileName;
            // 上传文件到指定目录
            try {
                MinIoUtil.staticMinIoClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(minFileName).contentType(multipartFile.getContentType()).stream(inputStream, inputStream.available(), -1).build());
                Objects.requireNonNull(inputStream).close();
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件上传
     *
     * @param inputStream
     * @param bucketName
     * @param directory
     * @param fileName
     * @return
     * @throws Exception
     */
    public static boolean upload(InputStream inputStream, String bucketName, String directory, String fileName) {
        if (!bucketExists(bucketName)) {
            makeBucket(bucketName);
        }
        try {
            directory = Optional.ofNullable(directory).orElse("");
            String minFileName = directory + fileName;
            // 上传文件到指定目录
            try {
                MinIoUtil.staticMinIoClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(minFileName).stream(inputStream, inputStream.available(), -1).build());
                Objects.requireNonNull(inputStream).close();
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static InputStream inputStream(BufferedImage bufferedImage, String format) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, format, outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * 文件下载
     *
     * @param response
     * @param bucketName
     * @param fileName
     */
    public static void download(HttpServletResponse response, String bucketName, String fileName) {
        try {
            InputStream fileInputStream = MinIoUtil.staticMinIoClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            response.setContentType("application/force-download");
            response.setCharacterEncoding("UTF-8");
            IOUtils.copy(fileInputStream, response.getOutputStream());
            Objects.requireNonNull(fileInputStream).close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件流字节
     *
     * @param bucketName
     * @param fileName
     * @return
     */
    public static byte[] getFileInputStreamToByte(String bucketName, String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = MinIoUtil.staticMinIoClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while ((n = inputStream.read(buf)) >= 0) {
                baos.write(buf, 0, n);
            }
            Objects.requireNonNull(inputStream).close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 创建上传文件对象的外链
     *
     * @param bucketName 存储桶名称
     * @param objectName 欲上传文件对象的名称
     * @return uploadUrl
     */
    public static String createUploadUrl(String bucketName, String objectName) {
        return createUploadUrl(bucketName, objectName, DEFAULT_EXPIRY);
    }

    /**
     * 创建上传文件对象的外链
     *
     * @param bucketName
     * @param objectName
     * @param expiry     过期时间(分钟) 最大为7天 超过7天则默认最大值
     * @return
     */
    @SneakyThrows
    public static String createUploadUrl(String bucketName, String objectName, Integer expiry) {
        return MinIoUtil.staticMinIoClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.PUT).bucket(bucketName).object(objectName).expiry(expiryHandle(expiry)).build());
    }

    /**
     * 获取文件流
     *
     * @param bucketName
     * @param fileName
     * @return
     */
    public static String getFileInputStreamToString(String bucketName, String fileName) {
        InputStream inputStream = null;
        StringWriter stringWriter = new StringWriter();
        try {
            inputStream = MinIoUtil.staticMinIoClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
            IOUtils.copy(inputStream, stringWriter);
            Objects.requireNonNull(inputStream).close();
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将分钟数转换为秒数
     *
     * @param expiry
     * @return
     */
    private static int expiryHandle(Integer expiry) {
        expiry = expiry * 60;
        if (expiry > 604800) {
            return 604800;
        }
        return expiry;
    }
}
