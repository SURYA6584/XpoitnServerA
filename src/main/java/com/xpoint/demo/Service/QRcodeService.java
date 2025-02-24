package com.xpoint.demo.Service;

import java.io.IOException;

import com.google.zxing.WriterException;

public interface QRcodeService {

    public byte[] generateQRCode(String text) throws WriterException, IOException;
    public void generateQRCodeToFile(String text, String filePath) throws WriterException, IOException ;

}
