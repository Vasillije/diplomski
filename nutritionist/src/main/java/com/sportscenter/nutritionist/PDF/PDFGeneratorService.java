package com.sportscenter.nutritionist.PDF;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PDFGeneratorService {
    public void export(ByteArrayOutputStream outputStream, String mondayText, String tuesdayText, String wednesdayText,
                       String thursdayText, String fridayText, String saturdayText, String sundayText) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph paragraph = new Paragraph("Meal plan", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(12);

        document.add(new Paragraph("Meal plan", fontTitle));
        document.add(new Paragraph("\nMonday:\n" + mondayText, fontParagraph));
        document.add(new Paragraph("\nTuesday:\n" + tuesdayText, fontParagraph));
        document.add(new Paragraph("\nWednesday:\n" + wednesdayText, fontParagraph));
        document.add(new Paragraph("\nThursday:\n" + thursdayText, fontParagraph));
        document.add(new Paragraph("\nFriday:\n" + fridayText, fontParagraph));
        document.add(new Paragraph("\nSaturday:\n" + saturdayText, fontParagraph));
        document.add(new Paragraph("\nSunday:\n" + sundayText, fontParagraph));
        document.close();
    }

    public void export1(ByteArrayOutputStream outputStream, String text) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph paragraph = new Paragraph("Meal plan", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(12);

        document.add(new Paragraph("Meal plan", fontTitle));
        document.add(new Paragraph(text, fontParagraph));

        document.close();
    }
}
