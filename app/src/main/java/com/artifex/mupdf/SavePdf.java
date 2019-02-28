package com.artifex.mupdf;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.jammy.pdf_demo.PDFActivity;
import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

import static android.content.ContentValues.TAG;


/**
 * Created by csl
 */
public class SavePdf {

    public void setWidthScale(float widthScale) {
        this.widthScale = widthScale;
    }

    public void setHeightScale(float heightScale) {
        this.heightScale = heightScale;
    }

    float widthScale;
    float heightScale;
    String inPath;//当前的PDF地址
    String outPath;//要输出的PDF地址
    private int pageNum;//签名所在的页码
    private Bitmap bitmap;//签名图像
    private float scale;
    private float density;  //屏幕的分辨率密度

    /**
     * 设置放大比例
     * @param scale
     */
    public void setScale(float scale) {
        this.scale = scale;
    }


    /**
     * 设置分辨率密度
     *
     * @param density
     */
    public void setDensity(float density) {
        this.density = density;
    }

    /**
     * 设置嵌入的图片
     *
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * 设置需要嵌入的页面
     *663，820
     * @param pageNum
     */
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public SavePdf(String inPath, String outPath) {
        this.inPath = inPath;
        this.outPath = outPath;
    }

    /**
     * 将图片加入PDF并保存
     */
    public void addText() {
        try {
            //打开要写入的PDF
            PdfReader reader = new PdfReader(inPath, "PDF".getBytes());
            //设置涂鸦后的PDF
            FileOutputStream outputStream = new FileOutputStream(outPath);
            PdfStamper stamp;
            stamp = new PdfStamper(reader, outputStream);
            //用于设置在第几页打印签名
            PdfContentByte over = stamp.getOverContent(pageNum);

            //用pdfreader获得当前页字典对象.包含了该页的一些数据.比如该页的坐标轴
            PdfDictionary p = reader.getPageN(pageNum);
            PdfObject po =  p.get(new PdfName("MediaBox"));
            //po是一个数组对象.里面包含了该页pdf的坐标轴范围.
            PdfArray pa = (PdfArray) po;

            byte[] bytes = Bitmap2Bytes(bitmap);
            //将要放到PDF的图片传过来，要设置为byte[]类型
            Image img = Image.getInstance(bytes);
            Rectangle rectangle = reader.getPageSize(pageNum);
            //设置图片对齐
            img.setAlignment(1);
            //设置Image图片大小，需要根据屏幕的分辨率，签名时PDF的放大比例来计算；还有就是当PDF开始显示的时候，这里已经做了一次缩放，
            // 可以用 rectangle.getWidth() / (bitmap.getWidth() / 2)求得那个放大比
            img.scaleAbsolute(180 * 1.0f * density / 2 / scale * rectangle.getWidth() / (bitmap.getWidth() / 2),
                    180 * 1.0f * density / 2 / scale * rectangle.getWidth() / (bitmap.getWidth() / 2));
            //设置image相对PDF左下角的偏移量，得到放大后位置相对于整个PDF的百分比再乘PDF的大小得到他的相对偏移位置
            writingPosition(img ,pa.getAsNumber(pa.size()-1).floatValue());
//            img.setAbsolutePosition(rectangle.getWidth() * widthScale, rectangle.getHeight() * heightScale);
            over.addImage(img);
            stamp.close();
        } catch (FileNotFoundException e) {
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将BitMap转换为Bytes
     *
     * @param bm
     * @return
     */
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 功能：处理要书写pdf位置
     * @param img
     */
    private void writingPosition(Image img ,float pdfHigth){
        int pdfSizeX = MuPDFPageView.pdfSizeX;
        int pdfSizeY = MuPDFPageView.pdfSizeY;
        int pdfPatchX = MuPDFPageView.pdfPatchX;
        int pdfPatchY = MuPDFPageView.pdfPatchY;
        int pdfPatchWidth = MuPDFPageView.pdfPatchWidth;
        int pdfPatchHeight = MuPDFPageView.pdfPatchHeight;
        int y = PDFActivity.y+180;
        float n = pdfPatchWidth*1.0f;
        float m = pdfPatchHeight*1.0f;
        n = pdfSizeX/n;
        m = pdfSizeY/m;
        if(n == 1.0f){
            //pdf页面没有放大时的比例190
            if(PDFActivity.y >= 900){
                img.setAbsolutePosition(PDFActivity.x *2/4,pdfHigth-((PDFActivity.y+190)*2/4));
            }else if(PDFActivity.y <= 60){
                img.setAbsolutePosition(PDFActivity.x*3/6,pdfHigth-150);
            }else{
                img.setAbsolutePosition(PDFActivity.x*2/4,pdfHigth-((PDFActivity.y+190)*2/4));
            }
        }else{
            n = (PDFActivity.x+pdfPatchX)/n;
            m = (PDFActivity.y+pdfPatchY)/m;
            img.setAbsolutePosition(n*5/6,pdfHigth-((m+120)*5/6));
        }
    }

    /**
     * 返回32位UUID字符串
     * @return
     */
    public static String getUUID32(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }
}
