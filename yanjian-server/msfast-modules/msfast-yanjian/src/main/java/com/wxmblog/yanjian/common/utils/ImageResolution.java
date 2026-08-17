package com.wxmblog.yanjian.common.utils;

import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ImageResolution {

    private static final int MAX_RESOLUTION = 4000; // 最大分辨率限制

    /**
     * 压缩base64图片至分辨率4000以下
     * @param base64Image 原始base64图片编码
     * @return 压缩后的base64图片编码
     */
    public static String compressBase64ImageByResolution(String base64Image) throws Exception {
        if (StringUtils.isBlank(base64Image)) {
            return "";
        }
        String[] fieBase64 = base64Image.split(",");
        //取出bae64 后面的数据

        // 解码base64图片
        byte[] imageBytes = Base64.getDecoder().decode(fieBase64.length==1?fieBase64[0]:fieBase64[1]);

        // 将字节数组转换为BufferedImage
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(bis);

        // 检查原始图片分辨率
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 如果分辨率已经在限制以内，直接返回
        if (Math.max(originalWidth, originalHeight) <= MAX_RESOLUTION) {
            return base64Image;
        }

        // 计算新的尺寸，保持宽高比
        int newWidth, newHeight;
        if (originalWidth > originalHeight) {
            // 宽度较大，以宽度为准进行缩放
            newWidth = MAX_RESOLUTION;
            newHeight = (int) (originalHeight * ((double) MAX_RESOLUTION / originalWidth));
        } else {
            // 高度较大，以高度为准进行缩放
            newHeight = MAX_RESOLUTION;
            newWidth = (int) (originalWidth * ((double) MAX_RESOLUTION / originalHeight));
        }

        // 压缩图片
        BufferedImage compressedImage = compressImage(originalImage, newWidth, newHeight);

        // 转换为base64
        return imageToBase64(compressedImage, "jpg");
    }

    /**
     * 压缩网络图片至分辨率4000以下
     * @param imageUrl 图片URL
     * @return 压缩后的base64图片编码
     */
    public static String compressImageToBase64ByResolution(String imageUrl) throws Exception {
        // 下载图片
        BufferedImage originalImage = ImageIO.read(new java.net.URL(imageUrl));

        // 检查原始图片分辨率
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 如果分辨率已经在限制以内，直接转换为base64
        if (Math.max(originalWidth, originalHeight) <= MAX_RESOLUTION) {
            return imageToBase64(originalImage, "jpg");
        }

        // 计算新的尺寸，保持宽高比
        int newWidth, newHeight;
        if (originalWidth > originalHeight) {
            newWidth = MAX_RESOLUTION;
            newHeight = (int) (originalHeight * ((double) MAX_RESOLUTION / originalWidth));
        } else {
            newHeight = MAX_RESOLUTION;
            newWidth = (int) (originalWidth * ((double) MAX_RESOLUTION / originalHeight));
        }

        // 压缩图片
        BufferedImage compressedImage = compressImage(originalImage, newWidth, newHeight);

        // 转换为base64
        return imageToBase64(compressedImage, "jpg");
    }

    /**
     * 压缩图片
     */
    private static BufferedImage compressImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    /**
     * 图片转base64
     */
    private static String imageToBase64(BufferedImage image, String format) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
