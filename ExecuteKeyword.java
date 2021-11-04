package com.demo.commons.bddinterface;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.demo.commons.coreframework.AfterKeyword;
import com.demo.commons.coreframework.BeforeKeyword;
import com.demo.commons.coreframework.FrameworkGlobalVariables;
import com.demo.commons.coreframework.Keyword;
import com.demo.commons.coreframework.TestCaseInputs;
import com.demo.commons.coreframework.TestCases;
import com.demo.commons.report.FailType;

public class ExecuteKeyword {

	private static boolean execute(String keywordType, String phrase, String originalPhrase, TestCaseInputs inputs,
			TestCases testCase, ArrayList<String> parameters, DataTable dataTable, boolean doExecute)
			throws KeywordNotFoundException {
		boolean flag = true;
		Method[] methods;
		Keyword marker = null;

		System.out.println("Executing ---->>> " + phrase);

		Class<?> clazz = FrameworkGlobalVariables.mapper.getClassInstance(phrase);

		if (clazz != null) {
			@SuppressWarnings("unchecked")
			Constructor<Keyword>[] constructors = (Constructor<Keyword>[]) clazz.getConstructors();

			for(int index=0;index<constructors.length;++index){
				try {
					if (parameters.size() > 0) {
						if (dataTable != null) {
							if (dataTable.getSize() > 0) {
								marker = constructors[index].newInstance(testCase, inputs, parameters, dataTable);
								break;
							} else {
								marker = constructors[index].newInstance(testCase, inputs, parameters);
								break;
							}

						} else {
							marker = constructors[index].newInstance(testCase, inputs, parameters);
							break;
						}
					} else {
						if (dataTable != null) {
							if (dataTable.getSize() > 0) {
								marker = constructors[index].newInstance(testCase, inputs, dataTable);
								break;
							} else {
								marker = constructors[index].newInstance(testCase, inputs);
								break;
							}

						} else {
							marker = constructors[index].newInstance(testCase, inputs);
							break;
						}
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					
//					System.out.println(e);
//					
//					testCase.getXMLTestNode().addKeywordToCurrentKeyword(keywordType + " : " + phrase);
//
//					Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FRAMEWORK_CONFIGURATION,
//							"Execute Keyword : Not able to execute Step - '" + phrase + "'. Error Occured - "
//									+ e.getMessage());
//					testCase.getXMLTestNode().getCurrentKeyword().setKeywordSuccess(false);
//					flag = false;
//					testCase.getXMLTestNode().setTestSuccess(testCase.getXMLTestNode().isTestSuccess() & flag);
//					testCase.getXMLTestNode().addKeywordStatusToCurrentKeyword();
//					testCase.getXMLTestNode().commitTest(testCase);
				} catch (Exception e) {
//					testCase.getXMLTestNode().addKeywordToCurrentKeyword(keywordType + " : " + phrase);
//					Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FRAMEWORK_CONFIGURATION,
//							"Execute Keyword : Not able to execute Step - '" + phrase + "'. Error Occured - "
//									+ e.getMessage());
//					testCase.getXMLTestNode().getCurrentKeyword().setKeywordSuccess(false);
//					flag = false;
//					testCase.getXMLTestNode().setTestSuccess(testCase.getXMLTestNode().isTestSuccess() & flag);
//					testCase.getXMLTestNode().addKeywordStatusToCurrentKeyword();
//					testCase.getXMLTestNode().commitTest(testCase);
				}
			}
			
			
		} else {
			testCase.getXMLTestNode().addKeywordToCurrentKeyword(keywordType + " : " + phrase);
			Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FRAMEWORK_CONFIGURATION,
					"Execute Keyword : Class not found with phrase - " + phrase);
			testCase.getXMLTestNode().getCurrentKeyword().setKeywordSuccess(false);
			flag = false;
			testCase.getXMLTestNode().setTestSuccess(testCase.getXMLTestNode().isTestSuccess() & flag);
			testCase.getXMLTestNode().addKeywordStatusToCurrentKeyword();
			testCase.getXMLTestNode().commitTest(testCase);
		}

		try {
			if (marker != null) {
				marker.setKeywordName(phrase);
				testCase.getXMLTestNode()
						.addKeywordToCurrentKeyword(keywordType + " : " + phrase + (doExecute ? "" : " [SKIPPED]"));
				if (doExecute) {
					methods = clazz.getMethods();

					for (Method method : methods) {
						if (method.isAnnotationPresent(BeforeKeyword.class)) {
							flag = (boolean) method.invoke(marker);
							testCase.getXMLTestNode().getCurrentKeyword().setKeywordSuccess(flag);
							break;
						}
					}

					if (flag) {
						for (Method method : methods) {
							if (method.isAnnotationPresent(KeywordStep.class)
									||method.isAnnotationPresent(com.demo.commons.coreframework.KeywordStep.class)) {
								String stepGerkins = "";
								KeywordStep stepAnnotation = method.getAnnotation(KeywordStep.class);
								if(stepAnnotation==null){
									com.demo.commons.coreframework.KeywordStep stepAnnotion2 = method.getAnnotation(com.demo.commons.coreframework.KeywordStep.class);
									if(stepAnnotion2 == null){
										
									}else{
										stepGerkins = stepAnnotion2.gherkins().trim();
									}
								}else{
									stepGerkins = stepAnnotation.gherkins().trim();
								}
								
								
								Pattern pattern = Pattern.compile(stepGerkins,Pattern.CASE_INSENSITIVE);
								
								Matcher matcher = pattern.matcher(phrase.trim());
								
								if(matcher.find()){
									// testCase.getXMLTestNode().addKeywordToCurrentKeyword(keywordType
									// + " : " + phrase);
									flag = (boolean) method.invoke(marker);
									testCase.getXMLTestNode().getCurrentKeyword().setKeywordSuccess(flag);
									break;
								}
							}
						}
					} else {
						testCase.getXMLTestNode().getCurrentKeyword().setKeywordSuccess(false);
					}

					for (Method method : methods) {
						if (method.isAnnotationPresent(AfterKeyword.class)) {
							flag = (boolean) method.invoke(marker) & flag;
							testCase.getXMLTestNode().getCurrentKeyword().setKeywordSuccess(flag);
							break;
						}
					}
				} else {
					flag = false;
				}

			}else{
				Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FRAMEWORK_CONFIGURATION,
						"Execute Keyword : Not able to execute Step - '" + phrase + "'. Wrong number of arguments.");
				flag = false;
			}

		} catch (Exception e) {
			Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FRAMEWORK_CONFIGURATION,
					"Execute Keyword : Not able to execute Step - '" + phrase + "'. Error Occured - " + e.getMessage());
			flag = false;
		} finally {
			if (flag) {
			} else {
				testCase.getXMLTestNode().getCurrentKeyword().setKeywordSuccess(false);
			}
			testCase.getXMLTestNode().setTestSuccess(testCase.getXMLTestNode().isTestSuccess() & flag);
			testCase.getXMLTestNode().addKeywordStatusToCurrentKeyword();
			testCase.getXMLTestNode().commitTest(testCase);
		}

		return flag;
	}

	public static boolean executeWhen(String keywordType, String phrase, String originalPhrase, TestCaseInputs inputs,
			TestCases testCase, ArrayList<String> parameters, DataTable dataTable, boolean doExecute)
			throws KeywordNotFoundException {

		testCase.keywordType = BDDConstants.WHEN;
		return ExecuteKeyword.execute(keywordType, phrase, originalPhrase, inputs, testCase, parameters,
				dataTable.getSize() > 0 ? dataTable : null, true);
	}

	public static boolean executeThen(String keywordType, String phrase, String originalPhrase, TestCaseInputs inputs,
			TestCases testCase, ArrayList<String> parameters, DataTable dataTable, boolean doExecute)
			throws KeywordNotFoundException {

		testCase.keywordType = BDDConstants.THEN;
		return ExecuteKeyword.execute(keywordType, phrase, originalPhrase, inputs, testCase, parameters,
				dataTable.getSize() > 0 ? dataTable : null, true);
	}

	public static boolean executeGiven(String keywordType, String phrase, String originalPhrase, TestCaseInputs inputs,
			TestCases testCase, ArrayList<String> parameters, DataTable dataTable, boolean doExecute)
			throws KeywordNotFoundException {

		return ExecuteKeyword.execute(keywordType, phrase, originalPhrase, inputs, testCase, parameters,
				dataTable.getSize() > 0 ? dataTable : null, true);
	}

	public static boolean executeAnd(String keywordType, String phrase, String originalPhrase, TestCaseInputs inputs,
			TestCases testCase, ArrayList<String> parameters, DataTable dataTable, boolean doExecute)
			throws KeywordNotFoundException {

		return ExecuteKeyword.execute(keywordType, phrase, originalPhrase, inputs, testCase, parameters,
				dataTable.getSize() > 0 ? dataTable : null, true);

		// if ((testCase.getXMLTestNode().isTestSuccess())) {
		//
		// return ExecuteKeyword.execute(
		// keywordType, phrase, originalPhrase, inputs, testCase, parameters,
		// dataTable.getSize() > 0 ? dataTable : null, true);
		// } else {
		// if(testCase.keywordType!=null){
		// if (testCase.keywordType.equalsIgnoreCase(BDDConstants.THEN)) {
		//
		// }else{
		// return false;
		// }
		// }else{
		// return false;
		// }
		// }

	}

}
