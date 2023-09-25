package com.moneydance.modules.features.reportwriter2.view.screenctrl;

import java.io.File;
import java.lang.reflect.Field;
import java.util.SortedMap;

import com.moneydance.modules.features.reportwriter2.Constants;
import com.moneydance.modules.features.reportwriter2.Constants.DatasetType;
import com.moneydance.modules.features.reportwriter2.Constants.ReportFieldType;
import com.moneydance.modules.features.reportwriter2.FieldSelectListener;
import com.moneydance.modules.features.reportwriter2.Main;
import com.moneydance.modules.features.reportwriter2.OptionMessage;
import com.moneydance.modules.features.reportwriter2.databeans.AccountBean;
import com.moneydance.modules.features.reportwriter2.databeans.AddressBean;
import com.moneydance.modules.features.reportwriter2.databeans.BeanAnnotations.ColumnName;
import com.moneydance.modules.features.reportwriter2.databeans.BeanAnnotations.ColumnTitle;
import com.moneydance.modules.features.reportwriter2.databeans.BeanAnnotations.FieldType;
import com.moneydance.modules.features.reportwriter2.view.FieldPane;
import com.moneydance.modules.features.reportwriter2.view.LayoutPane;
import com.moneydance.modules.features.reportwriter2.view.TemplateDataPane;
import com.moneydance.modules.features.reportwriter2.view.TemplatePane;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportField;
import com.moneydance.modules.features.reportwriter2.view.controls.ReportTemplate;
import com.moneydance.modules.features.reportwriter2.databeans.BudgetBean;
import com.moneydance.modules.features.reportwriter2.databeans.CategoryBean;
import com.moneydance.modules.features.reportwriter2.databeans.CurrencyBean;
import com.moneydance.modules.features.reportwriter2.databeans.DataBean;
import com.moneydance.modules.features.reportwriter2.databeans.InvTranBean;
import com.moneydance.modules.features.reportwriter2.databeans.ReminderBean;
import com.moneydance.modules.features.reportwriter2.databeans.SecurityBean;
import com.moneydance.modules.features.reportwriter2.databeans.TransactionBean;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;

public class TemplateDataPaneController {
	private ReportTemplate template;
	private LayoutPane layoutPane = null;
	private TemplateDataPane callingPane;
	public FieldPane fields = null;
	private boolean addTemplate = false;
	private TemplatePane templatePane;
	private boolean dataDirty = false;
	@FXML
	private TextField templateName;
	@FXML
	private RadioButton tranMain;
	@FXML
	private RadioButton acctMain;
	@FXML
	private RadioButton budgMain;
	@FXML
	private RadioButton catMain;
	@FXML
	private RadioButton curMain;
	@FXML
	private RadioButton secMain;
	@FXML
	private RadioButton remMain;
	@FXML
	private RadioButton addMain;
	@FXML
	private RadioButton invMain;
	@FXML
	private RadioButton question1;
	@FXML
	private RadioButton question2;
	@FXML
	private RadioButton question3;
	@FXML
	private RadioButton question4;
	@FXML
	private Button layoutBtn;
	@FXML
	private Button okBtn;
	@FXML
	private Button saveBtn;
	@FXML
	private Button saveAsBtn;
	@FXML
	private Button cancelBtn;
	@FXML
	private ToggleGroup mainDataset;

