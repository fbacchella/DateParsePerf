package ethanjones.boxplot;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.DOMGroupManager;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BoxPlot {

    public static int overallScale = 10;
    public static int bpHeight = overallScale * 5;

    public static void main(String[] args) {
        BoxPlot fictionPrice = new BoxPlot("Fiction");
        fictionPrice.q1 = 5.24;
        fictionPrice.q2 = 5.59;
        fictionPrice.q3 = 6.29;
        fictionPrice.low = 3.99;
        fictionPrice.high = 7.00;
        fictionPrice.outliers = new double[]{13.60, 9.09, 12.91, 8.39, 10.49, 8.55, 1.00};

        BoxPlot nonfictionPrice = new BoxPlot("Non fiction");
        nonfictionPrice.q1 = 5.565;
        nonfictionPrice.q2 = 6.99;
        nonfictionPrice.q3 = 10.49;
        nonfictionPrice.low = 1.00;
        nonfictionPrice.high = 17.68;
        nonfictionPrice.outliers = new double[]{26.00};

        BoxPlot childrensPrice = new BoxPlot("Children's ");
        childrensPrice.q1 = 4.89;
        childrensPrice.q2 = 5.24;
        childrensPrice.q3 = 5.24;
        childrensPrice.low = 4.49;
        childrensPrice.high = 5.59;
        childrensPrice.outliers = new double[]{6.49, 5.84, 8.24, 7.69, 3.99, 20.40, 3.38, 7.17, 7.00};

        generate("price", 0, 30, 5, 10, "£", "", fictionPrice, nonfictionPrice, childrensPrice);

        BoxPlot fictionPages = new BoxPlot("Fiction");
        fictionPages.q1 = 352;
        fictionPages.q2 = 400;
        fictionPages.q3 = 468;
        fictionPages.low = 192;
        fictionPages.high = 624;
        fictionPages.outliers = new double[]{/*112, 136, 768, 1008*/};

        BoxPlot nonfictionPages = new BoxPlot("Non fiction");
        nonfictionPages.q1 = 112;
        nonfictionPages.q2 = 264;
        nonfictionPages.q3 = 320;
        nonfictionPages.low = 32;
        nonfictionPages.high = 576;
        nonfictionPages.outliers = new double[]{/*784*/};

        BoxPlot childrensPages = new BoxPlot("Children's ");
        childrensPages.q1 = 32;
        childrensPages.q2 = 224;
        childrensPages.q3 = 336;
        childrensPages.low = 32;
        childrensPages.high = 640;
        childrensPages.outliers = new double[]{/*816*/};

        generate("pages", 0, 1100, 100, 0.5, "", "", fictionPages, nonfictionPages, childrensPages);
    }

    public static void generate(String filename, int chartMin, int chartMax, int interval, double scale, String unitBefore, String unitAfter, BoxPlot... boxPlots) {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        SVGGraphics svgGenerator = new SVGGraphics(document);

        // The height of the plotted boxes
        int height = (int) (((boxPlots.length * 1.5) + 0.5) * bpHeight);
        int xoffset = bpHeight * 4;
        // grid
        svgGenerator.setPaint(Color.LIGHT_GRAY);
        int width = 0;
        for (int i = 1; i <= chartMax; i+=(interval/5)) {
            svgGenerator.drawLine((int) (xoffset + (i * scale)), 1,(int) (xoffset + (i * scale)), height);
            width = Math.max(width, (int) (xoffset + (i * scale)));
        }

        svgGenerator.setPaint(Color.BLACK);
        // x axis
        svgGenerator.drawLine(xoffset, 1, xoffset, height);
        // y axis
        svgGenerator.drawLine(xoffset, height,(int) (xoffset + (chartMax - chartMin) * scale), height);
        // x axis labels
        for (int i = 0; i <= chartMax; i += interval) {
            anchoredText(svgGenerator, unitBefore + i + unitAfter,(int) (xoffset + (i * scale)), height + (int) (bpHeight * 0.25), "middle");
        }

        for (int i = 0; i < boxPlots.length; i++) {
            // label
            anchoredText(svgGenerator, boxPlots[i].name, xoffset - overallScale, (int) (((i * 1.5) + 1) * bpHeight), "end");
            // draw
            boxPlots[i].paint(svgGenerator, scale, xoffset, (int) (((i * 1.5) + 0.5) * bpHeight));
        }
        svgGenerator.setSVGCanvasSize(new Dimension((int)(width * 1.1), height + 20));

        try (Writer svgOut = new FileWriter(new File(filename))) {
            svgGenerator.stream(svgOut, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void anchoredText(SVGGraphics svgGenerator, String string, int x, int y, String textAnchor) {
        Element text = svgGenerator.getDOMFactory().createElementNS(SVGConstants.SVG_NAMESPACE_URI, SVGConstants.SVG_TEXT_TAG);
        text.setAttributeNS(null, SVGConstants.SVG_X_ATTRIBUTE, svgGenerator.generatorCtx().doubleString(x));
        text.setAttributeNS(null, SVGConstants.SVG_Y_ATTRIBUTE, svgGenerator.generatorCtx().doubleString(y));
        //center text
        text.setAttributeNS(null, "text-anchor", textAnchor);

        text.setAttributeNS(SVGConstants.XML_NAMESPACE_URI,
                            SVGConstants.XML_SPACE_QNAME,
                            SVGConstants.XML_PRESERVE_VALUE);
        text.appendChild(svgGenerator.getDOMFactory().createTextNode(string));
        svgGenerator.domGroupManager().addElement(text, DOMGroupManager.FILL);
    }

    public String name;
    public double[] outliers;
    public double q1;
    public double q2;
    public double q3;
    public double low;
    public double high;

    public BoxPlot(String s) {
        this.name = s;
    }

    public void paint(Graphics2D g2d, double scale, int xoffset, int yoffset) {
        // q1 line
        g2d.drawLine(xoffset + (int) (q1 * scale), yoffset, xoffset + (int) (q1 * scale), yoffset + bpHeight);
        // q2 line
        g2d.drawLine(xoffset + (int) (q2 * scale), yoffset, xoffset + (int) (q2 * scale), yoffset + bpHeight);
        // q3 line
        g2d.drawLine(xoffset + (int) (q3 * scale), yoffset, xoffset + (int) (q3 * scale), yoffset + bpHeight);
        // top
        g2d.drawLine(xoffset + (int) (q1 * scale), yoffset, xoffset + (int) (q3 * scale), yoffset);
        // bottom
        g2d.drawLine(xoffset + (int) (q1 * scale), yoffset + bpHeight, xoffset + (int) (q3 * scale), yoffset + bpHeight);

        // left
        g2d.drawLine(xoffset + (int) (low * scale), (int) (bpHeight * 0.25) + yoffset, xoffset + (int) (low * scale), (int) (bpHeight * 0.75) + yoffset);
        // left line
        g2d.drawLine(xoffset + (int) (low * scale), (int) (bpHeight * 0.5) + yoffset, xoffset + (int) (q1 * scale), (int) (bpHeight * 0.5) + yoffset);
        // right
        g2d.drawLine(xoffset + (int) (high * scale), (int) (bpHeight * 0.25) + yoffset, xoffset + (int) (high * scale), (int) (bpHeight * 0.75) + yoffset);
        // right line
        g2d.drawLine(xoffset + (int) (q3 * scale), (int) (bpHeight * 0.5) + yoffset, xoffset + (int) (high * scale), (int) (bpHeight * 0.5) + yoffset);

        // outliers
        for (double outlier : outliers) {
            g2d.drawOval(xoffset + (int) (outlier * scale), (int) (bpHeight * 0.5) + yoffset - 1, 2, 2);
        }
    }
}
