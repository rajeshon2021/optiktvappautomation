package com.honeywell.lyric.utils;

import com.honeywell.commons.coreframework.Keyword;
import com.honeywell.commons.coreframework.TestCaseInputs;
import com.honeywell.commons.coreframework.TestCases;
import com.honeywell.commons.mobile.MobileUtils;

//import com.honeywell.keywords.CHIL.ChangePasswordThroughCHIL;
//import com.honeywell.keywords.lyric.common.MobileUtilsLocal;


public class LyricUtils {



	public static boolean loginToLyricApp(TestCases testCase, TestCaseInputs inputs) {
		boolean flag = true;
//		LoginScreen ls = new LoginScreen(testCase);
//		if (ls.isLoginButtonVisible() && !ls.isEmailAddressTextFieldVisible()) {
//			if (inputs.getInputValue(TestCaseInputs.DEVICE_NAME).contains("iPad")) {
//				flag = flag && ls.clickOnLoginButton(inputs);
//			}else {
//			flag = flag && ls.clickOnLoginButton();
//			}
//		}
		try {
			Thread.sleep(5000);
		}catch(Exception e) {}
		MobileUtils.clickOnElement(testCase, "XPATH","//*[@resource-id='com.optiktv:id/enter_guest_mode']");
		Keyword.ReportStep_Pass(testCase,
				"Tapped on Guest Mode");
		MobileUtils.clickOnElement(testCase, "XPATH","//*[@text='On Demand']");
		Keyword.ReportStep_Pass(testCase,
				"Tapped on demand and entered movie section");
		//MobileUtils.clickOnElement(testCase, "XPATH","//hierarchy/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.view.ViewGroup[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.widget.ScrollView[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[3]/android.widget.TextView[1]");
		//Keyword.ReportStep_Pass(testCase, "Tapped on Movies");
		//MobileUtils.clickOnElement(testCase, "XPATH","//*[@text='18A >");
		//Keyword.ReportStep_Pass(testCase,
			//	"Tapped on 18A Movie icon");
		if (MobileUtils.isMobElementExists("XPATH", "//*[@text='18A']", testCase, 10)) {
			Keyword.ReportStep_Pass(testCase,
					"Displayed 18A");
			
		} else {
			
		}
		return flag;
	}
}