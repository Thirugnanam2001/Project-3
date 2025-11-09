
package com.jts.movie_ticket_booking_system.services;

import com.jts.movie_ticket_booking_system.entity.Ticket;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class TicketPdfService {

    public void generateTicketPdf(Ticket ticket, OutputStream outputStream) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        try {
            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
            titleFont.setColor(0, 0, 255);
            Paragraph title = new Paragraph("MOVIE TICKET", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            document.add(new Chunk("\n"));

            // Movie Information Section
            Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            // Movie Details
            document.add(new Paragraph("MOVIE DETAILS", headingFont));
            document.add(new Paragraph("Movie: " + ticket.getShow().getMovie().getMovieName(), normalFont));
            document.add(new Paragraph("Genre: " + ticket.getShow().getMovie().getGenre(), normalFont));
            document.add(new Paragraph("Duration: " + ticket.getShow().getMovie().getDuration() + " mins", normalFont));
            document.add(new Paragraph("Language: " + ticket.getShow().getMovie().getLanguage(), normalFont));

            document.add(new Chunk("\n"));

            // Show Details
            document.add(new Paragraph("SHOW DETAILS", headingFont));
            document.add(new Paragraph("Theater: " + ticket.getShow().getTheater().getName(), normalFont));
            document.add(new Paragraph("Address: " + ticket.getShow().getTheater().getAddress(), normalFont));

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

            document.add(new Paragraph("Date: " + ticket.getShow().getDate().toLocalDate().format(dateFormatter), normalFont));
            document.add(new Paragraph("Time: " + ticket.getShow().getTime().toLocalTime().format(timeFormatter), normalFont));
            document.add(new Paragraph("Screen: Screen " + ticket.getShow().getId(), normalFont));

            document.add(new Chunk("\n"));

            // Booking Details
            document.add(new Paragraph("BOOKING DETAILS", headingFont));
            document.add(new Paragraph("Booking ID: " + ticket.getBookingId(), normalFont));
            document.add(new Paragraph("Seats: " + ticket.getBookedSeats(), normalFont));
            document.add(new Paragraph("Total Amount: ₹" + ticket.getTotalTicketsPrice(), headingFont));

            document.add(new Chunk("\n"));
            document.add(new Chunk("\n"));

            // Important Information
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);
            infoFont.setColor(255, 0, 0);
            Paragraph info = new Paragraph("IMPORTANT INFORMATION:", infoFont);
            document.add(info);

            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            document.add(new Paragraph("• Please arrive at least 30 minutes before the show", smallFont));
            document.add(new Paragraph("• Carry a valid ID proof for verification", smallFont));
            document.add(new Paragraph("• Tickets are non-refundable and non-transferable", smallFont));
            document.add(new Paragraph("• Children below 3 years are allowed free of charge", smallFont));

        } finally {
            document.close();
        }
    }
}