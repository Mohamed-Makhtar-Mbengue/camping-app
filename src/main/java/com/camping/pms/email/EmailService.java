package com.camping.pms.email;

import com.camping.pms.bookings.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final PdfService pdfService;

    public EmailService(JavaMailSender mailSender, PdfService pdfService) {
        this.mailSender = mailSender;
        this.pdfService = pdfService;
    }

    public void sendConfirmationEmail(Booking booking) {
        try {
            byte[] pdfBytes = pdfService.generateBonEchange(booking);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getCustomer().getEmail());
            helper.setSubject("✅ Confirmation de votre réservation — Camping PMS");
            helper.setText(buildEmailBody(booking), true);
            helper.addAttachment("bon-echange-" + booking.getId() + ".pdf", 
                () -> new java.io.ByteArrayInputStream(pdfBytes), 
                "application/pdf");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur envoi email", e);
        }
    }

    private String buildEmailBody(Booking booking) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <div style="background: #667eea; padding: 24px; text-align: center;">
                    <h1 style="color: white; margin: 0;">🏕️ Camping PMS</h1>
                </div>
                <div style="padding: 32px;">
                    <h2>Bonjour %s %s,</h2>
                    <p>Votre réservation a été <strong style="color: #4caf50;">confirmée</strong> !</p>
                    
                    <div style="background: #f5f7fa; padding: 20px; border-radius: 8px; margin: 24px 0;">
                        <h3>📋 Récapitulatif</h3>
                        <p><strong>Hébergement :</strong> %s</p>
                        <p><strong>Arrivée :</strong> %s</p>
                        <p><strong>Départ :</strong> %s</p>
                        <p><strong>Adultes :</strong> %d</p>
                        <p><strong>Enfants :</strong> %d</p>
                        <p><strong>Total :</strong> <span style="color: #667eea; font-size: 18px;">%s€</span></p>
                    </div>
                    
                    <p>Votre <strong>bon d'échange</strong> est joint en pièce jointe PDF.</p>
                    <p>Présentez-le à votre arrivée.</p>
                    
                    <div style="background: #fff3cd; padding: 16px; border-radius: 8px; margin: 24px 0;">
                        <h3>📜 Conditions importantes</h3>
                        <ul>
                            <li>Arrivée à partir de 15h00</li>
                            <li>Départ avant 11h00</li>
                            <li>Animaux acceptés (tatoués et vaccinés uniquement)</li>
                            <li>Le calme est de rigueur après 22h00</li>
                            <li>Les barbecues au charbon sont interdits</li>
                            <li>Vitesse limitée à 5 km/h sur le camping</li>
                        </ul>
                    </div>
                    
                    <p>À bientôt au camping !</p>
                    <p><em>L'équipe Camping PMS</em></p>
                </div>
            </body>
            </html>
            """.formatted(
                booking.getCustomer().getFirstName(),
                booking.getCustomer().getLastName(),
                booking.getAccommodation().getName(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getAdults(),
                booking.getChildren(),
                booking.getTotalPrice()
            );
    }
}