	public void setFields(TemplatePane templatePane,TemplateDataPane callingPane, ReportTemplate template) {
		this.templatePane = templatePane;
		this.callingPane = callingPane;
		this.template = template;
		if (template.getName() != null)
			templateName.setText(this.template.getName());
		templateName.setEditable(false);
		if (template.getDataset() != null) {
			switch (template.getDataset()) {
			case ACCOUNT:
				acctMain.setSelected(true);
				AccountBean acctBean = new AccountBean();
				template.setDataset(DatasetType.ACCOUNT);
				updateFields(acctBean, true);
				break;
			case ADDRESS:
				addMain.setSelected(true);
				AddressBean addBean = new AddressBean();
				template.setDataset(DatasetType.ADDRESS);
				updateFields(addBean, true);
				break;
			case BUDGET:
				budgMain.setSelected(true);
				BudgetBean budBean = new BudgetBean();
				template.setDataset(DatasetType.BUDGET);
				updateFields(budBean, true);
				break;
			case CATEGORY:
				catMain.setSelected(true);
				CategoryBean catBean = new CategoryBean();
				template.setDataset(DatasetType.CATEGORY);
				updateFields(catBean, true);
				break;
			case INVESTMENT:
				invMain.setSelected(true);
				InvTranBean invBean = new InvTranBean();
				template.setDataset(DatasetType.INVESTMENT);
				updateFields(invBean, true);
				break;
			case REMINDER:
				remMain.setSelected(true);
				ReminderBean remBean = new ReminderBean();
				template.setDataset(DatasetType.REMINDER);
				updateFields(remBean, true);
				break;
			case SECURITY:
				secMain.setSelected(true);
				SecurityBean secBean = new SecurityBean();
				template.setDataset(DatasetType.SECURITY);
				updateFields(secBean, true);
				break;
			case TRANSACTION:
				tranMain.setSelected(true);
				TransactionBean bean = new TransactionBean();
				template.setDataset(DatasetType.TRANSACTION);
				updateFields(bean, true);
				break;
			default:
				break;
			}
			setSecondary(template.getDataset());
		}
		question1.setSelected(template.isQuestion1());
		question2.setSelected(template.isQuestion2());
		question3.setSelected(template.isQuestion3());
		question4.setSelected(template.isQuestion4());
		if (Main.loadedIcons.closeImg == null)
			okBtn.setText("Close");
		else
			okBtn.setGraphic(new ImageView(Main.loadedIcons.closeImg));
		if (Main.loadedIcons.saveImg == null)
			saveBtn.setText("Save");
		else
			saveBtn.setGraphic(new ImageView(Main.loadedIcons.saveImg));
		if (Main.loadedIcons.copyImg == null)
			saveAsBtn.setText("Save As");
		else
			saveAsBtn.setGraphic(new ImageView(Main.loadedIcons.copyImg));
		if (Main.loadedIcons.cancelImg == null)
			cancelBtn.setText("Cancel");
		else
			cancelBtn.setGraphic(new ImageView(Main.loadedIcons.cancelImg));
		templateName.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue)
				templateNameChg(templateName.getText());
		});
		setSecondary(template.getDataset());
	}

	public void setAdd() {
		templateName.setEditable(true);
		addTemplate = true;
	}

	@FXML
	private void setTran(ActionEvent action) {
		template.setDirty(true);
		RadioButton source = (RadioButton) action.getSource();
		TransactionBean bean = new TransactionBean();
		if (source.isSelected())
			updateFields(bean, true);
		else
			updateFields(bean, false);
		template.setDataset(DatasetType.TRANSACTION);
		template.setDirty(true);
		setSecondary(template.getDataset());
	}

	@FXML
	private void setAcct(ActionEvent action) {
		template.setDirty(true);
		RadioButton source = (RadioButton) action.getSource();
		AccountBean bean = new AccountBean();
		if (source.isSelected())
			updateFields(bean, true);
		else
			updateFields(bean, false);
		template.setDataset(DatasetType.ACCOUNT);
		template.setDirty(true);
		question1.setVisible(true);
		setSecondary(template.getDataset());
	}

	@FXML
	private void setBudg(ActionEvent action) {
		template.setDirty(true);
		RadioButton source = (RadioButton) action.getSource();
		BudgetBean bean = new BudgetBean();
		if (source.isSelected())
			updateFields(bean, true);
		else
			updateFields(bean, false);
		template.setDataset(DatasetType.BUDGET);
		template.setDirty(true);
		setSecondary(template.getDataset());
	}

	@FXML
	private void setCat(ActionEvent action) {
		template.setDirty(true);
		RadioButton source = (RadioButton) action.getSource();
		CategoryBean bean = new CategoryBean();
		if (source.isSelected())
			updateFields(bean, true);
		else
			updateFields(bean, false);
		template.setDataset(DatasetType.CATEGORY);
		template.setDirty(true);
		setSecondary(template.getDataset());
	}

	@FXML
	private void setCur(ActionEvent action) {
		template.setDirty(true);
		RadioButton source = (RadioButton) action.getSource();
		CurrencyBean bean = new CurrencyBean();
		if (source.isSelected())
			updateFields(bean, true);
		else
			updateFields(bean, false);
		template.setDataset(DatasetType.CURRENCY);
		template.setDirty(true);
		setSecondary(template.getDataset());
	}

	@FXML
	private void setSec(ActionEvent action) {
		template.setDirty(true);
		RadioButton source = (RadioButton) action.getSource();
		SecurityBean bean = new SecurityBean();
		if (source.isSelected())
			updateFields(bean, true);
		else
			updateFields(bean, false);
		template.setDataset(DatasetType.SECURITY);
		template.setDirty(true);
		setSecondary(template.getDataset());
	}

	@FXML
	private void setRem(ActionEvent action) {
		template.setDirty(true);
		RadioButton source = (RadioButton) action.getSource();
		ReminderBean bean = new ReminderBean();
		if (source.isSelected())
			updateFields(bean, true);
		else
			updateFields(bean, false);
		template.setDataset(DatasetType.REMINDER);
		template.setDirty(true);
		setSecondary(template.getDataset());
	}

	@FXML
	private void setAdd(ActionEvent action) {
		template.setDirty(true);
		RadioButton source = (RadioButton) action.getSource();
		AddressBean bean = new AddressBean();
		if (source.isSelected())
			updateFields(bean, true);
		else
			updateFields(bean, false);
		template.setDataset(DatasetType.ADDRESS);
		template.setDirty(true);
		setSecondary(template.getDataset());
	}

	@FXML
	private void setInv(ActionEvent action) {
		template.setDirty(true);
		RadioButton source = (RadioButton) action.getSource();
		InvTranBean bean = new InvTranBean();
		if (source.isSelected())
			updateFields(bean, true);
		else
			updateFields(bean, false);
		template.setDataset(DatasetType.INVESTMENT);
		template.setDirty(true);
		setSecondary(template.getDataset());
	}

	@FXML
	private void setQ1(ActionEvent action) {
		template.setDirty(true);
		template.setQuestion1(question1.isSelected());
	}

	@FXML
	private void setQ2(ActionEvent action) {
		template.setDirty(true);
		template.setQuestion2(question1.isSelected());
	}

	@FXML
	private void setQ3(ActionEvent action) {
		template.setDirty(true);
		template.setQuestion3(question1.isSelected());
	}

	@FXML
	private void setQ4(ActionEvent action) {
		template.setDirty(true);
		template.setQuestion4(question1.isSelected());
	}

	private void setSecondary(DatasetType type) {
		if (type == null) {
			question1.setVisible(false);
			question2.setVisible(false);
			question3.setVisible(false);
			question4.setVisible(false);
			return;
		}
		switch (type) {
		case ACCOUNT:
			question1.setVisible(true);
			question1.setText("Add Transaction Information");
			question2.setVisible(true);
			question2.setText("Add Category Information");
			question3.setVisible(false);
			question4.setVisible(false);
			break;
		case ADDRESS:
			question1.setVisible(false);
			question2.setVisible(false);
			question3.setVisible(false);
			question4.setVisible(false);
			break;
		case BUDGET:
			question1.setVisible(true);
			question1.setText("Add Budget Items");
			question2.setVisible(true);
			question2.setText("Add Category Information");
			question3.setText("Add Transaction Information");
			question3.setVisible(true);
			question4.setVisible(false);
			break;
		case CATEGORY:
			question1.setVisible(true);
			question1.setText("Add Transaction Information");
			question2.setVisible(false);
			question3.setVisible(false);
			question4.setVisible(false);
			break;
		case CURRENCY:
			question1.setVisible(true);
			question1.setText("Add Currency Rates");
			question2.setVisible(false);
			question3.setVisible(false);
			question4.setVisible(false);
			break;
		case INVESTMENT:
			question1.setVisible(true);
			question1.setText("Add Security Information");
			question2.setVisible(true);
			question2.setText("Add Category Information");
			question3.setVisible(true);
			question3.setText("Add Account Information");
			question4.setVisible(false);
			break;
		case REMINDER:
			question1.setVisible(false);
			question2.setVisible(false);
			question3.setVisible(false);
			question4.setVisible(false);
			break;
		case SECURITY:
			question1.setVisible(true);
			question1.setText("Add Security Prices");
			question2.setVisible(true);
			question2.setText("Add Investment Transaction Information");
			question3.setVisible(false);
			question4.setVisible(false);
			break;
		case TRANSACTION:
			question1.setVisible(true);
			question1.setText("Add Account Information");
			question2.setVisible(true);
			question2.setText("Add Category Information");
			question3.setVisible(false);
			question4.setVisible(false);
			break;
		default:
			question1.setVisible(false);
			question2.setVisible(false);
			question3.setVisible(false);
			question4.setVisible(false);
			break;
		}
	}

	@FXML
	private void selectMain(ActionEvent action) {
		if (mainDataset.getSelectedToggle() == null) {
			OptionMessage.displayMessage("You must select a Main Dataset first");
			return;
		}
		fields = new FieldPane(template);
		if (layoutPane != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					layoutPane.notifyFieldPane();
				}
			});
		}
	}

	@FXML
	private void cancel(ActionEvent action) {
		if (dataDirty || template.isDirty())
			if (!OptionMessage.yesnoMessageFX("Template has been changed.  Do you wish to abandon these changes"))
				return;
		if (layoutPane != null)
			layoutPane.call(Constants.CLOSELAYOUTSCREEN);
		if (fields != null)
			fields.call(Constants.CLOSEFIELDLIST);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				callingPane.closeDown();
			}
		});
	}

	@FXML
	private void layoutReport(ActionEvent action) {
		if (templateName==null || templateName.getText().isEmpty()) {
			OptionMessage.displayMessage("Template Name must be entered");
			return;
		}		
		layoutPane = new LayoutPane(template, this);

	}

	@FXML
	private void saveTemplate(ActionEvent action) {
		saveTemplate();
	}

	@FXML
	private void saveAndClose(ActionEvent action) {
		if (saveTemplate()) {
			if (layoutPane != null)
				layoutPane.call(Constants.CLOSELAYOUTSCREEN);
			if (fields != null)
				fields.call(Constants.CLOSEFIELDLIST);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					callingPane.closeDown();
				}
			});
		}
	}


	@FXML
	private void saveAsTemplate(ActionEvent action) {
		String newName;
		while (true) {
			newName = OptionMessage.inputMessage("Enter the name of the new template");
			if (newName.equals(Constants.CANCELPRESSED))
				return;
			if (template.saveAs(newName))
				break;
		}
		saveTemplate();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				templatePane.resetData();
			}
		});
	}



	private void templateNameChg(String name) {
		template.setName(name);
	}

	private void updateFields(DataBean bean, boolean add) {
		Field fields[] = bean.getClass().getDeclaredFields();
		SortedMap<String, ReportField> available = template.getAvailableFields();
		SortedMap<String, ReportField> selectedFields = template.getSelectedFields();
		for (Field field : fields) {
			if (!field.isAnnotationPresent(ColumnTitle.class)) // not database field
				continue;
			String fldName = field.isAnnotationPresent(ColumnTitle.class)
					? field.getAnnotation(ColumnTitle.class).value()
					: "";
			String fldShortName = field.isAnnotationPresent(ColumnName.class)
					? field.getAnnotation(ColumnName.class).value()
					: "";
			String fldKey = (bean.getShortTableName() + "." + fldShortName).toLowerCase();

			if (add) {
				if (!available.containsKey(fldKey)) {
					ReportField repField = new ReportField(template);
					repField.setFieldBean(bean);
					repField.setName(fldName);
					repField.setFieldText(fldName);
					repField.setReportType(ReportFieldType.DATABASE);
					repField.setFieldType(field.getAnnotation(FieldType.class).value());
					repField.setKey(fldKey);
					available.put(fldKey, repField);
				}
			} else {
				if (available.containsKey(fldKey)) {
					available.remove(fldKey);
					if (selectedFields.containsKey(fldKey))
						selectedFields.remove(fldKey);
				}
			}
		}
		template.setAvailableFields(available);
	}
	private boolean checkFileName(String fileName){
		if (new File(fileName).isFile())
			return true;
		return false;
	}
	private boolean saveTemplate() {
		if (addTemplate && templateName.getText().isEmpty()) {
			OptionMessage.displayMessage("Template Name must be entered");
			return false;
		}
		template.saveTemplate();
		return true;

	}

	public void addFieldListener(FieldSelectListener listener) {
		if (fields != null)
			fields.addFieldListener(listener);
	}

	public void removeFieldListener(FieldSelectListener listener) {
		if (fields != null)
			fields.removeFieldListener(listener);
	}

	public void closeLayoutScreen() {
		layoutPane = null;
		callingPane.requestExtFocus();
	}
}
