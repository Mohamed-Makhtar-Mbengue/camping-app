package com.camping.pms.email;

import com.camping.pms.bookings.Booking;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(102, 126, 234);
    private static final DeviceRgb LIGHT_BG = new DeviceRgb(245, 247, 250);

    public byte[] generateBonEchange(Booking booking) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // En-tête
        Paragraph header = new Paragraph("🏕️ CAMPING PMS")
                .setFontSize(24)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(header);

        Paragraph subHeader = new Paragraph("BON D'ÉCHANGE — RÉSERVATION CONFIRMÉE")
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subHeader);

        // Numéro de réservation
        Paragraph bookingId = new Paragraph("N° Réservation : " + booking.getId().toString().substring(0, 8).toUpperCase())
                .setFontSize(12)
                .setBold()
                .setBackgroundColor(PRIMARY_COLOR)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(bookingId);

        // Infos client
        document.add(new Paragraph("INFORMATIONS CLIENT").setBold().setFontSize(13).setFontColor(PRIMARY_COLOR));
        document.add(createLine());

        Table clientTable = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(clientTable, "Nom & Prénom", booking.getCustomer().getLastName() + " " + booking.getCustomer().getFirstName());
        addRow(clientTable, "Email", booking.getCustomer().getEmail());
        addRow(clientTable, "Téléphone", booking.getCustomer().getPhone() != null ? booking.getCustomer().getPhone() : "—");
        addRow(clientTable, "Adresse", booking.getAddress() != null ? booking.getAddress() : "—");
        addRow(clientTable, "Code postal / Ville", (booking.getPostalCode() != null ? booking.getPostalCode() : "") + " " + (booking.getCity() != null ? booking.getCity() : ""));
        addRow(clientTable, "Pays", booking.getCountry() != null ? booking.getCountry() : "France");
        document.add(clientTable);
        document.add(new Paragraph("\n"));

        // Infos séjour
        document.add(new Paragraph("DÉTAILS DU SÉJOUR").setBold().setFontSize(13).setFontColor(PRIMARY_COLOR));
        document.add(createLine());

        Table stayTable = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(stayTable, "Hébergement", booking.getAccommodation().getName());
        addRow(stayTable, "Type", booking.getAccommodation().getType());
        addRow(stayTable, "Arrivée", booking.getStartDate().format(DATE_FORMAT) + " (à partir de 15h00)");
        addRow(stayTable, "Départ", booking.getEndDate().format(DATE_FORMAT) + " (avant 11h00)");

        long nights = booking.getStartDate().until(booking.getEndDate()).getDays();
        addRow(stayTable, "Durée", nights + " nuit(s)");
        addRow(stayTable, "Adultes (+18 ans)", String.valueOf(booking.getAdults()));
        addRow(stayTable, "Enfants (-18 ans)", String.valueOf(booking.getChildren() != null ? booking.getChildren() : 0));
        document.add(stayTable);
        document.add(new Paragraph("\n"));

        // Véhicule
        document.add(new Paragraph("VÉHICULE(S)").setBold().setFontSize(13).setFontColor(PRIMARY_COLOR));
        document.add(createLine());

        Table vehicleTable = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(vehicleTable, "Type de véhicule", booking.getVehicleType() != null ? booking.getVehicleType() : "—");
        addRow(vehicleTable, "Plaque d'immatriculation", booking.getLicensePlate() != null ? booking.getLicensePlate() : "—");
        addRow(vehicleTable, "2ème véhicule", booking.getSecondVehicle() != null ? booking.getSecondVehicle() : "—");
        document.add(vehicleTable);
        document.add(new Paragraph("\n"));

        // Section animaux
        document.add(new Paragraph("ANIMAUX").setBold().setFontSize(13).setFontColor(PRIMARY_COLOR));
        document.add(createLine());

        Table animalTable = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        boolean hasAnimal = booking.getPets() != null && booking.getPets() > 0;
        addRow(animalTable, "Animal(aux)", hasAnimal ? booking.getPets() + " animal(aux)" : "Aucun");
        if (hasAnimal) {
            addRow(animalTable, "Type d'animal", booking.getAnimalType() != null ? booking.getAnimalType() : "—");
            addRow(animalTable, "Race", booking.getAnimalBreed() != null ? booking.getAnimalBreed() : "—");
            addRow(animalTable, "Tatoué", Boolean.TRUE.equals(booking.getAnimalTattooed()) ? "✓ Oui" : "✗ Non");
            addRow(animalTable, "Vacciné", Boolean.TRUE.equals(booking.getAnimalVaccinated()) ? "✓ Oui" : "✗ Non");
        }
        document.add(animalTable);

        // Prix
        document.add(new Paragraph("TARIFICATION").setBold().setFontSize(13).setFontColor(PRIMARY_COLOR));
        document.add(createLine());

        Table priceTable = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        addRow(priceTable, "Hébergement", booking.getTotalPrice() + " €");
        if (booking.getSupplementsTotal() != null && booking.getSupplementsTotal().compareTo(java.math.BigDecimal.ZERO) > 0) {
            addRow(priceTable, "Suppléments", booking.getSupplementsTotal() + " €");
            addRow(priceTable, "TOTAL", booking.getTotalPrice().add(booking.getSupplementsTotal()) + " €");
        } else {
            addRow(priceTable, "TOTAL", booking.getTotalPrice() + " €");
        }
        document.add(priceTable);
        document.add(new Paragraph("\n"));

        // Règlement intérieur
        document.add(new Paragraph("RÈGLEMENT INTÉRIEUR").setBold().setFontSize(13).setFontColor(PRIMARY_COLOR));
        document.add(createLine());

        String[] rules = {
            "• Arrivée à partir de 15h00 — Départ avant 11h00",
            "• Le calme est de rigueur entre 22h00 et 08h00",
            "• Vitesse limitée à 5 km/h sur tout le camping",
            "• Les barbecues au charbon sont interdits (barbecues électriques ou à gaz autorisés)",
            "• Animaux acceptés uniquement s'ils sont tatoués et vaccinés — tenu en laisse obligatoire",
            "• Le tri sélectif des déchets est obligatoire",
            "• Toute dégradation sera facturée au locataire",
            "• Le camping décline toute responsabilité en cas de vol ou accident",
            "• En cas de restitution non conforme, un forfait ménage sera facturé",
            "• Ce bon d'échange doit être présenté à l'accueil à l'arrivée"
        };

        for (String rule : rules) {
            document.add(new Paragraph(rule).setFontSize(9).setMarginBottom(4));
        }

        document.add(new Paragraph("\n"));

        // Footer
        Paragraph footer = new Paragraph("Camping PMS — Bon d'échange généré le " + java.time.LocalDate.now().format(DATE_FORMAT))
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    private LineSeparator createLine() {
        return new LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine()).setMarginBottom(8);
    }

    private void addRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold().setFontSize(10))
                .setBackgroundColor(LIGHT_BG).setPadding(6));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "—").setFontSize(10))
                .setPadding(6));
    }
}