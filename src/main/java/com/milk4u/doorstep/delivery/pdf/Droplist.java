package com.milk4u.doorstep.delivery.pdf;

import java.io.FileOutputStream;
import java.util.*;
import java.util.Date;
import java.util.Optional;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfWriter;
import com.milk4u.doorstep.delivery.entity.UsersEntity;
import org.springframework.stereotype.Component;
import com.milk4u.doorstep.delivery.response.CustomerResponse;

@Component
public class Droplist {
    private static String FILE = "c:/Users/User/Documents/Droplist.pdf";
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    public static void createPDF(UsersEntity driver, java.util.List<Optional<UsersEntity>> allCustomers, List<List<CustomerResponse>> allCustomerResponses) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            document.open();
            addMetaData(document);
            addTitlePage(document, driver);
            addContent(document, allCustomers, allCustomerResponses);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
    private static void addMetaData(Document document) {
        document.addTitle("Droplist");
        document.addSubject("Driver");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("Abraar Vahora");
        document.addCreator("Abraar Vahora");
    }

    private static void addTitlePage(Document document, UsersEntity driver) throws DocumentException {
        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header
        preface.add(new Paragraph("Droplist for " + driver.getfName() +" "+ driver.getlName() , catFont));

        addEmptyLine(preface, 1);
        // Will create: Report generated by: _name, _date
        preface.add(new Paragraph(
                "Report generated by: " + "Abraar Vahora, " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                smallBold));
        // Start a new page
        document.add(preface);
        document.newPage();
    }

    private static void addContent(Document document, List<Optional<UsersEntity>> allCustomers, List<List<CustomerResponse>> allCustomerResponses) throws DocumentException {
        for(int i = 0; i < allCustomers.size(); i++){
            Chapter catPart = new Chapter(new Paragraph("Customer ID: " + allCustomers.get(i).get().getUserId(), catFont), i + 1);
            Paragraph nameAndAreaPara = new Paragraph("Customer Details", subFont);
            Section nameAndArea = catPart.addSection(nameAndAreaPara);
            nameAndArea.add(new Paragraph("Customer Name: " + allCustomers.get(i).get().getfName() + allCustomers.get(i).get().getlName() +"\nPost Code: "+ allCustomers.get(i).get().getPostcode()));
            Paragraph subPara = new Paragraph("Products", subFont);
            Section subCatPart = catPart.addSection(subPara);
            List<CustomerResponse> cstOrder = allCustomerResponses.get(i);
            int totalPrice = 0;
            for(int j = 0; j < cstOrder.size(); j++){
                String product = "Product Name: "+cstOrder.get(j).getName() + "\nProduct Description: " + cstOrder.get(j).getDescription() + "\nProduct Price: £" + cstOrder.get(j).getPrice() + "\nProduct Quantity: " + cstOrder.get(j).getQuantity();
                subCatPart.add(new Paragraph(product));
                totalPrice += cstOrder.get(j).getPrice() * cstOrder.get(j).getQuantity();
            }
            subCatPart.add(new Paragraph("Total Price: £" + totalPrice));
            document.add(catPart);
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

}