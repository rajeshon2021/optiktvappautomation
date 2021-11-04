package com.demo.commons.coreframework;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.openqa.selenium.WebDriver;

import com.demo.commons.bddinterface.KeywordStep;
import com.demo.commons.mobile.CustomDriver;
import com.demo.commons.report.FailType;
import com.demo.commons.report.Reporting;
import com.demo.commons.report.rebot.XMLTestNode;

public abstract class Keyword {

	private boolean isCritical;
	public String keywordName = FrameworkGlobalVariables.BLANK;

	public Keyword() {

	}

	public final String getKeywordName() {
		return !this.keywordName.isEmpty() ? keywordName : this.getClass().getName();
	}

	public Keyword(String keywordName) {
		this.keywordName = keywordName;
	}
	
	public void setKeywordName(String keywordName){
		this.keywordName = keywordName;
	}

	public final static void ReportStep_Pass_With_ScreenShot(TestCases testCase, String message, WebDriver driver) {
		if (testCase != null && driver != null && testCase.getScreenShotCount() < FrameworkGlobalVariables.MAX_SCREENSHOTS) {

			testCase.incrementScreenShotCount();
			String screenShotName = Reporting.takeScreenShot(testCase.getScrShotPath(), driver);
			message = "<b>[PASS]</b> " + message + "::::" + new File(testCase.getScrShotPath()).getName() + "/"
					+ screenShotName;
			Reporting.reportStep(testCase, Reporting.PASS, message,true);

		} else {
			ReportStep_Pass(testCase, message);
		}
	}

	public final static void ReportStep_Fail(TestCases testCase, int failType, String message, WebDriver driver, boolean... reportToKeyword) {

		if (testCase != null && driver != null && testCase.getScreenShotCount() < FrameworkGlobalVariables.MAX_SCREENSHOTS) {
			
			switch(failType){
			case FailType.FALSE_POSITIVE:
			case FailType.FRAMEWORK_CONFIGURATION:
				testCase.setFalsePositive(message);
				break;
			case FailType.NO_FAILURE:
				reportToKeyword = new boolean[] {false};
			default:
				break;
			}

			if(testCase.getFirstErrorMessage().isEmpty()){
				testCase.setFirstErrorMessage(message);
			}
			
			testCase.incrementScreenShotCount();

			String screenShotName = Reporting.takeScreenShot(testCase.getScrShotPath(), driver);

			if ("#".equals(screenShotName)) {
				ReportStep_Fail_WithOut_ScreenShot(testCase, failType, message);
			} else {
				
				if(failType==FailType.FALSE_POSITIVE){
					testCase.setFalsePositive(message);
				}
				
				message = "<b>[FAIL]</b> " + message + "::::" + new File(testCase.getScrShotPath()).getName() + "/"
						+ screenShotName;
				
				Reporting.reportStep(testCase, Reporting.FAIL, message,reportToKeyword.length>0?reportToKeyword[0]:true);
			}
		} else {
			ReportStep_Fail_WithOut_ScreenShot(testCase, failType, message);
		}

	}

	public final static void ReportStep_Pass_With_ScreenShot(TestCases testCase, String message,
			CustomDriver driver) {
		if (testCase != null && driver != null && testCase.getScreenShotCount() < FrameworkGlobalVariables.MAX_SCREENSHOTS) {

			testCase.incrementScreenShotCount();

			String screenShotName = Reporting.takeScreenShot(testCase.getScrShotPath(), driver);

			message = "<b>[PASS]</b> " + message + "::::" + new File(testCase.getScrShotPath()).getName() + "/"
					+ screenShotName;
			Reporting.reportStep(testCase, Reporting.PASS, message,true);
		} else {
			ReportStep_Pass(testCase, message);
		}
	}

	public final static void ReportStep_Fail(TestCases testCase, int failType, String message,
			CustomDriver driver,boolean... reportToKeyword) {

		if (testCase != null && driver != null && testCase.getScreenShotCount() < FrameworkGlobalVariables.MAX_SCREENSHOTS) {
			
			switch(failType){
			case FailType.FALSE_POSITIVE:
			case FailType.FRAMEWORK_CONFIGURATION:
				testCase.setFalsePositive(message);
				break;
			case FailType.NO_FAILURE:
				reportToKeyword = new boolean[] {false};
			default:
				break;
			}

			if(testCase.getFirstErrorMessage().isEmpty()){
				testCase.setFirstErrorMessage(message);
			}
			
			testCase.incrementScreenShotCount();

			String screenShotName = Reporting.takeScreenShot(testCase.getScrShotPath(), driver);

			message = "<b>[FAIL]</b> " + message + "::::" + new File(testCase.getScrShotPath()).getName() + "/"
					+ screenShotName;
			Reporting.reportStep(testCase, Reporting.FAIL, message,reportToKeyword.length>0?reportToKeyword[0]:true);
		} else {
			ReportStep_Fail_WithOut_ScreenShot(testCase, failType, message);
		}

	}

