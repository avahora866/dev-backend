package com.milk4u.doorstep.delivery.pdf;

import com.spire.pdf.*;
import java.awt.print.*;

public class PrintingExample {
    public static void main(String[] args) {
        //load the sample document
        PdfDocument pdf = new PdfDocument();
        pdf.loadFromFile("c:/Users/User/Documents/Droplist.pdf");

        PrinterJob loPrinterJob = PrinterJob.getPrinterJob();
        PageFormat loPageFormat  = loPrinterJob.defaultPage();
        Paper loPaper = loPageFormat.getPaper();

        //remove the default printing margins
        loPaper.setImageableArea(0,0,loPageFormat.getWidth(),loPageFormat.getHeight());

        //set the number of copies
        loPrinterJob.setCopies(1);

        loPageFormat.setPaper(loPaper);
        loPrinterJob.setPrintable(pdf,loPageFormat);
        try {
            loPrinterJob.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }
}

