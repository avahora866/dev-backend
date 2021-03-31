package com.milk4u.doorstep.delivery.pdf;

import java.io.FileOutputStream;
import java.util.*;
import java.util.Date;
import java.util.Optional;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfWriter;
import com.milk4u.doorstep.delivery.entity.CurrentOrderEntity;
import com.milk4u.doorstep.delivery.entity.DroplistEntity;
import com.milk4u.doorstep.delivery.entity.ProductEntity;
import com.milk4u.doorstep.delivery.entity.UserEntity;
import com.milk4u.doorstep.delivery.repository.CurrentOrderRepository;
import com.milk4u.doorstep.delivery.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import com.milk4u.doorstep.delivery.controller.Controller;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import response.CustomerResponse;

@Component
public class FirstPdf {
    private static String FILE = "c:/Users/User/Documents/Droplist.pdf";
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    public static void createPDF(UserEntity driver, java.util.List<Optional<UserEntity>> allCustomers, List<List<CustomerResponse>> allCustomerResponses) {
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

    private static void addTitlePage(Document document, UserEntity driver) throws DocumentException {
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

    private static void addContent(Document document, List<Optional<UserEntity>> allCustomers, List<List<CustomerResponse>> allCustomerResponses) throws DocumentException {
        for(int i = 0; i < allCustomers.size(); i++){
            Anchor anchor = new Anchor("Customer ID: " + allCustomers.get(i).get().getUserId(), catFont);
            anchor.setName("Customer Name: " + allCustomers.get(i).get().getfName() + allCustomers.get(i).get().getlName() +"\nPostCode: "+ allCustomers.get(i).get().getPostcode());
            Chapter catPart = new Chapter(new Paragraph(anchor), 1);
            Paragraph subPara = new Paragraph("Products", subFont);
            Section subCatPart = catPart.addSection(subPara);
            List<CustomerResponse> cstOrder = allCustomerResponses.get(i);
            int totalPrice = 0;
            for(int j = 0; j < cstOrder.size(); j++){
                String product = "Product Name: "+cstOrder.get(j).getName() + "\nProduct Description: " + cstOrder.get(j).getDescription() + "\nProduct Price: £" + cstOrder.get(j).getPrice() + "\nProduct Quantity: " + cstOrder.get(j).getQuantity();
                subCatPart.add(new Paragraph(product));
                totalPrice += cstOrder.get(j).getPrice() * cstOrder.get(j).getQuantity();
            }
            subCatPart.add(new Paragraph("Total Price: " + totalPrice));
            document.add(catPart);
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

}