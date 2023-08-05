package io.github.cctyl;

import com.madgag.gif.fmsware.AnimatedGifEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Main {
    private static final Pattern numPattern = Pattern.compile("^\\d+$");


    public static void main(String[] args) {
        //0. 读取参数
        //文件路径
        String filePath = "";
        //播放间隔
        int sec = 1;
        for (int i = 0; i < args.length; i+=2) {
            switch (args[i]){
                case "-path":
                    filePath = args[i + 1];
                    break;
                case "-sec":
                    sec = Integer.parseInt(args[i + 1]);
            }
        }

        if (filePath.length()==0){
            filePath = "E:\\temp\\gif";
        }

        System.out.println("---开始对"+filePath+"路径进行读取---");

        //1.判断文件路径是否存在
        File dir = new File(filePath);
        if (!dir.exists()){
            System.out.println("文件路径不存在");
        }

        //2.获取文件路径下的png图片
        File[] pngList = dir.listFiles((curDir, name) -> {
            return new File(dir, name).isFile() &&
                    name.toLowerCase().endsWith(".png") &&
                    numPattern.matcher(name.replace(".png","")).matches()
                    ;
        });
        if (pngList==null || pngList.length==0){
            System.out.println("该路径下没有合适的图片");
            return;
        }
        Arrays.sort(pngList,(o1, o2) -> {
            try {
                int a = Integer.parseInt(o1.getName().replace(".png", ""));
                int b = Integer.parseInt(o2.getName().replace(".png", ""));
                return a-b;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new RuntimeException("图片格式异常:"+o1.getName() + " , "+o2.getName());
            }
        });


        //3.生成
        generateGif(pngList,filePath,sec);



    }

    /**
     * 生成gif
     * @param imgFileList 源文件数组
     * @param outputPath  输出路径
     * @param sec         播放间隔，单位：秒
     */
    public static void generateGif(File[] imgFileList,String outputPath,int sec){
        AnimatedGifEncoder e = new AnimatedGifEncoder();
        //生成的图片路径
        try {
            //创建输出路径
            File outPutDir = new File(outputPath);
            outPutDir.mkdirs();

            //创建输出文件
            File outputFile = new File(outPutDir, "output.gif");

            e.start(new BufferedOutputStream(new FileOutputStream(outputFile)));
            //图片之间间隔时间,单位毫秒
            e.setDelay(sec*1000);

            //重复次数 0表示无限重复 默认不重复
            e.setRepeat(0);

            //添加图片
            Arrays.stream(imgFileList)
                    .map(file -> {
                        try {
                            return ImageIO.read(file);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new RuntimeException("读取图片失败");
                        }
                    })
                    .forEach(e::addFrame);
            e.finish();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

    }
}
