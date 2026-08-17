package com.wxmblog.yanjian.common.utils;

import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;
import javax.imageio.ImageIO;

public class ImageCompressor {

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * 压缩网络图片至5M以下并返回base64编码
     *
     * @param imageUrl 图片URL
     * @return base64编码字符串
     */
    public static String compressImageToBase64(String imageUrl) throws IOException {
        // 下载图片
        BufferedImage image = ImageIO.read(new URL(imageUrl));

        // 检查原始图片大小
        long originalSize = getImageSizeFromUrl(imageUrl);

        if (originalSize <= MAX_SIZE) {
            // 如果小于5M，直接转换为base64
            return imageToBase64(image, "jpg");
        }

        // 计算压缩比例
        double compressionRatio = Math.sqrt((double) MAX_SIZE / originalSize);
        int newWidth = (int) (image.getWidth() * compressionRatio);
        int newHeight = (int) (image.getHeight() * compressionRatio);

        // 压缩图片
        BufferedImage compressedImage = compressImage(image, newWidth, newHeight);
        // 转换为base64
        return imageToBase64(compressedImage, "jpg");
    }

    /**
     * 压缩base64图片至5M以下并返回新的base64编码
     *
     * @param base64Image 原始base64图片编码
     * @return 压缩后的base64图片编码
     */
    public static String compressBase64Image(String base64Image) throws Exception {

        if (StringUtils.isBlank(base64Image)) {
            return "";
        }
        String[] fieBase64 = base64Image.split(",");
        //取出bae64 后面的数据

        // 解码base64图片
        byte[] imageBytes = Base64.getDecoder().decode(fieBase64.length==1?fieBase64[0]:fieBase64[1]);

        // 检查原始图片大小
        if (imageBytes.length <= MAX_SIZE) {
            return base64Image; // 如果小于5M，直接返回原图
        }

        // 将字节数组转换为BufferedImage
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(bis);

        // 计算压缩比例
        double compressionRatio = Math.sqrt((double) MAX_SIZE / imageBytes.length);
        int newWidth = (int) (originalImage.getWidth() * compressionRatio);
        int newHeight = (int) (originalImage.getHeight() * compressionRatio);

        // 压缩图片
        BufferedImage compressedImage = compressImage(originalImage, newWidth, newHeight);

        // 转换为base64
        return imageToBase64(compressedImage, "jpg");
    }

    /**
     * 获取网络图片大小
     */
    private static long getImageSizeFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        return url.openConnection().getContentLengthLong();
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
    private static String imageToBase64(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