	public final static void ReportStep_Pass_With_ScreenShot(TestCases testCase, String message) {

		if (testCase != null && testCase.getScreenShotCount() < FrameworkGlobalVariables.MAX_SCREENSHOTS) {

			String screenShotName = "#";
			testCase.incrementScreenShotCount();

			if (testCase.isMobileTestCase()) {
				screenShotName = Reporting.takeScreenShot(testCase.getScrShotPath(), testCase.getMobileDriver());
			} else {
				if (testCase.isWebTestCase()) {
					screenShotName = Reporting.takeScreenShot(testCase.getScrShotPath(), testCase.getWebDriver());
				}
			}
			message = "<b>[PASS]</b> " + message + "::::" + new File(testCase.getScrShotPath()).getName() + "/"
					+ screenShotName;
			
			Reporting.reportStep(testCase, Reporting.PASS, message,true);
		} else {
			ReportStep_Pass(testCase, message);
		}
	}

	public final static void ReportStep_Fail(TestCases testCase, int failType, String message,boolean... reportToKeyword) {

		
		
		if (testCase != null && testCase.getScreenShotCount() < FrameworkGlobalVariables.MAX_SCREENSHOTS) {
			String screenShotName = "#";
			String failureType = FrameworkGlobalVariables.BLANK;
			switch(failType){
			case FailType.FALSE_POSITIVE:
				failureType = " - False Positive";
				testCase.setFalsePositive(message);
				break;
			case FailType.FRAMEWORK_CONFIGURATION:
				failureType = " - Framework Configuration";
				testCase.setFalsePositive(message);
				break;
			case FailType.NO_FAILURE:
				reportToKeyword = new boolean[] {false};
				failureType = " - No Failure";
				break;
			case FailType.COSMETIC_FAILURE:
				reportToKeyword = new boolean[] {false};
				failureType = " - Cosmetic Failure";
				break;
			default:
				break;
			}
			
			if(testCase.getFirstErrorMessage().isEmpty()){
				testCase.setFirstErrorMessage(message);
			}

			if (testCase.isMobileTestCase()) {
				if (testCase.getMobileDriver() != null) {
					
					screenShotName = Reporting.takeScreenShot(testCase.getScrShotPath(), testCase.getMobileDriver());
					testCase.incrementScreenShotCount();
					
					message = String.format("<b>[FAIL%s]</b> ", failureType) + message + "::::" + new File(testCase.getScrShotPath()).getName() + "/"
							+ screenShotName;

					Reporting.reportStep(testCase, Reporting.FAIL, message,reportToKeyword.length>0?reportToKeyword[0]:true);

					testCase.incrementScreenShotCount();

				} else {
					ReportStep_Fail_WithOut_ScreenShot(testCase, failType, message,reportToKeyword);
				}

			} else {
				if (testCase.isWebTestCase()) {

					screenShotName = Reporting.takeScreenShot(testCase.getScrShotPath(), testCase.getWebDriver());

					testCase.incrementScreenShotCount();
					message =  String.format("<b>[FAIL%s]</b> ", failureType) + message + "::::" + new File(testCase.getScrShotPath()).getName() + "/"
							+ screenShotName;

					Reporting.reportStep(testCase, Reporting.FAIL, message,reportToKeyword.length>0?reportToKeyword[0]:true);

					testCase.incrementScreenShotCount();
				} else {
					ReportStep_Fail_WithOut_ScreenShot(testCase, failType, message);
				}
			}

		} else {
			ReportStep_Fail_WithOut_ScreenShot(testCase, failType, message);
		}

	}

	public boolean isCritical() {
		return isCritical;
	}

	public void setCritical(boolean isCritical) {
		this.isCritical = isCritical;
	}

	public final static void ReportStep_Pass(TestCases testCase, String message) {

		if (testCase != null) {

			message = "<b>[PASS]</b> " + message;
			Reporting.reportStep(testCase, Reporting.PASS, message,true);

		} else {

		}

	}

