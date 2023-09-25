package com.moneydance.modules.features.reportwriter2.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Database;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.Parameters;
import com.moneydance.modules.features.reportwriter2.RWException;
import com.moneydance.modules.features.reportwriter2.Utilities;
import com.moneydance.modules.features.reportwriter2.databeans.BeanAnnotations.BEANFIELDTYPE;
import com.moneydance.modules.features.reportwriter2.view.PopUpScreen;
import com.moneydance.modules.features.reportwriter2.view.controls.ExpressionProcessor;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportBanner;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportLayout;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;
import com.moneydance.modules.features.reportwriter2.view.tables.ReportDataRow;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.print.PageLayout;
import javafx.print.PrintQuality;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class Report {
	private ReportTemplate template;
	private ReportDataRow rowEdit;
	private Database database;
	private Parameters params;
	private PrinterJob job;
	private Printer defaultPrinter;
	private ReportBanner title = null;
	private ReportBanner columnHeader = null;
	private ReportBanner pageHeader = null;
	private ReportBanner columnFooter = null;
	private ReportBanner pageFooter = null;
	private ReportBanner end = null;
	private ReportBanner detail = null;
	private List<ReportPage> pages = null;
	private ReportPage crntPage = null;
	private boolean titlePrinted = false;
	private boolean columnHeaderPrinted = false;
	private boolean columnFooterPrinted = false;
	private boolean pageHeaderPrinted = false;
	private boolean pageFooterPrinted = false;
	private SortedMap<Integer, GroupValues> groupHeadValues;
	private SortedMap<Integer, GroupValues> groupFootValues;
	private SortedMap<String, FieldValue>variableValues;
	private GroupValues detailValues;
	private GroupValues titleValues;
	private GroupValues pageHeaderValues;
	private GroupValues columnHeaderValues;
	private GroupValues columnFooterValues;
	private GroupValues pageFooterValues;
	private GroupValues endPageValues;
	private List<Integer> groupsWithHead;
	private List<Integer> groupsWithFoot;
	private SortedMap<String, ReportField> variables;
	private SortedMap<String, ReportField> fields;
	private List<ReportField> sortedVariables;
	private PageLayout pageLayout;

	public Report(Parameters params, Database database, ReportDataRow rowEdit) throws RWException {
		this.rowEdit = rowEdit;
		this.database = database;
		this.params = params;
		String templateName = this.rowEdit.getTemplate();
		template = new ReportTemplate(this.params);
		template.setName(templateName);
		if (!template.loadTemplate()) {
			JOptionPane.showMessageDialog(null, "Can not find template " + templateName);
			throw new RWException();
		}
		variables = template.getVariables();
		fields = template.getSelectedFields();
		groupHeadValues = new TreeMap<Integer, GroupValues>();
		groupFootValues = new TreeMap<Integer, GroupValues>();
		groupsWithHead = new ArrayList<Integer>();
		groupsWithFoot = new ArrayList<Integer>();
		detailValues = new GroupValues();
		/*
		 * set up values
		 */
		for (ReportBanner banner : template.getBanners()) {
			if (!banner.isVisible())
				continue;
			switch (banner.getBannerType()) {
			case COLUMNFOOT:
				columnFooter = banner;
				columnFooterValues = new GroupValues(); 
				break;
			case COLUMNHEAD:
				columnHeader = banner;
				columnHeaderValues = new GroupValues();
				break;
			case DETAIL:
				detail = banner;
				detailValues = new GroupValues();
				break;
			case END:
				end = banner;
				endPageValues = new GroupValues();
				break;
			case GROUPFOOT:
				groupsWithFoot.add(banner.getPosition());
				GroupValues footValues = groupFootValues.get(banner.getPosition());
				/*
				 *   If group footer values do not exist we need to create them with the group field.
				 */
				if (footValues == null) {
					footValues = new GroupValues(banner.getGroupField());
					groupFootValues.put(banner.getPosition(), footValues);
				}
				break;
			case GROUPHEAD:
				groupsWithHead.add(banner.getPosition());
				GroupValues headValues = groupHeadValues.get(banner.getPosition());
				/*
				 *   If group footer values do not exist we need to create them with the group field.
				 */
				if (headValues == null) {
					headValues = new GroupValues(banner.getGroupField());
					groupHeadValues.put(banner.getPosition(), headValues);
				}
				break;
			case PAGEFOOT:
				pageFooter = banner;
				pageFooterValues = new GroupValues();
				break;
			case PAGEHEAD:
				pageHeader = banner;
				pageHeaderValues = new GroupValues();
				break;
			case TITLE:
				title = banner;
				titleValues = new GroupValues();
				break;
			default:
				break;

			}
		}
		/*
		 * group headers are traversed in reverse order so most detailed is checked first.
		 */
		Collections.sort(groupsWithHead, Collections.reverseOrder());
		/*
		 * group footers will have the most detailed with the lowest position
		 */
		Collections.sort(groupsWithFoot);
		template.determineVariables();
		sortedVariables = template.getSortedVariables();
	}

	public boolean buildReport() throws RWException {
		ResultSet results = null;
		String sql = template.getDatasetSQL();
		GridPane scenePane;
		try {
			results = database.executeQuery(sql);
		} catch (RWException e) {
			JOptionPane.showMessageDialog(null, "Error reading data from database");
			throw new RWException(e.getLocalizedMessage());
		}
		if (results == null) {
			Main.extension.updateProgress("No data retrieved for Report");
			return false;
		}
		defaultPrinter = Printer.getDefaultPrinter();
		PopUpScreen popUp = new PopUpScreen((Integer parm) -> {
			if (parm == PopUpScreen.OKPRESSED)
				return PopUpScreen.CLOSESCREEN;
			if (parm == PopUpScreen.CANCELPRESSED)
				return PopUpScreen.CANCELLED;
			return PopUpScreen.LEAVEOPEN;
		});
		scenePane = popUp.getPane();
		Label printersLbl = new Label("Select Printer");
		ChoiceBox<Printer> printerChooser = new ChoiceBox<Printer>(
				FXCollections.observableArrayList(Printer.getAllPrinters()));
		printerChooser.getSelectionModel().select(defaultPrinter);
		printersLbl.setPadding(new Insets(5, 5, 5, 5));
		printerChooser.setPadding(new Insets(5, 5, 5, 5));
		scenePane.add(printersLbl, 0, 0);
		scenePane.add(printerChooser, 0, 1);
		popUp.display();
		if (popUp.getResult().equals(PopUpScreen.CANCELLED))
			return false;
		Printer selectedPrinter = printerChooser.getSelectionModel().getSelectedItem();
		job = PrinterJob.createPrinterJob(selectedPrinter);

		pageLayout = job.getPrinter().createPageLayout(template.getPaperSize(), template.getOrientation(),
				Printer.MarginType.HARDWARE_MINIMUM);
		double pageWidth = pageLayout.getPrintableWidth();
		double pageHeight = pageLayout.getPrintableHeight();
		job.getJobSettings().setPrintQuality(PrintQuality.HIGH);
		pages = new ArrayList<ReportPage>();
		try {
			while (results.next()) {
				/*
				 * create page banners if beginning of page
				 */
				if (crntPage == null) {
					crntPage = new ReportPage(template, pageWidth, pageHeight);
					pages.add(crntPage);
				}
				if (!titlePrinted && title != null && title.isBannerVisible()) {
					calculateBannerValues(results, title,titleValues,false);
					buildBanner(title, results, titleValues);
					titlePrinted = true;
				}
				if (!pageHeaderPrinted && pageHeader != null && pageHeader.isBannerVisible()) {
					calculateBannerValues(results, pageHeader,pageHeaderValues,false);
					buildBanner(pageHeader, results,pageHeaderValues);
					pageHeaderPrinted = true;
				}
				if (!columnHeaderPrinted && columnHeader != null && columnHeader.isBannerVisible()) {
					calculateBannerValues(results, columnHeader,columnHeaderValues,false);
					buildBanner(columnHeader, results,columnHeaderValues);
					columnHeaderPrinted = true;
				}
				/*
				 *  determine if footers need printing. A footer needs printing if the value of the detail does not match the 
				 *  group field value
				 */
				List<Integer> footersPrinted = new ArrayList<Integer>();
				for (Integer footerPos : groupsWithFoot) {
					FieldValue detailFieldValue = null;
					FieldValue crntGroupValue = null;
					GroupValues groupValues = groupFootValues.get(footerPos);
					if (groupValues != null) {
						ReportField groupField = groupValues.getKeyField();
						crntGroupValue = groupValues.getCurrentValue();
						/*
						 * crntGroupValue will be null on first page
						 */
						if (crntGroupValue==null) { 
							try {
								detailFieldValue = groupField.getResultsValue(results);
							} catch (RWException e) {
								throw new RWException(
										"Problem reading value of field " + groupField.getName());
							}
							groupValues.setCurrrentValue(detailFieldValue);
						}
						else {
							/*
							 * not first time through so check to see if value has changed
							 */
							try {
								detailFieldValue = groupField.getResultsValue(results);
							} catch (RWException e) {
								throw new RWException(
										"Problem reading value of field " + groupField.getName());
							}
							/*
							 * check that group value has changed
							 */
							if ((groupField.getFieldType()== BEANFIELDTYPE.STRING && (!crntGroupValue.getText().equals(detailFieldValue.getText()))
									|| (groupField.getFieldType()!= BEANFIELDTYPE.STRING && (crntGroupValue.getNumeric() !=detailFieldValue.getNumeric())))){
								/*
								 *  value changed print all footers with lower positions
								 */
								for (Integer printedPos : groupsWithFoot) {
									if (printedPos >= footerPos)
										break;
									if (!footersPrinted.contains(printedPos)) {
										buildBanner(template.getBannerByPos(printedPos), results,groupFootValues.get(printedPos));
										footersPrinted.add(printedPos);
										resetBannerValues(template.getBannerByPos(printedPos),groupFootValues.get(printedPos));
									}
								}
								buildBanner(template.getBannerByPos(footerPos), results,groupValues);
								groupValues.setCurrrentValue(detailFieldValue);
							}
						}
					}
				}
				/*
				 *  determine if headers need printing
				 */
				List<Integer> headersPrinted = new ArrayList<Integer>();
				for (Integer headerPos : groupsWithHead) {
					FieldValue detailFieldValue =null;
					FieldValue crntGroupValue = null;
					GroupValues groupValues = groupHeadValues.get(headerPos);
					if (groupValues != null) {
						ReportField groupField = groupValues.getKeyField();
						crntGroupValue = groupValues.getCurrentValue();
						/*
						 * crntGroupValue will be null on first page
						 */
						if (crntGroupValue==null) {
							try {
								detailFieldValue = groupField.getResultsValue(results);
							} catch (RWException e) {
								throw new RWException(
										"Problem reading value of field " + groupField.getName());
							}
							calculateBannerValues(results,template.getBannerByPos(headerPos),groupHeadValues.get(headerPos),false);
							groupValues.setCurrrentValue(detailFieldValue);
							buildBanner(template.getBannerByPos(headerPos),results,groupValues);
						}
						else {
							/*
							 * not first time through so check to see if value has changed
							 */

							try {
								detailFieldValue = groupField.getResultsValue(results);
							} catch (RWException e) {
								throw new RWException(
										"Problem reading value of field " + groupField.getName());
							}
							/*
							 * check that group value has changed
							 */
							if ((groupField.getFieldType()== BEANFIELDTYPE.STRING && (!crntGroupValue.getText().equals(detailFieldValue.getText()))
									|| (groupField.getFieldType()!= BEANFIELDTYPE.STRING && (crntGroupValue.getNumeric() !=detailFieldValue.getNumeric())))){
								/*
								 *  value changed print all headers with higher positions
								 */
								for (Integer printedPos : groupsWithHead) {
									if (printedPos < headerPos)
										break;
									if (!headersPrinted.contains(printedPos)) {
										calculateBannerValues(results,template.getBannerByPos(headerPos),groupHeadValues.get(headerPos),false);
										buildBanner(template.getBannerByPos(printedPos), results,groupValues);
										headersPrinted.add(printedPos);
									}
								}

							}
						}
						groupValues.setCurrrentValue(detailFieldValue);
					}
				}
				buildBanner(detail, results,detailValues);
				if (title != null)
					calculateBannerValues(results, title,titleValues,true);
				if (columnHeader != null)
					calculateBannerValues(results, columnHeader,columnHeaderValues,true);
				if (pageHeader != null)
					calculateBannerValues(results,pageHeader,pageHeaderValues,true);
				if (end != null)
					calculateBannerValues(results, end,endPageValues,true);
				if (columnFooter != null)
					calculateBannerValues(results, columnFooter,columnFooterValues,true);
				if (pageFooter != null)
					calculateBannerValues(results,pageFooter,pageFooterValues,true);
				for (Integer headerPos : groupsWithHead) {
					calculateBannerValues(results,template.getBannerByPos(headerPos),groupHeadValues.get(headerPos),true);
				}
				for (Integer footerPos : groupsWithFoot) {
					calculateBannerValues(results,template.getBannerByPos(footerPos),groupFootValues.get(footerPos),true);
				}
				double spaceReq = Utilities.screenToPrinter(detail.getCanvasHeight() / Constants.LAYOUTDIVIDER)
						+ Utilities.screenToPrinter(template.getBottomMargin() * 10.0);
				if (columnFooter != null && columnFooter.isBannerVisible())
					spaceReq += Utilities
					.screenToPrinter(columnFooter.getCanvasHeight() / Constants.LAYOUTDIVIDER);
				if (pageFooter != null && pageFooter.isBannerVisible())
					spaceReq += Utilities
					.screenToPrinter(columnFooter.getCanvasHeight() / Constants.LAYOUTDIVIDER);
				;
				if (spaceReq > crntPage.getSpaceLeft()) {
					if (!columnFooterPrinted && columnFooter != null && columnFooter.isBannerVisible()) {
						buildBanner(columnFooter, results,columnFooterValues);
						columnFooterPrinted = true;
					}
					if (!pageFooterPrinted && pageFooter != null && pageFooter.isBannerVisible()) {
						buildBanner(pageFooter, results,pageFooterValues);
						pageFooterPrinted = true;
					}
					crntPage = null;
					pageHeaderPrinted = false;
					columnHeaderPrinted = false;
				}
			}
			if (!columnFooterPrinted && columnFooter != null && columnFooter.isBannerVisible()) {
				if (Utilities.screenToPrinter(columnFooter.getCanvasHeight()
						/ Constants.LAYOUTDIVIDER) > crntPage.getSpaceLeft()) {
					crntPage = new ReportPage(template, pageWidth, pageHeight);
					pages.add(crntPage);
				}
				buildBanner(columnFooter, results,columnFooterValues);
				columnFooterPrinted = true;
			}
			if (!pageFooterPrinted && pageFooter != null && pageFooter.isBannerVisible()) {
				if (Utilities.screenToPrinter(pageFooter.getCanvasHeight() / Constants.LAYOUTDIVIDER) > crntPage
						.getSpaceLeft()) {
					crntPage = new ReportPage(template, pageWidth, pageHeight);
					pages.add(crntPage);
				}
				buildBanner(pageFooter, results,pageFooterValues);
				pageFooterPrinted = true;
			}
			if (end != null && end.isBannerVisible()) {
				if (Utilities.screenToPrinter(end.getCanvasHeight() / Constants.LAYOUTDIVIDER) > crntPage
						.getSpaceLeft()) {
					crntPage = new ReportPage(template, pageWidth, pageHeight);
					pages.add(crntPage);
				}
				buildBanner(end, results,endPageValues);
			}
		} catch (SQLException e) {
			OptionMessage.displayMessage("SQL failed. See error log for more details ");
			e.printStackTrace();
			Main.rwDebugInst.debugThread("Report", "buildReport", MRBDebug.DETAILED, "SQL exception " + sql);
			throw new RWException("Database error retrieval " + e.getLocalizedMessage());
		}
		database.closeQuery();
		database.close();
		return true;
	}


	private void buildBanner(ReportBanner banner,ResultSet results,GroupValues groupValues) {
		if (banner.isTopLine())
			crntPage.writeLine(0);
		for (ReportLayout field:banner.getFields()) {
			try {
				crntPage.addField(field,results,groupValues);
			}
			catch (RWException e) {

			}
		}
		if (banner.isBottomLine())
			crntPage.writeLine(banner.getHeight());

		crntPage.nextBanner(banner.getCanvasHeight());
	}

	public void printReport() {
		for (ReportPage page : pages) {
			job.printPage(pageLayout, page);
		}
		job.endJob();
	}
	private void calculateDetailValues(ResultSet results,GroupValues values,boolean increment) {
		List <ReportField> variablesInBanner=new ArrayList<ReportField>();
		for (ReportLayout layout:detail.getFields()) {
			if (layout.getField() != null && layout.getType()==Constants.ReportFieldType.VARIABLE){
				variablesInBanner.add(layout.getField());
			}
		}
		Collections.sort(variablesInBanner,new DepthComparator());
		variableValues = values.getVariableValues();
		for (ReportField field:variablesInBanner) {
			ExpressionProcessor proc= new ExpressionProcessor(template, field);
			try {
				FieldValue tmpValue =proc.calculateValue( field.getFieldExp(),results, values.getValues(),variableValues,template,increment);
				variableValues.put(field.getKey(), tmpValue);
			}
			catch (RWException e) {

			}

		}
	}
	private void calculateBannerValues(ResultSet results,ReportBanner banner, GroupValues values,boolean increment) {
		List <ReportField> variablesInBanner=new ArrayList<ReportField>();
		List <ReportField> fieldsInBanner = new ArrayList<ReportField>();
		for (ReportLayout layout:banner.getFields()) {
			if (layout.getField() != null) {
				if ( layout.getType()==Constants.ReportFieldType.VARIABLE)
					variablesInBanner.add(layout.getField());
				else if (layout.getType() == Constants.ReportFieldType.DATABASE)
					fieldsInBanner.add(layout.getField());
			}
		}
		Collections.sort(variablesInBanner,new DepthComparator());
		variableValues = values.getVariableValues();
		for (ReportField field:variablesInBanner) {
			ExpressionProcessor proc= new ExpressionProcessor(template, field);
			try {
				FieldValue tmpValue = proc.calculateValue( field.getFieldExp(),results, values.getValues(),variableValues,template,increment);
				variableValues.put(field.getKey(), tmpValue);
			}
			catch (RWException e) {

			}
		}
		for (ReportField field:fieldsInBanner) {
			try {
				FieldValue tmpValue = field.getResultsValue(results);
				variableValues.put(field.getKey(), tmpValue);
			}
			catch (RWException e) {

			}

		}
	}
	private void resetBannerValues(ReportBanner banner,GroupValues values) {

	}

	class DepthComparator implements java.util.Comparator<ReportField>{
		@Override
		public int compare(ReportField a, ReportField b) {
			return a.getDepth()-b.getDepth();
		}
	}

}
