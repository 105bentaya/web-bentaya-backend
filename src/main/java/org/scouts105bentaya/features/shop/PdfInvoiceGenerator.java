package org.scouts105bentaya.features.shop;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import jakarta.mail.util.ByteArrayDataSource;
import org.scouts105bentaya.features.shop.entity.BoughtProduct;
import org.scouts105bentaya.features.shop.entity.ShopPurchase;
import org.scouts105bentaya.shared.util.PdfUtils;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class PdfInvoiceGenerator {
    private static final DateTimeFormatter INVOICE_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ByteArrayDataSource generateInvoicePdf(ShopPurchase shopPurchase) {
        PdfPTable table = new PdfPTable(12);
        PdfCreator creator = new PdfCreator(table);

        PdfUtils.createTitleHeader(table);
        this.addInvoiceHeader(creator, shopPurchase);
        shopPurchase.getBoughtProducts().forEach(product -> addProductData(creator, product));
        this.addInvoiceEnd(creator, shopPurchase);

        ByteArrayDataSource dataSource = PdfUtils.generatePdfByteArrayDataSourceFromPdfPTable(table);
        dataSource.setName("pedido_%s_105bentaya".formatted(shopPurchase.getPayment().getOrderNumber()));

        return dataSource;
    }

    private void addInvoiceHeader(PdfCreator creator, ShopPurchase shopPurchase) {
        creator.addInlineParagraph("Nº de Pedido: ", shopPurchase.getPayment().getOrderNumber());
        creator.addInlineParagraph("Fecha: ", shopPurchase.getPayment().getModificationDate().format(INVOICE_DATE));
        creator.addDivider();
        creator.addHeaderCell("Descripción", 6, Element.ALIGN_LEFT);
        creator.addHeaderCell("Precio", 2, Element.ALIGN_CENTER);
        creator.addHeaderCell("Cantidad", 2, Element.ALIGN_CENTER);
        creator.addHeaderCell("Importe", 2, Element.ALIGN_CENTER);
        creator.addDivider();
    }

    private void addProductData(PdfCreator creator, BoughtProduct product) {
        String name = "%s (%s)".formatted(product.getProductName(), product.getSizeName());
        creator.addAlignCell(name, 6, Element.ALIGN_LEFT);
        creator.addAlignCell("%.2f€".formatted(product.getPrice() / 100f), 2, Element.ALIGN_CENTER);
        creator.addAlignCell("%d".formatted(product.getCount()), 2, Element.ALIGN_CENTER);
        float total = (product.getPrice() * product.getCount()) / 100f;
        creator.addAlignCell("%.2f€".formatted(total), 2, Element.ALIGN_CENTER);
    }

    private void addInvoiceEnd(PdfCreator creator, ShopPurchase shopPurchase) {
        creator.addDivider();
        creator.addInlineParagraph("IMPORTE TOTAL: ", String.format("%.2f€", shopPurchase.getPayment().getAmount() / 100f));
    }

    private record PdfCreator(PdfPTable table) {
        private void addHeaderCell(String title, int colspan, int alignment) {
            PdfPCell cell = new PdfPCell(new Paragraph(title, PdfUtils.BOLD_FONT));
            cell.setColspan(colspan);
            cell.setBorder(0);
            cell.setFixedHeight(23);
            cell.setHorizontalAlignment(alignment);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }

        private void addInlineParagraph(String title, String field) {
            PdfPCell cell = new PdfPCell();
            Paragraph p = new Paragraph();
            p.add(new Chunk(title, PdfUtils.BOLD_FONT));
            p.add(new Chunk(field, PdfUtils.NORMAL_FONT));
            p.setAlignment(Element.ALIGN_RIGHT);
            cell.addElement(p);
            cell.setBorder(0);
            cell.setColspan(12);
            cell.setVerticalAlignment(Element.ALIGN_BASELINE);
            table.addCell(cell);
        }

        private void addDivider() {
            PdfPCell separatorCell = new PdfPCell();
            separatorCell.setColspan(12);
            separatorCell.setBorder(0);
            separatorCell.setBorderWidthBottom(2);
            separatorCell.setBorderColor(WebColors.getRGBColor("#03569C"));
            table.addCell(separatorCell);
        }

        private void addAlignCell(String title, int colspan, int align) {
            PdfPCell cell = new PdfPCell(new Phrase(title, PdfUtils.NORMAL_FONT));
            cell.setColspan(colspan);
            cell.setBorder(0);
            cell.setFixedHeight(23);
            cell.setHorizontalAlignment(align);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }
    }
}
