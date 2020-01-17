package com.moneydance.modules.features.mrbutil;
 
 import java.awt.Color;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;

import com.moneydance.apps.md.view.gui.MoneydanceGUI;
 /**
  * Defines the printer to be used for printing.  Uses standard dialogs to ask for printer.
  * <p>
  * Obtains the orientation and page sizes from the printer. Calls the printPage method in the 
  * Report Printer to construct the page 
  * @author Mike Bray
  *
  */
 public class MRBPrinter 
   implements Printable
 {
   private PrinterJob objPrintJob = null;
   private MRBPrintable objPrinter=null;
   private int iLastPage = -1;
   private double dWidth;
   private double dHeight;
   
 
   /**
    * Creates the Printer object.  
    * @param objPrinterp The Report Printer object to be used
    * @return true if successful, false if not
    */
   public boolean print(MRBPrintable objPrinterp)
   {
     objPrintJob = PrinterJob.getPrinterJob();
     if (objPrintJob == null) {
       return false;
     }
 
     PrintService ps = objPrintJob.getPrintService();
     if (ps == null) {
        return false;
     }
 
     objPrinter = objPrinterp;
     try
     {
       PageFormat objPageFormat = objPrintJob.defaultPage();
       objPageFormat.setOrientation(PageFormat.LANDSCAPE);
       objPageFormat = objPrintJob.validatePage(objPageFormat);
       Paper objPaper = objPageFormat.getPaper();
       dHeight = objPaper.getHeight();
       dWidth = objPaper.getWidth();
       objPaper.setImageableArea(0.0D, 0.0D, dWidth, dHeight);
       objPageFormat.setPaper(objPaper);
 
       objPrintJob.setPrintable(this, objPageFormat);
     } catch (Exception e) {
       e.printStackTrace(System.err);
       return false;
     }
 
     if (!objPrintJob.printDialog()) {
       return false;
     }
     try
     {
       objPrintJob.print();
     } catch (Exception e) {
       e.printStackTrace(System.err);
       return false;
     }
 
     return true;
   }
 
   @Override
   public int print(Graphics g, PageFormat pageFormat, int pageIndex)
     throws PrinterException
   {
 
     if ((iLastPage >= 0) && (pageIndex > iLastPage)) return 1;
     double dHeight;
     double dWidth;
     if (objPrinter.usesWholePage())
     {
       dWidth = pageFormat.getWidth();
       dHeight = pageFormat.getHeight();
     } else {
       dWidth = pageFormat.getWidth() - 72.0D;
       dHeight = pageFormat.getHeight() - 72.0D;
       int iImageableX = 36;
       int iImageableY = 36;
 
       if (MoneydanceGUI.isMac);
       		g = g.create(iImageableX, iImageableY, (int)dWidth, (int)dHeight);
     }
 
     g.setColor(Color.black);
 
     if (!objPrinter.printPage(g, pageIndex, dWidth, dHeight, 72))
     {
       iLastPage = pageIndex;
     }
 
     return 0;
   }
}
