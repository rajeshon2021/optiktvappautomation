package com.demo.keywords.jasper.Login;

import java.io.File ;
import java.io.PrintWriter ;

import com.demo.CHIL.CHILUtil ;
import com.demo.commons.coreframework.AfterKeyword ;
import com.demo.commons.coreframework.BeforeKeyword ;
import com.demo.commons.coreframework.Keyword ;
import com.demo.commons.coreframework.KeywordStep ;
import com.demo.commons.coreframework.SuiteConstants ;
import com.demo.commons.coreframework.SuiteConstants.SuiteConstantTypes ;
import com.demo.commons.coreframework.TestCaseInputs ;
import com.demo.commons.coreframework.TestCases ;
import com.demo.commons.mobile.MobileUtils;
import com.demo.commons.report.FailType ;
import com.demo.lyric.utils.LyricUtils ;

public class LoginToLyric extends Keyword {

	private TestCases testCase;
	private TestCaseInputs inputs;

	public boolean flag = true;

	public LoginToLyric(TestCases testCase, TestCaseInputs inputs) {
		this.inputs = inputs;
		this.testCase = testCase;
	}

	@Override
	@BeforeKeyword
	public boolean preCondition() {
		if (testCase.isTestSuccessful()) {
		try {
			if (inputs.isInputAvailable("COLLECT_LOGS")) {
				if (inputs.getInputValue("COLLECT_LOGS").equalsIgnoreCase("true")) {
					File appiumLogFile = new File(SuiteConstants.getConstantValue(SuiteConstantTypes.PROJECT_SPECIFIC,
							"APPIUM_LOG_FILE_PATH"));
					PrintWriter writer = new PrintWriter(appiumLogFile);
					writer.print("");
					writer.close();
				}
			}
		} catch (Exception e) {
			Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
					"Login To Lyric : Failed to collect logs" + e.getMessage());
			
		}
		try
		{
			@SuppressWarnings("resource")
			CHILUtil chUtil = new CHILUtil(inputs);
			if (chUtil.getConnection()) {
				int result =0;
				String passcodeStatu="";
				if(!MobileUtils.isRunningOnAndroid(testCase)) {
					result = chUtil.TurnoffOnPasscode(inputs,false);
					passcodeStatu="disabled";
				}else {
				 result = chUtil.TurnoffOnPasscode(inputs,true);
				 passcodeStatu="enabled";
				}
				 if (result == 200) {
					Keyword.ReportStep_Pass(testCase,"Activate passcode Using CHIL : Successfully "+passcodeStatu+" passcode using CHIL");
				} else {
					flag = false;
					Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
							"Activate Passcode UsingCHIL : Failed to "+passcodeStatu+" passcode using CHIL");
				}
			}
			chUtil.close();
		}
		catch(Exception e)
		{
			
		}

		} else {
				flag = false;
				Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FUNCTIONAL_FAILURE,
					"Scenario steps failed already, hence skipping the verification");

			}
		return flag;
	}

	@Override
	@KeywordStep(gherkins = "^user in to the Optiktv basic flow$")
	public boolean keywordSteps() {
		if (testCase.isTestSuccessful()) {
		flag = flag && LyricUtils.launchAndLoginToApplication(testCase, inputs);

		} else {
				flag = false;
				Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FUNCTIONAL_FAILURE,
					"Scenario steps failed already, hence skipping the verification");

			}
		return flag;
	}

	@Override
	@AfterKeyword
	public boolean postCondition() {
		return flag;
	}

}