	public final static void ReportStep_Fail_WithOut_ScreenShot(TestCases testCase, int failType, String message, boolean...reportToKeyword) {

		if (testCase != null) {
			
			String failureType = FrameworkGlobalVariables.BLANK;
			
			switch(failType){
			case FailType.FALSE_POSITIVE:
				failureType = " - False Positive";
				testCase.setFalsePositive(message);
				break;
			case FailType.FRAMEWORK_CONFIGURATION:
				failureType = " - Framework Configuration";
				break;
			case FailType.NO_FAILURE:
				failureType = " - No Failure";
				reportToKeyword = new boolean[] {false};
				break;
			case FailType.COSMETIC_FAILURE:
				failureType = " - Cosmetic Failure";
				reportToKeyword = new boolean[] {false};
			default:
				break;
			}
			
			if(testCase.getFirstErrorMessage().isEmpty()){
				testCase.setFirstErrorMessage(message);
			}
			
			message = String.format("<b>[FAIL%s]</b> ",failureType) + message + "::::#";
			
			Reporting.reportStep(testCase, Reporting.FAIL, message,reportToKeyword.length>0?reportToKeyword[0]:true);
		}
	}

	@BeforeKeyword
	public boolean preCondition() throws KeywordException{
		return true;
	}

	@KeywordStep
	public abstract boolean keywordSteps() throws KeywordException;

	@AfterKeyword
	public boolean postCondition() throws KeywordException{
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })

	public static final boolean execute(Class keywordClass, TestCases testCase, Object... inputs) {

		ArrayList<Class> inputTypes = new ArrayList<>();
		ArrayList<Object> inputValue = new ArrayList<>();
		boolean flag = true;
		Keyword keyword;
		String keywordName = keywordClass.getName();
		
		inputTypes.add(TestCases.class);
		inputValue.add(testCase);
		
		for(Object input:inputs){
			
			System.out.println(input.getClass().getSimpleName());
			switch(input.getClass().getSimpleName()){
			case "Boolean":
				inputTypes.add(Boolean.TYPE);
				break;
			case "Integer":
				inputTypes.add(Integer.TYPE);
				break;
			case "Float":
				inputTypes.add(Float.TYPE);
				break;
			case "Byte":
				inputTypes.add(Byte.TYPE);
				break;
			case "Double":
				inputTypes.add(Double.TYPE);
				break;
			case "Character":
				inputTypes.add(Character.TYPE);
				break;
			case "Long":
				inputTypes.add(Long.TYPE);
				break;
			default:
				inputTypes.add(input.getClass());
				break;
			}
			
			inputValue.add(input);
		}

		try {

			Constructor<Keyword> constructors = (Constructor<Keyword>) keywordClass.getDeclaredConstructor(inputTypes.toArray(new Class[inputTypes.size()]));
			
			keyword = constructors.newInstance(inputValue.toArray(new Object[inputValue.size()]));
			
			keywordName = keyword.getKeywordName();

			if(testCase.getTestCaseInputs().isRunningOn("Perfecto")){
				if(testCase.getMobileDriver()!=null){
					if(testCase.getPerfectoExecutionContext()!=null){
						testCase.getReportiumClient().testStep(keyword.getKeywordName());
					}
				}
			}

			
			
			if(testCase.getTestCaseInputs().isRunningOn("Perfecto")){
				if(testCase.getMobileDriver()!=null){
					if(testCase.getPerfectoExecutionContext()!=null){
						testCase.getReportiumClient().testStep(keyword.getKeywordName());
					}
				}
			}

			BeforeKeywordParser beforeKeywordParser = new BeforeKeywordParser();
			flag = beforeKeywordParser.parser(testCase, keywordClass, keyword);
			
			if (flag) {
				KeywordStepParser keywordStepParser = new KeywordStepParser();
				flag = flag && keywordStepParser.parser(testCase, keywordClass, keyword);
			}

			AfterKeywordParser afterKeywordParser = new AfterKeywordParser();
			flag = flag && afterKeywordParser.parser(testCase, keywordClass, keyword);

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			
			Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FALSE_POSITIVE,
					"Execute Keyword : Not able to execute Keyword - " + keywordClass.getName() + ". Error Occured - "
							+ e.getMessage());
			flag = false;
		} catch (Exception e) {
			Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FALSE_POSITIVE,
					"Execute Keyword : Not able to execute Keyword - " + keywordClass.getName() + ". Error Occured - "
							+ e.getMessage());
			flag = false;
		} finally {
			
			XMLTestNode testNode = testCase.getXMLTestNode();
			
			if(testNode!=null){
				testNode.getCurrentKeyword().setKeywordSuccess(flag);
				testNode.addKeywordToCurrentKeyword(keywordName);
				testNode.getCurrentKeyword().setKeywordSuccess(flag);
				testNode.addKeywordStatusToCurrentKeyword();
			}
		}

		return flag;
	}

}
