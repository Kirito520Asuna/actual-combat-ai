package com.actual_combat.aop.utils.pdf;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.actual_combat.aop.all.pdf.PDFFieldMap;
import com.actual_combat.aop.enums.PDFType;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author yan
 * @Date 2023/11/29 0029 17:33
 * @Description
 */
@Slf4j
public class PDFUtils {

    /**
     * 加文字居中
     *
     * @param form
     * @param stamper
     * @param fieldNameForPDF
     * @param text
     * @param horizontalAlignment
     * @param verticalAlignment
     * @param baseFont
     */

    public static void addTextToPDFCenter(AcroFields form, PdfStamper stamper, String fieldNameForPDF, String text, int horizontalAlignment, int verticalAlignment, BaseFont baseFont) {
        // 通过模板表单单元格名获取所在页和坐标，左下角为起点
        log.debug("fieldNameForPDF:{},fieldValue:{}", fieldNameForPDF, text);
        List<AcroFields.FieldPosition> fieldPositions = form.getFieldPositions(fieldNameForPDF);
        if (CollectionUtils.isEmpty(fieldPositions)) {
            return;
        }
        int pageNo = fieldPositions.get(0).page;
        Rectangle signRect = form.getFieldPositions(fieldNameForPDF).get(0).position;
        // 获取操作的页面
        PdfContentByte contentByte = stamper.getOverContent(pageNo);
        //创建表单
        PdfPTable table = new PdfPTable(1);
        //获取当前模板表单宽度
        float totalWidth = signRect.getRight() - signRect.getLeft();
        //设置新表单宽度
        table.setTotalWidth(totalWidth);
        //设置中文格式
        Font font = new Font(baseFont);
        //设置单元格格式
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        //设置单元格高度
        float totalHeight = signRect.getTop() - signRect.getBottom();
        cell.setFixedHeight(totalHeight);
        cell.setBorderWidth(0);
        //设置垂直居中
        cell.setVerticalAlignment(verticalAlignment);
        //设置水平居中
        cell.setHorizontalAlignment(horizontalAlignment);
        //添加到表单中
        table.addCell(cell);
        //写入pdf
        table.writeSelectedRows(0, -1, signRect.getLeft(), signRect.getTop(), contentByte);
    }

    /**
     * 加图片居中
     *
     * @param form
     * @param stamper
     * @param fieldNameForPDF
     * @param filePath
     * @param isCenter
     * @param request
     * @throws DocumentException
     * @throws IOException
     */
    public static void addImageToPDF(AcroFields form, PdfStamper stamper, String fieldNameForPDF, String filePath, boolean isCenter, HttpServletRequest request) throws DocumentException, IOException {
        if (!StringUtils.hasLength(filePath)) {
            return;
        }
        List<AcroFields.FieldPosition> fieldPositions = form.getFieldPositions(fieldNameForPDF);
        if (CollectionUtils.isEmpty(fieldPositions)) {
            return;
        }
        if (StringUtils.startsWithIgnoreCase(filePath, "//")) {
            //String scheme = request.getScheme();
            filePath = "http" + ":" + filePath;
        }
        // 通过域名获取所在页和坐标，左下角为起点
        int pageNo = fieldPositions.get(0).page;
        Rectangle signRect = form.getFieldPositions(fieldNameForPDF).get(0).position;
//        float x = signRect.getLeft() + signRect.getRight();
//        float y = signRect.getTop();

        // 读图片
        Image image = Image.getInstance(filePath);
        // 获取操作的页面
        PdfContentByte under = stamper.getOverContent(pageNo);
//        // 根据域的大小缩放图片
//        image.scaleToFit(signRect.getWidth() / 4, signRect.getHeight() / 4);
//        // 添加图片并设置位置（个人通过此设置使得图片垂直水平居中，可参考，具体情况已实际为准）
//        image.setAbsolutePosition(x / 2 - image.getWidth() / 2, y / 2 + image.getHeight());

//        float x = signRect.getLeft();
        float y = signRect.getBottom();
        float x = signRect.getLeft();
//        if (isCenter) {
//            x = (signRect.getRight() + signRect.getLeft()) / 2;
//        }
        image.setAbsolutePosition(x, y + 1);
        image.scaleToFit(signRect.getWidth(), signRect.getHeight());
        under.addImage(image);
    }


    /**
     * @param url
     * @param pdfFieldMaps
     * @param byteArrayOutputStream
     * @param request
     * @return
     */
    public static String generalPDF(String url, List<PDFFieldMap> pdfFieldMaps, ByteArrayOutputStream byteArrayOutputStream, HttpServletRequest request) {
        if (!StringUtils.hasLength(url)) {
            return null;
        }
        log.debug("pdf url:{}", url);
        try {
            PdfReader reader = null;

            try {
                URL url1 = new URL(url);
                reader = new PdfReader(url1);
            }catch (Exception e){
                reader = new PdfReader(url);
            }

            try {
                PdfStamper pdfStamper = new PdfStamper(reader, byteArrayOutputStream);
                //防止中文字体部分丢失
                BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                ArrayList<BaseFont> fontList = new ArrayList<>();
                fontList.add(baseFont);
                AcroFields acroFields = pdfStamper.getAcroFields();
                acroFields.setSubstitutionFonts(fontList);

                if (CollUtil.isNotEmpty(pdfFieldMaps)) {
                    for (PDFFieldMap one : pdfFieldMaps) {
                        String name = one.getName();
                        String fieldName = one.getFieldName();
                        PDFType type = one.getType();
                        String fieldValue = one.getFieldValue();
                        boolean isTime = one.isTime();
                        String datePattern = one.getDatePattern();

                        switch (type) {
                            case TEXT:
                                addTextToPDFCenter(acroFields, pdfStamper, name, fieldValue, Element.ALIGN_CENTER, Element.ALIGN_BOTTOM, baseFont);
                                break;
                            case IMAGES:
                                addImageToPDF(acroFields, pdfStamper, name, fieldValue, true, request);
                                break;
                            default:
                                break;
                        }
                    }
                }

                pdfStamper.setFormFlattening(true);
                pdfStamper.close();
                reader.close();
                String mainName = FileUtil.mainName(url);
                String extName = FileUtil.extName(url);
                return mainName + "." + extName;
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
