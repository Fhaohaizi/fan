package com.fan

import com.fantest.httpclient.FanLibrary
import com.fantest.source.WriteRead
import com.fantest.utils.Save
import net.coobird.thumbnailator.Thumbnails

import javax.imageio.ImageIO
import java.awt.*
import java.awt.image.BufferedImage
import java.util.List

class PIC extends FanLibrary {
    static void main(String[] args) throws IOException {

//        File everyfile = new File("/Users/Vicky/Desktop/0/_DEATHNOTE01-_0036.png");
//        getHalf(everyfile.getAbsolutePath());

//        List<String> allFile = getAllFile("/Users/Vicky/Desktop/Vol_01");
//        List<String> allFile = getAllFile("/Users/Vicky/Desktop/0");
//        allFile.forEach(file -> {
//            output(file);
//            getSmall(file);
//        });
//        getSmall("/Users/Vicky/Desktop/0/0000.png");
        List<String> allFile = getAllFile("/Users/Vicky/Desktop/Vol_01/");
        Save.saveStringList(allFile, "pic");
        List<String> list = WriteRead.readTxtFileByLine(LONG_Path + "pic.log");
        list.each { line -> getHalf(line) };
        testOver();
    }

    public static void getHalf(String path) {
        File everyfile = new File(path);
        try {
//            BufferedImage为Image的子类 ImageIO的read方法返回为BufferedImage
            BufferedImage image = ImageIO.read(everyfile);
            //创建图像的缩放版本，前两个参数分别为图像宽度和高度，最后是图像取样算法
            //Image.SCALE_SMOOTH是图像平滑度比缩放速度更具优先级的算法
            //SCALE_FAST 缩放速度比图像平滑度更具优先级的算法
            Image newimage = image.getScaledInstance(image.getWidth(), image.getHeight(), Image.SCALE_SMOOTH);
//            //构造图像，最后一参数为图像颜色分量为RGB
            //此为16级灰度
            BufferedImage left = new BufferedImage(image.getWidth() / 2, image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            BufferedImage right = new BufferedImage(image.getWidth() / 2, image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//            //方法返回Graphics2D，为Graphics的子类，其实用createGraphics更方便，为向后兼容考虑
//            //用于绘制图像
            Graphics gl = left.createGraphics();
            Graphics gr = right.createGraphics();
//            //第一个参数为需要绘制的图像，第二和第三个参数为x和y坐标，第三个参数为图像非透明部分绘制的
//            //背景色，最后一个为转换图像需要通知的对象（这个不是很了解）
            gl.drawImage(newimage, 0, 0, Color.white, null);
            gr.drawImage(newimage, -image.getWidth() / 2, 0, Color.white, null);
//            //释放资源
            gl.dispose();
            gr.dispose();
//            //将原文件删除
//            everyfile.delete();
            ImageIO.write(left, "png", new File("/Users/Vicky/Desktop/0/" + everyfile.getName().replaceAll("_DEATHNOTE\\d+-_", EMPTY).replace(".png", "0.png")));
            ImageIO.write(right, "png", new File("/Users/Vicky/Desktop/0/" + everyfile.getName().replaceAll("_DEATHNOTE\\d+-_", EMPTY).replace(".png", "1.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void getSmall(String patb) {
        try {
            Thumbnails.of(patb).scale(0.75f).outputQuality(0.5f).toFile(patb);
        } catch (IOException e) {
            output(e);
        }
    }
}
