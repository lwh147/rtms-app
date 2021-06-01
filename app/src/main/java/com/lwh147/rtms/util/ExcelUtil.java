package com.lwh147.rtms.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import com.lwh147.rtms.data.model.TempInfo;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

/**
 * @description: excel工具类
 * @author: lwh
 * @create: 2021/5/8 21:42
 * @version: v1.0
 **/
public class ExcelUtil {

    private ExcelUtil() {

    }

    public static boolean generateExcel(String fileName, Context context, List<TempInfo> tempInfos) throws IOException, WriteException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && getAvailableStorage(context) > 1000000) {
            return false;
        }
        File file;
        File dir = new File(Objects.requireNonNull(context.getExternalFilesDir(null)).getPath());
        file = new File(dir, fileName + ".xls");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        WritableWorkbook wwb;
        OutputStream os = new FileOutputStream(file);
        wwb = Workbook.createWorkbook(os);
        WritableSheet sheet = wwb.createSheet("体温数据", 0);
        String[] title = {"检测时间", "姓名", "体温(摄氏度)"};
        Label label;
        for (int i = 0; i < title.length; i++) {
            // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
            // 在Label对象的子对象中指明单元格的位置和内容
            label = new Label(i, 0, title[i], getHeader());
            // 将定义好的单元格添加到工作表中
            sheet.addCell(label);
        }
        int size = tempInfos.size();
        for (int i = 0; i < size; i++) {
            TempInfo tempInfo = tempInfos.get(i);

            Label time = new Label(0, i + 1, DateTimeUtil.getFullDate(tempInfo.getTime()));
            Label name = new Label(1, i + 1, tempInfo.getResidentName());
            Label temp = new Label(2, i + 1, String.valueOf(tempInfo.getTemp()));

            if(tempInfo.getTemp() > 37.0f){
                WritableCellFormat writableCellFormat = getWarning();
                time.setCellFormat(writableCellFormat);
                name.setCellFormat(writableCellFormat);
                temp.setCellFormat(writableCellFormat);
            }

            sheet.addCell(time);
            sheet.addCell(name);
            sheet.addCell(temp);
        }
        wwb.write();
        wwb.close();
        return true;
    }

    /**
     * 获取SD可用容量
     */
    public static long getAvailableStorage(Context context) {
        String root = Objects.requireNonNull(context.getExternalFilesDir(null)).getPath();
        StatFs statFs = new StatFs(root);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        // Formatter.formatFileSize(context, availableSize);
        return blockSize * availableBlocks;
    }

    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10,
                WritableFont.BOLD);// 定义字体
        try {
            font.setColour(Colour.BLUE);// 蓝色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
            format.setBorder(Border.ALL, BorderLineStyle.THIN,
                    Colour.BLACK);// 黑色边框
            format.setBackground(Colour.YELLOW);// 黄色背景
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }

    public static WritableCellFormat getWarning() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10,
                WritableFont.NO_BOLD);// 定义字体
        try {
            font.setColour(Colour.RED);// 红色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setBackground(Colour.LIGHT_ORANGE);// 浅红色背景
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }
}